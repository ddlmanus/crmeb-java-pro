package com.zbkj.service.service.impl.finance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.enums.AuditStatus;
import com.zbkj.common.enums.FarmTypeEnum;
import com.zbkj.common.enums.UserTypeEnum;
import com.zbkj.common.model.finance.CreditPaymentOrder;
import com.zbkj.common.model.finance.CreditTransaction;
import com.zbkj.common.model.finance.GuarantorInfo;
import com.zbkj.common.model.finance.RepaymentRecord;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.finance.*;
import com.zbkj.service.dao.finance.CreditTransactionDao;
import com.zbkj.service.dao.finance.GuarantorInfoDao;
import com.zbkj.service.dao.finance.RepaymentRecordDao;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.finance.CreditPaymentOrderService;
import com.zbkj.service.service.finance.MutualAidService;
import com.zbkj.service.service.finance.RepaymentRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MutualAidServiceImpl implements MutualAidService {

    @Resource
    private UserService userService;

    @Resource
    private GuarantorInfoDao guarantorInfoDao;

    @Resource
    private RepaymentRecordDao repaymentRecordDao;

    @Resource
    private CreditTransactionDao creditTransactionDao;
    @Resource
    private CreditPaymentOrderService creditPaymentOrderService;

    @Override
    public IPage<MutualAidMemberVO> getMembersList(MutualAidMemberPageRequest pageRequest) {
        log.info("开始查询社员互助金列表，请求参数：{}", pageRequest);
        
        // 获取当前用户
        User currentUser = userService.getInfo();
        if (currentUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        log.info("当前用户ID: {}, 用户类型: {}, 养殖户类型: {}, 机构ID: {}", 
                currentUser.getId(), currentUser.getUserType(), currentUser.getFarmType(), currentUser.getOrganizationId());
        
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 根据用户类型决定查询范围
        if (currentUser.getFarmType() != null && currentUser.getFarmType() == 2) {
            // 合作社用户：查询该合作社下的所有员工
            if (StringUtils.hasText(currentUser.getOrganizationId())&&currentUser.getUserType().equals(UserTypeEnum.USER_TYPE_ADMIN.getCode())) {
                queryWrapper.eq(User::getOrganizationId, currentUser.getOrganizationId());
                log.info("合作社用户，查询机构ID为 {} 的所有员工", currentUser.getOrganizationId());
            } else {
                // 如果合作社用户没有机构ID，只查询自己
                queryWrapper.eq(User::getId, currentUser.getId());
                log.info("合作社用户但无机构ID，只查询自己");
            }
        } else {
            // 养殖户：只查询自己
            queryWrapper.eq(User::getId, currentUser.getId());
            log.info("养殖户用户，只查询自己");
        }
        
        // 只查询正常状态的用户
        queryWrapper.eq(User::getStatus, true);
        queryWrapper.eq(User::getIsLogoff, false);
        
        // 添加搜索条件
        if (StringUtils.hasText(pageRequest.getMemberName())) {
            queryWrapper.and(wrapper -> wrapper
                .like(User::getRealName, pageRequest.getMemberName())
                .or()
                .like(User::getNickname, pageRequest.getMemberName())
            );
        }
        
        if (StringUtils.hasText(pageRequest.getMemberAccount())) {
            queryWrapper.and(wrapper -> wrapper
                .like(User::getAccount, pageRequest.getMemberAccount())
                .or()
                .like(User::getPhone, pageRequest.getMemberAccount())
            );
        }
        
        // 分页查询
        Page<User> page = new Page<>(pageRequest.getPageNo(), pageRequest.getPageSize());
        IPage<User> userPage = userService.page(page, queryWrapper);
        
        log.info("查询到 {} 条用户记录", userPage.getRecords().size());
        
        // 转换为VO
        List<MutualAidMemberVO> memberVOList = userPage.getRecords().stream().map(user -> {
            MutualAidMemberVO memberVO = new MutualAidMemberVO();
            
            // 基本信息
            memberVO.setUserId(user.getId());
            memberVO.setName(StringUtils.hasText(user.getRealName()) ? user.getRealName() : user.getNickname());
            memberVO.setAccount(user.getAccount());
            memberVO.setMobile(user.getPhone());
            
            // 授信相关信息
            memberVO.setTotalPrice(user.getCreditLimit());
            memberVO.setBalance(user.getRemainingCredit());
            memberVO.setFreezePrice(user.getUsedCredit());
            memberVO.setEstimatedPrice(user.getAssessmentAmount());
            memberVO.setEstimatedYield(user.getApplyAmount());
            memberVO.setAuditStatus(user.getCreditStatus());
            memberVO.setAuditReason(user.getCreditAuditRemark());
            memberVO.setAvailable(user.getCreditStatus() != null && user.getCreditStatus() == 1 ? 1 : 0);
            memberVO.setStep(1);
            memberVO.setFarmCode(user.getFarmCode());
            memberVO.setFarmName(user.getFarmName());
            memberVO.setOrganizationId(user.getOrganizationId());
            memberVO.setOrganizationName(user.getOrganizationName());
            memberVO.setOrganizationCode(user.getOrganizationCode());
            memberVO.setProvinceId(user.getProvinceId());
            memberVO.setCityId(user.getCityId());
            memberVO.setDistrictId(user.getDistrictId());
            memberVO.setUserType(user.getUserType());
            memberVO.setFarmType(user.getFarmType());
            memberVO.setCreateTime(user.getCreateTime());
            memberVO.setProvince(user.getProvince());
            memberVO.setCity(user.getCity());
            memberVO.setDistrict(user.getDistrict());
            memberVO.setTotalExpense(user.getUsedCredit());
            memberVO.setTotalRecharge(user.getNowMoney());
            memberVO.setTermDays(user.getRepaymentDays());
            memberVO.setPremium(user.getCreditRatio());
            memberVO.setCardNo(user.getCardNumber());
            memberVO.setPredictedAmount(user.getApplyAmount());
            
            // 时间信息
            memberVO.setCreateTime(user.getCreateTime());
            memberVO.setUpdateTime(user.getUpdateTime());
            if (user.getCreateTime() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                memberVO.setCreateTimeClone(sdf.format(user.getCreateTime()));
            }
            
            return memberVO;
        }).collect(Collectors.toList());
        
        // 构建分页结果
        Page<MutualAidMemberVO> resultPage = new Page<>(pageRequest.getPageNo(), pageRequest.getPageSize());
        resultPage.setRecords(memberVOList);
        resultPage.setTotal(userPage.getTotal());
        resultPage.setPages(userPage.getPages());
        
        log.info("社员互助金列表查询完成，返回 {} 条记录", memberVOList.size());
        return resultPage;
    }

    @Override
    public Boolean setCreditTerm(CreditTermSettingVO request) {
        log.info("开始设置授信条件，请求参数：{}", request);
        
        // 获取当前用户
        User currentUser = userService.getInfo();
        if (currentUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 获取要设置的用户
        User targetUser = userService.getById(request.getId());
        if (targetUser == null) {
            throw new RuntimeException("目标用户不存在");
        }
        
        // 权限检查：只有合作社可以设置员工的授信条件，或者用户设置自己的条件
        if (!currentUser.getId().equals(targetUser.getId())) {
            // 如果不是设置自己的条件，需要检查权限
            if (currentUser.getFarmType() == null || currentUser.getFarmType() != 2) {
                throw new RuntimeException("无权限设置其他用户的授信条件");
            }
            
            // 检查目标用户是否属于当前合作社
            if (!StringUtils.hasText(currentUser.getOrganizationId()) || 
                !currentUser.getOrganizationId().equals(targetUser.getOrganizationId())) {
                throw new RuntimeException("只能设置本合作社员工的授信条件");
            }
        }
        
        // 更新授信条件
        targetUser.setCreditRatio(request.getPremium());
        targetUser.setRepaymentDays(request.getTermDays());
        
        boolean result = userService.updateById(targetUser);
        
        log.info("授信条件设置完成，用户ID: {}, 利率: {}, 期限: {} 天, 结果: {}", 
                request.getId(), request.getPremium(), request.getTermDays(), result);
        
        return result;
    }

    @Override
    public List<GuarantorVO> getGuarantorsByUserId(Integer userId) {
        log.info("开始查询用户担保人列表，用户ID: {}", userId);
        
        // 查询担保人信息
        LambdaQueryWrapper<GuarantorInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GuarantorInfo::getUserId, userId);
        queryWrapper.orderByDesc(GuarantorInfo::getCreateTime);
        
        List<GuarantorInfo> guarantorInfoList = guarantorInfoDao.selectList(queryWrapper);
        
        // 转换为VO
        List<GuarantorVO> guarantorVOList = guarantorInfoList.stream().map(guarantorInfo -> {
            GuarantorVO guarantorVO = new GuarantorVO();
            guarantorVO.setId(guarantorInfo.getId());
            guarantorVO.setUserId(guarantorInfo.getUserId());
            guarantorVO.setName(guarantorInfo.getName());
            guarantorVO.setMobile(guarantorInfo.getMobile());
            guarantorVO.setIdCard(guarantorInfo.getIdNumber());
            guarantorVO.setSignature(guarantorInfo.getSignature());
            guarantorVO.setCreateTime(guarantorInfo.getCreateTime());
            guarantorVO.setUpdateTime(guarantorInfo.getUpdateTime());
            return guarantorVO;
        }).collect(Collectors.toList());
        
        log.info("查询到 {} 条担保人记录", guarantorVOList.size());
        return guarantorVOList;
    }

    @Override
    public Boolean addGuarantor(EmployeeGuarantorRequestVO request) {
        log.info("开始添加担保人，请求参数：{}", request);
        
        // 检查该用户的担保人数量（最多2个）
        LambdaQueryWrapper<GuarantorInfo> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(GuarantorInfo::getUserId, request.getUserId());
        long count = guarantorInfoDao.selectCount(countWrapper);
        
        if (count >= 2) {
            throw new RuntimeException("每个用户最多只能添加2个担保人");
        }
        if(CollectionUtils.isEmpty(request.getGuarantors())){
            throw new RuntimeException("请添加担保人");
        }
        request.getGuarantors().stream().forEach(x->{
            GuarantorInfo guarantorInfo=new GuarantorInfo();
            guarantorInfo.setUserId(request.getUserId());
            guarantorInfo.setName(x.getName());
            guarantorInfo.setMobile(x.getMobile());
            guarantorInfo.setIdNumber(x.getIdCard());
            guarantorInfo.setSignature(x.getSignature());
            guarantorInfo.setCreateTime(new Date());
            guarantorInfoDao.insert(guarantorInfo);
        });
        return true;
    }

    @Override
    public IPage<RepaymentRecordVO> getRepaymentRecords(MutualAidMemberPageRequest pageRequest) {
        log.info("开始查询还款记录，请求参数：{}", pageRequest);
        
        // 构建查询条件
        LambdaQueryWrapper<RepaymentRecord> queryWrapper = new LambdaQueryWrapper<>();
        User info = userService.getInfo();
        if(!Objects.isNull(info)){
            //判断是合作社管理员还是社员
            if(info.getFarmType().equals(FarmTypeEnum.TENANT_TYPE_COOPERATIVE.getCode())&&info.getUserType().equals(UserTypeEnum.USER_TYPE_ADMIN.getCode())){
                queryWrapper.eq(RepaymentRecord::getOrganizationId, info.getOrganizationId());
            }else {
                queryWrapper.eq(RepaymentRecord::getUserId, info.getId());
            }
        }
        if(!Objects.isNull(pageRequest.getRepaymentStatus())){
            queryWrapper.eq(RepaymentRecord::getRepaymentStatus, pageRequest.getRepaymentStatus());
        }
        queryWrapper.eq(RepaymentRecord::getDeleteFlag, 0);
        queryWrapper.orderByDesc(RepaymentRecord::getRepaymentTime);
        
        // 分页查询
        Page<RepaymentRecord> page = new Page<>(pageRequest.getPageNo(), pageRequest.getPageSize());
        IPage<RepaymentRecord> repaymentPage = repaymentRecordDao.selectPage(page, queryWrapper);
        
        // 转换为VO
        List<RepaymentRecordVO> repaymentVOList = repaymentPage.getRecords().stream().map(repaymentRecord -> {
            RepaymentRecordVO repaymentVO = new RepaymentRecordVO();
            BeanUtils.copyProperties(repaymentRecord, repaymentVO);
            repaymentVO.setId(repaymentRecord.getId());
            repaymentVO.setMemberName(repaymentRecord.getUserName());
            repaymentVO.setMemberAccount(repaymentRecord.getUserId().toString());
            repaymentVO.setVoucherUrl(repaymentRecord.getRepaymentProof());
            
            // 格式化时间
            if (repaymentRecord.getRepaymentTime() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
                repaymentVO.setRepaymentTimeClone(sdf.format(repaymentRecord.getRepaymentTime()));
            }
            
            return repaymentVO;
        }).collect(Collectors.toList());
        
        // 构建分页结果
        Page<RepaymentRecordVO> resultPage = new Page<>(pageRequest.getPageNo(), pageRequest.getPageSize());
        resultPage.setRecords(repaymentVOList);
        resultPage.setTotal(repaymentPage.getTotal());
        resultPage.setPages(repaymentPage.getPages());
        
        log.info("查询到 {} 条还款记录", repaymentVOList.size());
        return resultPage;
    }

    @Override
    public IPage<TransactionRecordVO> getTransactionRecords(MutualAidMemberPageRequest pageRequest) {
        log.info("开始查询交易记录，请求参数：{}", pageRequest);
        User info = userService.getInfo();
        // 构建查询条件
        LambdaQueryWrapper<CreditTransaction> queryWrapper = new LambdaQueryWrapper<>();
        
        // 根据用户ID查询
        if(info.getFarmType().equals(FarmTypeEnum.TENANT_TYPE_COOPERATIVE.getCode())&&info.getUserType().equals(UserTypeEnum.USER_TYPE_ADMIN.getCode())) {
            queryWrapper.eq(CreditTransaction::getOrganizationId, info.getOrganizationId());
        }else{
          queryWrapper.eq(CreditTransaction::getUserId, info.getId());
        }
        
        queryWrapper.eq(CreditTransaction::getDeleteFlag, 0);
        queryWrapper.orderByDesc(CreditTransaction::getTransactionTime);
        
        // 分页查询
        Page<CreditTransaction> page = new Page<>(pageRequest.getPageNo(), pageRequest.getPageSize());
        IPage<CreditTransaction> transactionPage = creditTransactionDao.selectPage(page, queryWrapper);
        
        // 转换为VO
        List<TransactionRecordVO> transactionVOList = transactionPage.getRecords().stream().map(transaction -> {
            TransactionRecordVO transactionVO = new TransactionRecordVO();
            transactionVO.setId(transaction.getId());
            transactionVO.setOrderNo(transaction.getOrderSn());
            transactionVO.setUserId(Integer.valueOf(transaction.getUserId()));
            transactionVO.setUserName(transaction.getUserName());
            transactionVO.setUserAccount(transaction.getUserId());
            transactionVO.setTransactionType(transaction.getTransactionType());
            transactionVO.setAmount(transaction.getTransactionAmount());
            transactionVO.setBalanceBefore(transaction.getBeforeAvailableAmount());
            transactionVO.setBalanceAfter(transaction.getAfterAvailableAmount());
            transactionVO.setDescription(transaction.getTransactionDescription());
            transactionVO.setTransactionTime(transaction.getTransactionTime());
            transactionVO.setStatus(1); // 默认状态为成功
            transactionVO.setCreateTime(transaction.getCreateTime());
            transactionVO.setUpdateTime(transaction.getUpdateTime());
            
            // 格式化时间
            if (transaction.getTransactionTime() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                transactionVO.setTransactionTimeClone(sdf.format(transaction.getTransactionTime()));
            }
            
            return transactionVO;
        }).collect(Collectors.toList());
        
        // 构建分页结果
        Page<TransactionRecordVO> resultPage = new Page<>(pageRequest.getPageNo(), pageRequest.getPageSize());
        resultPage.setRecords(transactionVOList);
        resultPage.setTotal(transactionPage.getTotal());
        resultPage.setPages(transactionPage.getPages());
        
        log.info("查询到 {} 条交易记录", transactionVOList.size());
        return resultPage;
    }

    @Override
    public Boolean processRepayment(String creditOrderNo, BigDecimal repaymentAmount, String repaymentProof) {
     return creditPaymentOrderService.processRepayment(creditOrderNo, repaymentAmount, repaymentProof);
    }

    @Override
    public RepaymentRecordVO getRepaymentDetail(String id) {
        RepaymentRecord repaymentRecord = repaymentRecordDao.selectById(id);
        RepaymentRecordVO repaymentVO = new RepaymentRecordVO();
        BeanUtils.copyProperties(repaymentRecord, repaymentVO);
        repaymentVO.setId(repaymentRecord.getId());
        repaymentVO.setMemberName(repaymentRecord.getUserName());
        repaymentVO.setMemberAccount(repaymentRecord.getUserId().toString());
        repaymentVO.setVoucherUrl(repaymentRecord.getRepaymentProof());

        // 格式化时间
        if (repaymentRecord.getRepaymentTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            repaymentVO.setRepaymentTimeClone(sdf.format(repaymentRecord.getRepaymentTime()));
        }
        return repaymentVO;
    }

    @Override
    public Boolean auditRepayment(RepaymentAuditVO repaymentAuditVO) {
        try {
            // 获取当前登录用户
            User currentUser = userService.getInfo();
            if (currentUser == null) {
                return false;
            }

            // 查询还款记录
            RepaymentRecord repaymentRecord = this.repaymentRecordDao.selectById(repaymentAuditVO.getId());
            if (repaymentRecord == null) {
                return false;
            }

            // 更新审核信息
            repaymentRecord.setRepaymentStatus(repaymentAuditVO.getRepaymentStatus());
            repaymentRecord.setAuditorId(currentUser.getId());
            repaymentRecord.setAuditorName(currentUser.getRealName());
            repaymentRecord.setAuditTime(new Date());
            repaymentRecord.setAuditRemark(repaymentAuditVO.getAuditRemark());
            repaymentRecord.setUpdateTime(new Date());

            // 如果审核通过，调用实际的还款处理逻辑
            if (repaymentAuditVO.getRepaymentStatus() == AuditStatus.PASS.getCode()) {
                // 调用授信支付订单服务的实际还款处理方法
                Boolean processResult = creditPaymentOrderService.processApprovedRepayment(repaymentRecord);
                if (!processResult) {
                    log.error("审核通过后的还款处理失败，还款记录ID：{}", repaymentRecord.getId());
                    return false;
                }
            }

            // 更新还款记录
             this.repaymentRecordDao.updateById(repaymentRecord);
            return true;
        } catch (Exception e) {
            log.error("审核还款记录异常", e);
            return false;
        }
    }

    @Override
    public PageInfo<CreditPaymentOrder> pageRepaymentOrder(CreditOrderRequest repaymentPageVO) {
        User info = userService.getInfo();
        com.github.pagehelper.Page<CreditPaymentOrder> page = PageHelper.startPage(repaymentPageVO.getPage(), repaymentPageVO.getLimit());
        LambdaQueryWrapper<CreditPaymentOrder> queryWrapper = new LambdaQueryWrapper<>();
        if(info.getFarmType().equals(FarmTypeEnum.TENANT_TYPE_COOPERATIVE.getCode())&&info.getUserType().equals(UserTypeEnum.USER_TYPE_EMPLOYEE.getCode())) {
            queryWrapper.eq(CreditPaymentOrder::getUserId, info.getId());
        }
        if(Objects.nonNull(repaymentPageVO.getRepaymentStatus())){
            queryWrapper.eq(CreditPaymentOrder::getRepaymentStatus, repaymentPageVO.getRepaymentStatus());
        }
        queryWrapper.eq(CreditPaymentOrder::getDeleteFlag, 0);
        queryWrapper.orderByDesc(CreditPaymentOrder::getCreateTime);
        List<CreditPaymentOrder> list = this.creditPaymentOrderService.list(queryWrapper);
        return CommonPage.copyPageInfo(page,list);

    }
} 