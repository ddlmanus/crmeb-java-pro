package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.finance.AssetAssessmentBreeding;
import com.zbkj.common.model.finance.FarmInstitution;
import com.zbkj.common.model.user.User;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.finance.UserCreditInfoVO;
import com.zbkj.service.service.UserCreditService;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.finance.AssetAssessmentBreedingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户授信服务实现类
 * 统一管理所有用户类型的授信相关操作
 */
@Slf4j
@Service
public class UserCreditServiceImpl implements UserCreditService {

    @Autowired
    private UserService userService;

    @Autowired
    private AssetAssessmentBreedingService assetAssessmentBreedingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean initUserCreditInfo(Integer userId, FarmInstitution farmInstitution) {
        log.info("开始初始化用户{}的授信信息，养殖场：{}", userId, farmInstitution.getFarmName());
        
        User user = userService.getById(userId);
        if (user == null) {
            throw new CrmebException("用户不存在");
        }

        // 计算养殖相关信息
        CreditInfo creditInfo = calculateCreditInfo(farmInstitution);
        
        // 更新用户的授信基础信息（不设置授信额度，等待审批）
        user.setAssessmentAmount(creditInfo.getAssessmentAmount());
        user.setCreditCoefficient(creditInfo.getCreditCoefficient());
        user.setTotalStockQuantity(creditInfo.getTotalStockQuantity());
        user.setBreedingVarietyNames(creditInfo.getBreedingVarietyNames());
        user.setBreedingVarietyTypes(creditInfo.getBreedingVarietyTypes());
        user.setCreditLevel(3); // 员工层级
        user.setCreditStatus(0); // 未授信状态
        user.setUpdateTime(new Date());
        
        boolean result = userService.updateById(user);
        
        log.info("用户{}授信信息初始化{}，评估金额：{}，授信系数：{}，总存栏量：{}，养殖品种：{}，品种类型：{}", 
                userId, result ? "成功" : "失败", creditInfo.getAssessmentAmount(),
                creditInfo.getCreditCoefficient(), creditInfo.getTotalStockQuantity(), 
                creditInfo.getBreedingVarietyNames(), creditInfo.getBreedingVarietyTypes());
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean allocateCredit(Integer fromUserId, Integer toUserId, BigDecimal amount, Integer auditUserId, String remark) {
        log.info("开始分配授信额度：从用户{}给用户{}分配{}，审核人：{}", fromUserId, toUserId, amount, auditUserId);
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CrmebException("分配金额必须大于0");
        }

        // 获取分配方用户信息
        User fromUser = userService.getById(fromUserId);
        if (fromUser == null) {
            throw new CrmebException("分配方用户不存在");
        }

        // 获取接收方用户信息
        User toUser = userService.getById(toUserId);
        if (toUser == null) {
            throw new CrmebException("接收方用户不存在");
        }

        // 检查分配方剩余额度
        BigDecimal fromRemainingCredit = fromUser.getRemainingCredit() != null ? fromUser.getRemainingCredit() : BigDecimal.ZERO;
        if (fromRemainingCredit.compareTo(amount) < 0) {
            throw new CrmebException("分配失败：上级用户剩余额度不足，当前剩余：" + fromRemainingCredit);
        }

        // 检查授信层级关系
        if (!isValidCreditHierarchy(fromUser, toUser)) {
            throw new CrmebException("分配失败：授信层级关系不正确");
        }

        // 减少分配方的剩余额度
        fromUser.setRemainingCredit(fromRemainingCredit.subtract(amount));
        fromUser.setUsedCredit((fromUser.getUsedCredit() != null ? fromUser.getUsedCredit() : BigDecimal.ZERO).add(amount));
        fromUser.setLastCreditAuditTime(new Date());
        fromUser.setLastCreditAuditUserId(auditUserId);
        fromUser.setUpdateTime(new Date());

        // 增加接收方的授信额度
        toUser.setCreditLimit((toUser.getCreditLimit() != null ? toUser.getCreditLimit() : BigDecimal.ZERO).add(amount));
        toUser.setRemainingCredit((toUser.getRemainingCredit() != null ? toUser.getRemainingCredit() : BigDecimal.ZERO).add(amount));
        toUser.setCreditStatus(1); // 设置为已授信
        toUser.setCreditStartTime(toUser.getCreditStartTime() == null ? new Date() : toUser.getCreditStartTime());
        toUser.setParentCreditUserId(fromUserId);
        toUser.setLastCreditAuditTime(new Date());
        toUser.setLastCreditAuditUserId(auditUserId);
        toUser.setCreditAuditRemark(remark);
        toUser.setUpdateTime(new Date());

        // 更新数据库
        boolean fromResult = userService.updateById(fromUser);
        boolean toResult = userService.updateById(toUser);

        boolean result = fromResult && toResult;
        log.info("授信额度分配{}，分配金额：{}", result ? "成功" : "失败", amount);
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean useCredit(Integer userId, BigDecimal amount, String businessType, String businessId) {
        log.info("用户{}使用授信额度：{}，业务类型：{}，业务ID：{}", userId, amount, businessType, businessId);
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CrmebException("使用金额必须大于0");
        }

        User user = userService.getById(userId);
        if (user == null) {
            throw new CrmebException("用户不存在");
        }

        // 检查授信状态
        if (user.getCreditStatus() == null || user.getCreditStatus() != 1) {
            throw new CrmebException("用户未获得授信或授信状态异常");
        }

        // 检查剩余额度
        BigDecimal remainingCredit = user.getRemainingCredit() != null ? user.getRemainingCredit() : BigDecimal.ZERO;
        if (remainingCredit.compareTo(amount) < 0) {
            throw new CrmebException("剩余授信额度不足，当前剩余：" + remainingCredit);
        }

        // 更新用户授信信息
        user.setRemainingCredit(remainingCredit.subtract(amount));
        user.setUsedCredit((user.getUsedCredit() != null ? user.getUsedCredit() : BigDecimal.ZERO).add(amount));
        user.setPendingRepayment((user.getPendingRepayment() != null ? user.getPendingRepayment() : BigDecimal.ZERO).add(amount));
        user.setUpdateTime(new Date());

        boolean result = userService.updateById(user);
        log.info("用户{}使用授信额度{}，金额：{}", userId, result ? "成功" : "失败", amount);
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean repayCredit(Integer userId, BigDecimal amount, String businessType, String businessId) {
        log.info("用户{}还款：{}，业务类型：{}，业务ID：{}", userId, amount, businessType, businessId);
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CrmebException("还款金额必须大于0");
        }

        User user = userService.getById(userId);
        if (user == null) {
            throw new CrmebException("用户不存在");
        }

        // 检查待还款金额
        BigDecimal pendingRepayment = user.getPendingRepayment() != null ? user.getPendingRepayment() : BigDecimal.ZERO;
        if (pendingRepayment.compareTo(amount) < 0) {
            throw new CrmebException("还款金额超出待还款金额，当前待还款：" + pendingRepayment);
        }

        // 更新用户授信信息
        user.setPendingRepayment(pendingRepayment.subtract(amount));
        user.setRemainingCredit((user.getRemainingCredit() != null ? user.getRemainingCredit() : BigDecimal.ZERO).add(amount));
        user.setUsedCredit((user.getUsedCredit() != null ? user.getUsedCredit() : BigDecimal.ZERO).subtract(amount));
        user.setUpdateTime(new Date());

        boolean result = userService.updateById(user);
        log.info("用户{}还款{}，金额：{}", userId, result ? "成功" : "失败", amount);
        
        return result;
    }

    @Override
    public UserCreditInfoVO getUserCreditInfo(Integer userId) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new CrmebException("用户不存在");
        }

        UserCreditInfoVO creditInfo = new UserCreditInfoVO();
        BeanUtils.copyProperties(user, creditInfo);
        
        // 设置用户ID
        creditInfo.setUserId(user.getId());
        
        // 设置文本字段
        creditInfo.setUserTypeText(getUserTypeText(user.getUserType()));
        creditInfo.setCreditLevelText(getCreditLevelText(user.getCreditLevel()));
        creditInfo.setCreditStatusText(getCreditStatusText(user.getCreditStatus()));
        
        // 获取上级用户姓名
        if (user.getParentCreditUserId() != null) {
            User parentUser = userService.getById(user.getParentCreditUserId());
            if (parentUser != null) {
                creditInfo.setParentUserName(parentUser.getRealName());
            }
        }
        
        // 获取审核人姓名
        if (user.getLastCreditAuditUserId() != null) {
            User auditUser = userService.getById(user.getLastCreditAuditUserId());
            if (auditUser != null) {
                creditInfo.setAuditUserName(auditUser.getRealName());
            }
        }

        return creditInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean freezeCredit(Integer userId, String reason, Integer operatorId) {
        log.info("冻结用户{}授信，原因：{}，操作人：{}", userId, reason, operatorId);
        
        User user = userService.getById(userId);
        if (user == null) {
            throw new CrmebException("用户不存在");
        }

        if (user.getCreditStatus() != 1) {
            throw new CrmebException("用户当前状态不能冻结");
        }

        user.setCreditStatus(2); // 授信冻结
        user.setLastCreditAuditTime(new Date());
        user.setLastCreditAuditUserId(operatorId);
        user.setCreditAuditRemark(reason);
        user.setUpdateTime(new Date());

        boolean result = userService.updateById(user);
        log.info("用户{}授信冻结{}", userId, result ? "成功" : "失败");
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unfreezeCredit(Integer userId, String reason, Integer operatorId) {
        log.info("解冻用户{}授信，原因：{}，操作人：{}", userId, reason, operatorId);
        
        User user = userService.getById(userId);
        if (user == null) {
            throw new CrmebException("用户不存在");
        }

        if (user.getCreditStatus() != 2) {
            throw new CrmebException("用户当前状态不能解冻");
        }

        user.setCreditStatus(1); // 已授信
        user.setLastCreditAuditTime(new Date());
        user.setLastCreditAuditUserId(operatorId);
        user.setCreditAuditRemark(reason);
        user.setUpdateTime(new Date());

        boolean result = userService.updateById(user);
        log.info("用户{}授信解冻{}", userId, result ? "成功" : "失败");
        
        return result;
    }

    @Override
    public Boolean checkCreditSufficient(Integer userId, BigDecimal amount) {
        BigDecimal availableCredit = getAvailableCredit(userId);
        return availableCredit != null && availableCredit.compareTo(amount) >= 0;
    }

    @Override
    public BigDecimal getAvailableCredit(Integer userId) {
        User user = userService.getById(userId);
        if (user == null || user.getCreditStatus() != 1) {
            return BigDecimal.ZERO;
        }
        return user.getRemainingCredit() != null ? user.getRemainingCredit() : BigDecimal.ZERO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserBreedingInfo(Integer userId) {
        log.info("开始更新用户{}的养殖信息", userId);
        
        User user = userService.getById(userId);
        if (user == null) {
            throw new CrmebException("用户不存在");
        }

        if (StrUtil.isBlank(user.getFarmCode())) {
            log.warn("用户{}没有养殖场编码，无法更新养殖信息", userId);
            return false;
        }

        // 根据养殖场编码查询资产评估数据
        LambdaQueryWrapper<AssetAssessmentBreeding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetAssessmentBreeding::getFarmCode, user.getFarmCode());
        wrapper.orderByDesc(AssetAssessmentBreeding::getCreateTime);
        
        List<AssetAssessmentBreeding> breedingList = assetAssessmentBreedingService.list(wrapper);
        
        if (breedingList.isEmpty()) {
            log.warn("用户{}的养殖场（编码：{}）没有资产评估数据", userId, user.getFarmCode());
            return false;
        }

        // 计算汇总信息
        BigDecimal totalAssessmentAmount = breedingList.stream()
            .map(AssetAssessmentBreeding::getAssessmentTotalPrice)
            .filter(amount -> amount != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal totalCreditCoefficient = breedingList.stream()
            .map(AssetAssessmentBreeding::getCreditCoefficient)
            .filter(coefficient -> coefficient != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Integer totalStockQuantity = breedingList.stream()
            .map(AssetAssessmentBreeding::getStockQuantity)
            .filter(quantity -> quantity != null)
            .mapToInt(Integer::intValue)
            .sum();
        
        String breedingVarietyNames = breedingList.stream()
            .map(AssetAssessmentBreeding::getBreedName)
            .filter(name -> name != null && !name.trim().isEmpty())
            .distinct()
            .collect(Collectors.joining("、"));
        
        String breedingVarietyTypes = breedingList.stream()
            .map(AssetAssessmentBreeding::getBreedType)
            .filter(type -> type != null && !type.trim().isEmpty())
            .distinct()
            .collect(Collectors.joining("、"));

        // 更新用户信息
        user.setAssessmentAmount(totalAssessmentAmount);
        user.setCreditCoefficient(totalCreditCoefficient);
        user.setTotalStockQuantity(totalStockQuantity);
        user.setBreedingVarietyNames(breedingVarietyNames);
        user.setBreedingVarietyTypes(breedingVarietyTypes);
        user.setUpdateTime(new Date());

        boolean result = userService.updateById(user);
        log.info("用户{}养殖信息更新{}，评估金额：{}，授信系数：{}，总存栏量：{}，养殖品种：{}，品种类型：{}", 
                userId, result ? "成功" : "失败", totalAssessmentAmount, totalCreditCoefficient, 
                totalStockQuantity, breedingVarietyNames, breedingVarietyTypes);
        
        return result;
    }

    /**
     * 计算养殖场的授信信息
     */
    private CreditInfo calculateCreditInfo(FarmInstitution farmInstitution) {
        CreditInfo creditInfo = new CreditInfo();
        
        try {
            // 根据养殖场编码查询资产评估数据
            LambdaQueryWrapper<AssetAssessmentBreeding> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AssetAssessmentBreeding::getFarmCode, farmInstitution.getFarmCode());
            wrapper.orderByDesc(AssetAssessmentBreeding::getCreateTime);
            
            List<AssetAssessmentBreeding> breedingList = assetAssessmentBreedingService.list(wrapper);
            
            if (breedingList.isEmpty()) {
                log.warn("养殖场{}（编码：{}）暂无资产评估数据，使用默认值", 
                        farmInstitution.getFarmName(), farmInstitution.getFarmCode());
                return creditInfo;
            }

            // 汇总计算
            BigDecimal totalAssessmentAmount = breedingList.stream()
                .map(AssetAssessmentBreeding::getAssessmentTotalPrice)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            BigDecimal totalCreditCoefficient = breedingList.stream()
                .map(AssetAssessmentBreeding::getCreditCoefficient)
                .filter(coefficient -> coefficient != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            Integer totalStockQuantity = breedingList.stream()
                .map(AssetAssessmentBreeding::getStockQuantity)
                .filter(quantity -> quantity != null)
                .mapToInt(Integer::intValue)
                .sum();
            
            String breedingVarietyNames = breedingList.stream()
                .map(AssetAssessmentBreeding::getBreedName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .distinct()
                .collect(Collectors.joining("、"));
            
            String breedingVarietyTypes = breedingList.stream()
                .map(AssetAssessmentBreeding::getBreedType)
                .filter(type -> type != null && !type.trim().isEmpty())
                .distinct()
                .collect(Collectors.joining("、"));

            creditInfo.setAssessmentAmount(totalAssessmentAmount);
            creditInfo.setCreditCoefficient(totalCreditCoefficient);
            creditInfo.setTotalStockQuantity(totalStockQuantity);
            creditInfo.setBreedingVarietyNames(breedingVarietyNames);
            creditInfo.setBreedingVarietyTypes(breedingVarietyTypes);
            
            log.info("养殖场{}授信信息计算完成，评估金额：{}，授信系数：{}，总存栏量：{}，养殖品种：{}，品种类型：{}", 
                    farmInstitution.getFarmName(), totalAssessmentAmount, totalCreditCoefficient, 
                    totalStockQuantity, breedingVarietyNames, breedingVarietyTypes);
            
        } catch (Exception e) {
            log.error("计算养殖场{}授信信息时发生异常：{}", farmInstitution.getFarmName(), e.getMessage(), e);
        }
        
        return creditInfo;
    }

    /**
     * 检查授信层级关系是否有效
     */
    private boolean isValidCreditHierarchy(User fromUser, User toUser) {
        if (fromUser.getCreditLevel() == null || toUser.getCreditLevel() == null) {
            return false;
        }
        
        // 上级授信层级必须小于下级（层级数字越小权限越高）
        return fromUser.getCreditLevel() < toUser.getCreditLevel();
    }

    /**
     * 获取用户类型文本
     */
    private String getUserTypeText(Integer userType) {
        if (userType == null) return "未知";
        switch (userType) {
            case 0: return "游客";
            case 1: return "管理员";
            case 2: return "员工";
            default: return "未知";
        }
    }

    /**
     * 获取授信层级文本
     */
    private String getCreditLevelText(Integer creditLevel) {
        if (creditLevel == null) return "未知";
        switch (creditLevel) {
            case 1: return "平台";
            case 2: return "合作社";
            case 3: return "员工";
            default: return "未知";
        }
    }

    /**
     * 获取授信状态文本
     */
    private String getCreditStatusText(Integer creditStatus) {
        if (creditStatus == null) return "未知";
        switch (creditStatus) {
            case 0: return "未授信";
            case 1: return "已授信";
            case 2: return "授信冻结";
            case 3: return "授信失效";
            default: return "未知";
        }
    }

    /**
     * 授信信息内部类
     */
    private static class CreditInfo {
        private BigDecimal assessmentAmount = BigDecimal.ZERO;
        private BigDecimal creditCoefficient = BigDecimal.ZERO;
        private Integer totalStockQuantity = 0;
        private String breedingVarietyNames = "";
        private String breedingVarietyTypes = "";

        public BigDecimal getAssessmentAmount() {
            return assessmentAmount;
        }

        public void setAssessmentAmount(BigDecimal assessmentAmount) {
            this.assessmentAmount = assessmentAmount;
        }

        public BigDecimal getCreditCoefficient() {
            return creditCoefficient;
        }

        public void setCreditCoefficient(BigDecimal creditCoefficient) {
            this.creditCoefficient = creditCoefficient;
        }

        public Integer getTotalStockQuantity() {
            return totalStockQuantity;
        }

        public void setTotalStockQuantity(Integer totalStockQuantity) {
            this.totalStockQuantity = totalStockQuantity;
        }

        public String getBreedingVarietyNames() {
            return breedingVarietyNames;
        }

        public void setBreedingVarietyNames(String breedingVarietyNames) {
            this.breedingVarietyNames = breedingVarietyNames;
        }

        public String getBreedingVarietyTypes() {
            return breedingVarietyTypes;
        }

        public void setBreedingVarietyTypes(String breedingVarietyTypes) {
            this.breedingVarietyTypes = breedingVarietyTypes;
        }
    }
} 