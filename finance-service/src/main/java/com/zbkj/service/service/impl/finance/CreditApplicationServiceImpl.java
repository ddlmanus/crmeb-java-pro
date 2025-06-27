package com.zbkj.service.service.impl.finance;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.enums.AuditStatus;
import com.zbkj.common.enums.FarmTypeEnum;
import com.zbkj.common.enums.UserTypeEnum;
import com.zbkj.common.model.employee.Employee;
import com.zbkj.common.model.finance.*;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.service.dao.EmployeeDao;
import com.zbkj.service.dao.finance.AssetAssessmentBreedingDao;
import com.zbkj.service.dao.finance.CreditApplicationDao;
import com.zbkj.service.dao.finance.GuarantorInfoDao;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.finance.*;
import com.zbkj.common.vo.finance.*;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.service.service.SystemConfigService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CreditApplicationServiceImpl extends ServiceImpl<CreditApplicationDao, CreditApplication> implements CreditApplicationService {

    @Resource
    private GuarantorInfoDao guarantorInfoMapper;
    
    @Autowired
    private BreedingProductService breedingProductService;
    @Autowired
    private UserService userService;
    @Autowired
    private FarmInstitutionService farmInstitutionService;
    @Autowired
    private AssetAssessmentService assetAssessmentService;
    
    @Autowired
    private AssetAssessmentBreedingService assetAssessmentBreedingService;
    
    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private CreditApplicationAuditHistoryService creditApplicationAuditHistoryService;


    /**
     * 获取用户的授信总金额
     * @param userId
     * @return
     */
    @Override
    public Double getAvailableCreditAmount(String userId) {
        LambdaQueryWrapper<CreditApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CreditApplication::getUserId, userId);
        queryWrapper.eq(CreditApplication::getAuditStatus, 2);
        return this.baseMapper.selectOne(queryWrapper).getAvailableCreditAmount().doubleValue();
    }

    /**
     * 扣除授信金额
     * @param userId
     * @param price
     */
    @Override
    public void deductCreditAmount(String userId, BigDecimal price) {
        CreditApplication creditApplication = this.baseMapper.selectOne(new LambdaQueryWrapper<CreditApplication>().eq(CreditApplication::getUserId, userId));
        //计算利息金额
        BigDecimal interestAmount = price.multiply(creditApplication.getCreditRatio());
        //剩余额度减少 需要减少price加上利息金额
        creditApplication.setAvailableCreditAmount(creditApplication.getAvailableCreditAmount().subtract(price.add(interestAmount)));
        //待还款总额度增加=待还款总额度+price+利息金额
        creditApplication.setTotalRepaymentAmount(creditApplication.getTotalRepaymentAmount().add(price.add(interestAmount)));
        this.updateById(creditApplication);
    }

    /**
     * 获取用户已使用的授信金额
     * @param userId
     * @return
     */
    @Override
    public Double getUsedCreditAmount(String userId) {
        CreditApplication creditApplication = this.baseMapper.selectOne(new LambdaQueryWrapper<CreditApplication>().eq(CreditApplication::getUserId, userId));
        //已使用余额=授信总金额-剩余授信额度
        return creditApplication.getTotalAmount().subtract(creditApplication.getAvailableCreditAmount()).doubleValue();
    }

    /**
     * 根据机构id获取养殖场
     */


    /**
     * 审核授信额度申请
     * @param request 审核请求参数
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditCreditApplication(CreditApplicationAuditRequest request) {
        // 获取授信申请
        CreditApplication creditApplication = this.getById(request.getId());
        if (creditApplication == null) {
            throw new RuntimeException("授信申请不存在");
        }
        
        // 业务验证：根据审核状态验证必填字段
        if (request.getAuditStatus() == AuditStatus.PASS.getCode()) { // 审核通过
            if (request.getCreditAmount() == null || request.getCreditAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("审核通过时授信额度不能为空且必须大于0");
            }
            if (request.getCreditRatio() == null || request.getCreditRatio().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("审核通过时授信利率不能为空且必须大于0");
            }
            if (request.getCreditRatio().compareTo(new BigDecimal("1000")) > 0) {
                throw new RuntimeException("授信利率不能大于1000‰");
            }
            if (request.getCreditPeriod() == null || request.getCreditPeriod() <= 0) {
                throw new RuntimeException("审核通过时授信期限不能为空且必须大于0");
            }
            if (request.getCreditPeriod() > 120) {
                throw new RuntimeException("授信期限不能大于120个月");
            }
        } else if (request.getAuditStatus() == AuditStatus.REJECT.getCode()) { // 审核拒绝
            if (request.getAuditRemark() == null || request.getAuditRemark().trim().isEmpty()) {
                throw new RuntimeException("审核拒绝时必须填写拒绝原因");
            }
        }
        
        // 更新审核状态
        creditApplication.setAuditStatus(request.getAuditStatus());
        creditApplication.setAuditTime(new Date());
        creditApplication.setAuditRemark(request.getAuditRemark());
        
        // 如果审核通过，设置授信额度
        if (request.getAuditStatus() == AuditStatus.PASS.getCode()) {
            // 设置授信额度相关信息
            creditApplication.setTotalAmount(request.getCreditAmount());
            creditApplication.setAvailableCreditAmount(request.getCreditAmount());
            creditApplication.setTotalRepaymentAmount(BigDecimal.ZERO);
            creditApplication.setCreditRatio(request.getCreditRatio()); // 授信利率
            creditApplication.setCreditStartTime(new Date());
            creditApplication.setCreditPeriod(request.getCreditPeriod());
            creditApplication.setCardNumber(request.getCardNumber());
            
            // 同步更新用户表的授信信息
            updateUserCreditInfo(creditApplication, request);
        } else if (request.getAuditStatus() == AuditStatus.REJECT.getCode()) {
            // 审核拒绝时，更新用户授信状态
            User user = userService.getById(creditApplication.getUserId());
            if (user != null) {
                user.setCreditStatus(0); // 0-未授信
                user.setUpdateTime(new Date());
                userService.updateById(user);
            }
        }
        
        return this.updateById(creditApplication);
    }

    /**
     * 分页查询授信申请列表
     * @param pageInfo 分页查询参数
     * @return 授信申请分页列表
     */
    @Override
    public PageInfo<CreditApplicationPage> pageInfo(CreditApplicationPageInfo pageInfo) {
        User info = userService.getInfo();
        // 创建分页对象
        Page<CreditApplication> page = PageHelper.startPage(pageInfo.getPageNumber(), pageInfo.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<CreditApplication> queryWrapper = new LambdaQueryWrapper<>();
        
        // 根据当前用户角色不同，查询条件可能不同
        
        // 设置其他查询条件
        if (pageInfo.getAuditStatus() != null) {
            queryWrapper.eq(CreditApplication::getAuditStatus, pageInfo.getAuditStatus());
        }
        if(info.getFarmType().equals(FarmTypeEnum.TENANT_TYPE_COOPERATIVE.getCode())&&info.getUserType().equals(UserTypeEnum.USER_TYPE_ADMIN.getCode())){
            queryWrapper.eq(CreditApplication::getOrganizationId, info.getOrganizationId());
        }else{
            queryWrapper.eq(CreditApplication::getUserId, info.getId());
        }
        
        // 按申请时间倒序排序
        queryWrapper.orderByDesc(CreditApplication::getApplyTime);

        List<CreditApplication> resultList = this.list(queryWrapper);
        List<CreditApplicationPage> resultVOList = resultList.stream().map(this::convertToPageVO).collect(Collectors.toList());
        PageInfo<CreditApplicationPage> creditApplicationPageInfo = CommonPage.copyPageInfo(page, resultVOList);
        return creditApplicationPageInfo;
    }

    /**
     * 转换为分页VO对象
     * @param creditApplication 授信申请实体
     * @return 授信申请分页VO
     */
    private CreditApplicationPage convertToPageVO(CreditApplication creditApplication) {
        CreditApplicationPage pageVO = new CreditApplicationPage();
        BeanUtils.copyProperties(creditApplication, pageVO);
        
        // 查询并设置担保人信息
        LambdaQueryWrapper<GuarantorInfo> queryWrapper = new LambdaQueryWrapper<>();
        List<GuarantorInfo> guarantors = guarantorInfoMapper.selectList(queryWrapper);
        pageVO.setGuarantors(guarantors);
        
        return pageVO;
    }

    /**
     * 获取授信申请详情
     * @param id 授信申请ID
     * @return 授信申请详情
     */
    @Override
    public CreditApplicationPage get(String id) {
        // 查询授信申请
        CreditApplication creditApplication = this.getById(id);
        if (creditApplication == null) {
            return null;
        }
        
        log.info("获取授信申请详情，申请ID：{}，评估ID：{}", id, creditApplication.getAssessmentId());
        
        // 转换为VO对象
        CreditApplicationPage pageVO = new CreditApplicationPage();
        BeanUtils.copyProperties(creditApplication, pageVO);
        
        // 查询并设置担保人信息
        LambdaQueryWrapper<GuarantorInfo> queryWrapper = new LambdaQueryWrapper<>();
        List<GuarantorInfo> guarantors = guarantorInfoMapper.selectList(queryWrapper);
        pageVO.setGuarantors(guarantors);

        /**
         * 查询养殖品种
         */
        LambdaQueryWrapper<BreedingProduct> query = new LambdaQueryWrapper<>();
        query.eq(BreedingProduct::getDeleteFlag, 0);
        List<BreedingProduct> breedingProducts = this.breedingProductService.list(query);
        pageVO.setBreedingProducts(breedingProducts);
        
        // 查询资产评估品种信息
        if (StrUtil.isNotBlank(creditApplication.getAssessmentId())) {
            log.info("查询资产评估品种信息，评估ID：{}", creditApplication.getAssessmentId());
            List<AssetAssessmentBreeding> assessmentBreedingList = assetAssessmentBreedingService.getByAssessmentId(creditApplication.getAssessmentId());
            pageVO.setAssessmentBreedingList(assessmentBreedingList);
            log.info("查询到{}条资产评估品种信息", assessmentBreedingList != null ? assessmentBreedingList.size() : 0);
        } else {
            log.warn("授信申请未关联资产评估ID，申请ID：{}", id);
            pageVO.setAssessmentBreedingList(new ArrayList<>());
        }
        
        return pageVO;
    }

    /**
     * 获取当前登录人的授信额度
     * @return 当前登录人的授信额度信息
     */
    @Override
    public CreditApplicationInfo getCreditAmount() {
        // 获取当前登录用户
        User currentUser = userService.getInfo();
        if (currentUser == null) {
            return null;
        }
        
        CreditApplicationInfo info = new CreditApplicationInfo();
        
        // 根据用户类型返回不同的授信信息
        if (currentUser.getFarmType() == 2 && currentUser.getUserType() == 1) {
            // 合作社管理员：返回合作社的整体授信信息
            if (currentUser.getOrganizationId() != null && !currentUser.getOrganizationId().isEmpty()) {
                // 查询合作社下所有员工的授信信息汇总
                List<User> organizationUsers = userService.list(new LambdaQueryWrapper<User>()
                        .eq(User::getOrganizationId, currentUser.getOrganizationId()));
                
                BigDecimal totalCreditLimit = BigDecimal.ZERO;
                BigDecimal totalRemainingCredit = BigDecimal.ZERO;
                BigDecimal totalUsedCredit = BigDecimal.ZERO;
                
                for (User user : organizationUsers) {
                    if (user.getCreditLimit() != null) {
                        totalCreditLimit = totalCreditLimit.add(user.getCreditLimit());
                    }
                    if (user.getRemainingCredit() != null) {
                        totalRemainingCredit = totalRemainingCredit.add(user.getRemainingCredit());
                    }
                    if (user.getUsedCredit() != null) {
                        totalUsedCredit = totalUsedCredit.add(user.getUsedCredit());
                    }
                }
                
                info.setTotalAmount(totalCreditLimit);
                info.setAvailableCreditAmount(totalRemainingCredit);
                info.setUsedCreditAmount(totalUsedCredit);
                info.setAuditStatus(currentUser.getCreditStatus());
            } else {
                // 没有机构信息，返回个人授信
                info = getUserCreditFromUserInfo(currentUser);
            }
        } else {
            // 合作社员工或养殖户：返回个人授信信息
            info = getUserCreditFromUserInfo(currentUser);
        }
        
        return info;
    }
    
    /**
     * 从用户信息中获取授信数据
     */
    private CreditApplicationInfo getUserCreditFromUserInfo(User currentUser) {
        CreditApplicationInfo info = new CreditApplicationInfo();
        
        // 直接从用户表中获取授信信息
        info.setTotalAmount(currentUser.getCreditLimit() != null ? currentUser.getCreditLimit() : BigDecimal.ZERO);
        info.setAvailableCreditAmount(currentUser.getRemainingCredit() != null ? currentUser.getRemainingCredit() : BigDecimal.ZERO);
        info.setUsedCreditAmount(currentUser.getUsedCredit() != null ? currentUser.getUsedCredit() : BigDecimal.ZERO);
        info.setAuditStatus(currentUser.getCreditStatus());
        
        // 设置其他相关信息
        info.setUserId(currentUser.getId().toString());
        info.setUserName(currentUser.getAccount());
        info.setCreditStartTime(currentUser.getCreditStartTime());
        info.setCreditRatio(currentUser.getCreditRatio());
        
        // 计算授信期限（如果有结束时间的话）
        if (currentUser.getCreditStartTime() != null && currentUser.getCreditEndTime() != null) {
            // 计算月份差
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(currentUser.getCreditStartTime());
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(currentUser.getCreditEndTime());
            
            int months = (endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR)) * 12 
                       + endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH);
            info.setCreditPeriod(months);
        }
        
        return info;
    }

    /**
     * 获取授信申请统计信息
     * @return 统计信息
     */
    @Override
    public CreditApplicationStatistics getStatistics() {
        CreditApplicationStatistics statistics = new CreditApplicationStatistics();
        
        // 查询总申请数
        long totalCount = this.count();
        statistics.setTotalCount(totalCount);
        
        // 查询各状态数量
        LambdaQueryWrapper<CreditApplication> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(CreditApplication::getAuditStatus, 0);
        long pendingCount = this.count(pendingWrapper);
        statistics.setPendingCount(pendingCount);
        
        LambdaQueryWrapper<CreditApplication> approvedWrapper = new LambdaQueryWrapper<>();
        approvedWrapper.eq(CreditApplication::getAuditStatus, 2);
        long approvedCount = this.count(approvedWrapper);
        statistics.setApprovedCount(approvedCount);
        
        LambdaQueryWrapper<CreditApplication> rejectedWrapper = new LambdaQueryWrapper<>();
        rejectedWrapper.eq(CreditApplication::getAuditStatus, 1);
        long rejectedCount = this.count(rejectedWrapper);
        statistics.setRejectedCount(rejectedCount);
        
        // 查询金额统计（只统计已通过的申请）
        List<CreditApplication> approvedApplications = this.list(approvedWrapper);
        BigDecimal totalCreditAmount = BigDecimal.ZERO;
        BigDecimal usedCreditAmount = BigDecimal.ZERO;
        BigDecimal availableCreditAmount = BigDecimal.ZERO;
        
        for (CreditApplication app : approvedApplications) {
            if (app.getTotalAmount() != null) {
                totalCreditAmount = totalCreditAmount.add(app.getTotalAmount());
            }
            if (app.getAvailableCreditAmount() != null) {
                availableCreditAmount = availableCreditAmount.add(app.getAvailableCreditAmount());
            }
        }
        
        // 已使用金额 = 总授信金额 - 可用金额
        usedCreditAmount = totalCreditAmount.subtract(availableCreditAmount);
        
        statistics.setTotalCreditAmount(totalCreditAmount);
        statistics.setUsedCreditAmount(usedCreditAmount);
        statistics.setAvailableCreditAmount(availableCreditAmount);
        
        // 查询申请总金额（所有申请）
        List<CreditApplication> allApplications = this.list();
        BigDecimal totalApplicationAmount = BigDecimal.ZERO;
        for (CreditApplication app : allApplications) {
            if (app.getApplyAmount() != null) {
                totalApplicationAmount = totalApplicationAmount.add(app.getApplyAmount());
            }
        }
        statistics.setTotalApplicationAmount(totalApplicationAmount);
        
        // 查询通过金额（已通过申请的申请金额）
        BigDecimal approvedAmount = BigDecimal.ZERO;
        for (CreditApplication app : approvedApplications) {
            if (app.getApplyAmount() != null) {
                approvedAmount = approvedAmount.add(app.getApplyAmount());
            }
        }
        statistics.setApprovedAmount(approvedAmount);
        
        // 计算通过率
        Double approvalRate = 0.0;
        if (totalCount > 0) {
            approvalRate = (double) approvedCount / totalCount;
        }
        statistics.setApprovalRate(approvalRate);
        
        // 生成月度统计数据（最近6个月）
        statistics.setMonthlyStats(generateMonthlyStats());
        
        // 生成类型统计数据
        statistics.setTypeStats(generateTypeStats());
        
        return statistics;
    }


    /**
     * 生成月度统计数据
     */
    private List<CreditApplicationStatistics.MonthlyStatistics> generateMonthlyStats() {
        List<CreditApplicationStatistics.MonthlyStatistics> monthlyStats = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        // 获取最近6个月的数据
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = now.minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
            String monthStr = monthStart.format(formatter);
            
            // 构建查询条件
            LambdaQueryWrapper<CreditApplication> monthWrapper = new LambdaQueryWrapper<>();
            monthWrapper.ge(CreditApplication::getApplyTime, java.sql.Date.valueOf(monthStart));
            monthWrapper.le(CreditApplication::getApplyTime, java.sql.Date.valueOf(monthEnd));
            
            List<CreditApplication> monthApplications = this.list(monthWrapper);
            
            CreditApplicationStatistics.MonthlyStatistics monthly = new CreditApplicationStatistics.MonthlyStatistics();
            monthly.setMonth(monthStr);
            monthly.setTotalCount((long) monthApplications.size());
            
            long pendingCount = monthApplications.stream().filter(app -> app.getAuditStatus() == 0).count();
            long approvedCount = monthApplications.stream().filter(app -> app.getAuditStatus() == 2).count();
            long rejectedCount = monthApplications.stream().filter(app -> app.getAuditStatus() == 1).count();
            
            monthly.setPendingCount(pendingCount);
            monthly.setApprovedCount(approvedCount);
            monthly.setRejectedCount(rejectedCount);
            
            // 计算金额统计
            BigDecimal totalAmount = monthApplications.stream()
                .filter(app -> app.getApplyAmount() != null)
                .map(CreditApplication::getApplyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal approvedAmount = monthApplications.stream()
                .filter(app -> app.getAuditStatus() == 2 && app.getApplyAmount() != null)
                .map(CreditApplication::getApplyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            monthly.setTotalAmount(totalAmount);
            monthly.setApprovedAmount(approvedAmount);
            
            // 计算通过率
            Double rate = monthly.getTotalCount() > 0 ? (double) approvedCount / monthly.getTotalCount() : 0.0;
            monthly.setApprovalRate(rate);
            
            monthlyStats.add(monthly);
        }
        
        return monthlyStats;
    }
    
    /**
     * 生成类型统计数据
     */
    private List<CreditApplicationStatistics.TypeStatistics> generateTypeStats() {
        List<CreditApplicationStatistics.TypeStatistics> typeStats = new ArrayList<>();
        
        // 按申请类型统计
        LambdaQueryWrapper<CreditApplication> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(CreditApplication::getApplyType, 0);
        long memberCount = this.count(memberWrapper);
        
        LambdaQueryWrapper<CreditApplication> coopWrapper = new LambdaQueryWrapper<>();
        coopWrapper.eq(CreditApplication::getApplyType, 1);
        long coopCount = this.count(coopWrapper);
        
        CreditApplicationStatistics.TypeStatistics memberStat = new CreditApplicationStatistics.TypeStatistics();
        memberStat.setTypeName("社员申请");
        memberStat.setTypeValue(0);
        memberStat.setCount(memberCount);
        
        CreditApplicationStatistics.TypeStatistics coopStat = new CreditApplicationStatistics.TypeStatistics();
        coopStat.setTypeName("合作社申请");
        coopStat.setTypeValue(1);
        coopStat.setCount(coopCount);
        
        typeStats.add(memberStat);
        typeStats.add(coopStat);
        
        return typeStats;
    }


    @Override
    public String createAssetAssessment(AssetAssessmentRequestVO request) {
        return assetAssessmentService.createOrUpdateAssessment(request);
    }

    @Override
    public AssetAssessmentDetailVO getAssetAssessmentDetail(String assessmentId) {
        return assetAssessmentService.getAssessmentDetail(assessmentId);
    }

    @Override
    public IPage<AssetAssessmentPageVO> pageAssetAssessment(AssetAssessmentPageRequest pageRequest) {
        return assetAssessmentService.pageAssessment(pageRequest);
    }

    @Override
    public AssetAssessmentPageVO getLatestAssetAssessment() {
        return assetAssessmentService.getLatestAssessmentByCurrentUser();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String applyCreditAmount(CreditAmountApplicationVO request) {
        // 获取当前登录用户
        User currentUser = userService.getInfo();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }

        // 检查用户是否可以申请
        CreditApplicationStatusVO statusVO = checkUserCanApply();
        if (!statusVO.getCanApply()) {
            throw new RuntimeException(statusVO.getReason());
        }

        // 验证资产评估是否存在
        AssetAssessmentDetailVO assessment = assetAssessmentService.getAssessmentDetail(request.getAssessmentId());
        if (assessment == null) {
            throw new RuntimeException("资产评估记录不存在");
        }

        // 检查资产评估是否属于当前用户
        if (!assessment.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("无权限使用此资产评估记录");
        }

        // 检查资产评估是否已被使用
        if (assessment.getIsUsed() == 1) {
            throw new RuntimeException("该资产评估记录已被使用");
        }

        // 创建授信申请记录
        CreditApplication creditApplication = new CreditApplication();
        creditApplication.setUserId(currentUser.getId());
        creditApplication.setUserName(currentUser.getAccount());
        creditApplication.setUserType(currentUser.getUserType());
        creditApplication.setAssessmentId(request.getAssessmentId());
        creditApplication.setApplyAmount(request.getApplyAmount());
        creditApplication.setApplyImages(request.getApplyImages());
        creditApplication.setApplyTime(new Date());
        creditApplication.setApplyStatus(1); // 已提交
        creditApplication.setAuditStatus(AuditStatus.PENDING.getCode()); // 待审核状态

        // 设置申请类型和相关信息
        if (currentUser.getOrganizationId() != null && !currentUser.getOrganizationId().isEmpty()) {
            // 有机构的情况：合作社申请
            creditApplication.setApplyType(1); // 合作社申请
            creditApplication.setOrganizationId(currentUser.getOrganizationId());
            
            // 设置养殖场信息
            if (currentUser.getFarmId() != null) {
                FarmInstitution farmInstitution = farmInstitutionService.getById(currentUser.getFarmId());
                if (farmInstitution != null) {
                    creditApplication.setFarmName(farmInstitution.getFarmName());
                    creditApplication.setManagerPhone(farmInstitution.getContactPhone());
                    creditApplication.setManagerName(farmInstitution.getContactName());
                }
            }
        } else {
            // 没有机构的情况：种植户/种植企业申请
            creditApplication.setApplyType(0); // 社员申请（种植户/种植企业）
            
            // 使用用户的个人养殖场信息
            if (currentUser.getFarmCode() != null && !currentUser.getFarmCode().isEmpty()) {
                creditApplication.setFarmName(currentUser.getFarmName() != null ? currentUser.getFarmName() : "个人养殖场");
                // 使用用户的个人信息作为负责人信息
                creditApplication.setManagerPhone(currentUser.getPhone());
                creditApplication.setManagerName(currentUser.getRealName() != null ? currentUser.getRealName() : currentUser.getNickname());
            } else {
                // 如果用户既没有机构也没有养殖场信息，则设置默认信息
                creditApplication.setFarmName("个人种植");
                creditApplication.setManagerPhone(currentUser.getPhone());
                creditApplication.setManagerName(currentUser.getRealName() != null ? currentUser.getRealName() : currentUser.getNickname());
            }
        }

        // 从资产评估中获取相关信息
        creditApplication.setIdNumber(assessment.getIdNumber());

        // 设置初始值
        creditApplication.setAvailableCreditAmount(BigDecimal.ZERO);
        creditApplication.setTotalRepaymentAmount(BigDecimal.ZERO);

        // 保存授信申请
        this.save(creditApplication);

        // 标记资产评估为已使用
        assetAssessmentService.markAssessmentAsUsed(request.getAssessmentId());

        // 更新用户表的授信状态为申请中
        currentUser.setCreditStatus(0); // 0-申请中
        currentUser.setUpdateTime(new Date());
        userService.updateById(currentUser);
        
        log.info("用户{}提交授信申请，申请类型：{}，申请金额：{}，申请ID：{}", 
                currentUser.getAccount(), 
                creditApplication.getApplyType() == 0 ? "种植户/种植企业申请" : "合作社申请",
                request.getApplyAmount(), 
                creditApplication.getId());

        return creditApplication.getId();
    }

    @Override
    public CreditApplicationStatusVO checkUserCanApply() {
        CreditApplicationStatusVO statusVO = new CreditApplicationStatusVO();
        
        // 获取当前登录用户
        User currentUser = userService.getInfo();
        if (currentUser == null) {
            statusVO.setCanApply(false);
            statusVO.setReason("用户未登录");
            return statusVO;
        }

        // 检查是否有可用的资产评估
        String availableAssessmentId = assetAssessmentService.getAvailableAssessmentByUser(currentUser.getId());
        statusVO.setHasValidAssessment(availableAssessmentId != null);
        statusVO.setAvailableAssessmentId(availableAssessmentId);

        if (availableAssessmentId == null) {
            statusVO.setCanApply(false);
            statusVO.setReason("请先完成资产评估");
            return statusVO;
        }

        // 首先检查是否存在有效的（未过期的）审核通过的授信额度
        LambdaQueryWrapper<CreditApplication> validCreditWrapper = new LambdaQueryWrapper<>();
        validCreditWrapper.eq(CreditApplication::getUserId, currentUser.getId());
        validCreditWrapper.eq(CreditApplication::getAuditStatus, AuditStatus.PASS.getCode()); // 审核通过
        validCreditWrapper.isNotNull(CreditApplication::getCreditStartTime);
        validCreditWrapper.isNotNull(CreditApplication::getCreditPeriod);
        
        List<CreditApplication> validCredits = this.list(validCreditWrapper);
        
        // 检查是否有未过期的授信额度
        Date currentDate = new Date();
        for (CreditApplication credit : validCredits) {
            if (credit.getCreditStartTime() != null && credit.getCreditPeriod() != null) {
                // 计算授信到期时间（开始时间 + 授信期限（月））
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(credit.getCreditStartTime());
                calendar.add(Calendar.MONTH, credit.getCreditPeriod());
                Date creditEndTime = calendar.getTime();
                
                // 如果当前时间在授信期限内，则不能重复申请
                if (currentDate.before(creditEndTime)) {
                    statusVO.setCanApply(false);
                    statusVO.setReason("您当前存在有效的授信额度，无需重复申请");
                    statusVO.setCurrentStatus(AuditStatus.PASS.getCode());
                    statusVO.setCurrentStatusDesc("审核通过（有效期内）");
                    statusVO.setLastApplicationId(credit.getId());
                    statusVO.setLastApplyTime(credit.getApplyTime());
                    return statusVO;
                }
            }
        }

        // 查询用户最新的申请记录
        LambdaQueryWrapper<CreditApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CreditApplication::getUserId, currentUser.getId());
        queryWrapper.orderByDesc(CreditApplication::getApplyTime);
        queryWrapper.last("LIMIT 1");

        CreditApplication latestApplication = this.getOne(queryWrapper);

        if (latestApplication == null) {
            // 没有申请记录，可以申请
            statusVO.setCanApply(true);
            return statusVO;
        }

        statusVO.setLastApplicationId(latestApplication.getId());
        statusVO.setLastApplyTime(latestApplication.getApplyTime());
        statusVO.setCurrentStatus(latestApplication.getAuditStatus());

        // 根据审核状态决定是否可以申请
        switch (latestApplication.getAuditStatus()) {
            case 0: // 待审核
                statusVO.setCanApply(false);
                statusVO.setReason("您有申请正在审核中，请等待审核结果");
                statusVO.setCurrentStatusDesc("待审核");
                break;
            case 1: // 拒绝
                statusVO.setCanApply(true);
                statusVO.setCurrentStatusDesc("审核拒绝");
                break;
            case 2: // 通过
                // 这里检查授信是否已过期
                if (latestApplication.getCreditStartTime() != null && latestApplication.getCreditPeriod() != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(latestApplication.getCreditStartTime());
                    calendar.add(Calendar.MONTH, latestApplication.getCreditPeriod());
                    Date creditEndTime = calendar.getTime();
                    
                    if (currentDate.before(creditEndTime)) {
                        // 授信未过期，不能重复申请
                        statusVO.setCanApply(false);
                        statusVO.setReason("您已有通过审核的授信额度，无需重复申请");
                        statusVO.setCurrentStatusDesc("审核通过（有效期内）");
                    } else {
                        // 授信已过期，可以重新申请
                        statusVO.setCanApply(true);
                        statusVO.setCurrentStatusDesc("审核通过（已过期）");
                    }
                } else {
                    // 如果没有授信开始时间或期限，按照原逻辑处理
                    statusVO.setCanApply(false);
                    statusVO.setReason("您已有通过审核的授信额度，无需重复申请");
                    statusVO.setCurrentStatusDesc("审核通过");
                }
                break;
            default:
                statusVO.setCanApply(false);
                statusVO.setReason("未知的审核状态");
                break;
        }

        return statusVO;
    }

    /**
     * 更新用户表的授信信息
     */
    private void updateUserCreditInfo(CreditApplication creditApplication, CreditApplicationAuditRequest request) {
        User user = userService.getById(creditApplication.getUserId());
        if (user != null) {
            // 更新授信信息
            user.setCreditLimit(request.getCreditAmount());
            user.setRemainingCredit(request.getCreditAmount());
            user.setUsedCredit(BigDecimal.ZERO);
            user.setPendingRepayment(BigDecimal.ZERO);
            user.setCreditStatus(1); // 1-已授信
            user.setCreditStartTime(new Date());
            user.setCreditEndTime(calculateCreditEndTime(new Date(), request.getCreditPeriod()));
            user.setCreditCoefficient(request.getCreditRatio());
            
            // 新增字段设置
            user.setCreditRatio(request.getCreditRatio()); // 授信利率
            user.setRepaymentDays(request.getCreditPeriod() != null ? request.getCreditPeriod() * 30 : null); // 还款期限（天）= 月数 * 30
            user.setCardNumber(request.getCardNumber()); // 卡号
            
            user.setLastCreditAuditTime(new Date());
            user.setLastCreditAuditUserId(getCurrentAuditUserId());
            user.setCreditAuditRemark(request.getAuditRemark());
            user.setUpdateTime(new Date());
            
            // 设置授信层级
            if (user.getUserType() == 1) { // 管理员类型（合作社）
                user.setCreditLevel(2);
                user.setParentCreditUserId(getPlatformUserId()); // 上级为平台
            } else { // 员工类型
                user.setCreditLevel(3);
                user.setParentCreditUserId(getCooperativeUserId(user.getOrganizationId())); // 上级为合作社
            }
            
            userService.updateById(user);
            
            log.info("更新用户{}授信信息成功，授信额度：{}，授信期限：{}月，授信利率：{}，卡号：{}", 
                    user.getAccount(), request.getCreditAmount(), request.getCreditPeriod(), 
                    request.getCreditRatio(), request.getCardNumber());
        }
    }
    
    /**
     * 计算授信结束时间
     */
    private Date calculateCreditEndTime(Date startTime, Integer creditPeriod) {
        if (startTime == null || creditPeriod == null || creditPeriod <= 0) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.add(Calendar.MONTH, creditPeriod);
        return calendar.getTime();
    }
    
    /**
     * 获取当前审核人ID
     */
    private Integer getCurrentAuditUserId() {
        try {
            User currentUser = userService.getInfo();
            return currentUser != null ? currentUser.getId() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取平台用户ID（用于合作社的上级）
     */
    private Integer getPlatformUserId() {
        // 这里可以配置平台用户ID，或者从配置表中获取
        return 1; // 假设平台用户ID为1
    }
    
    /**
     * 获取合作社用户ID（用于员工的上级）
     */
    private Integer getCooperativeUserId(String organizationId) {
        if (organizationId == null) {
            return null;
        }
        // 根据机构ID查找合作社管理员用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOrganizationId, organizationId);
        wrapper.eq(User::getUserType, 1); // 管理员类型
        wrapper.eq(User::getStatus, true);
        wrapper.orderByDesc(User::getCreateTime);
        wrapper.last("LIMIT 1");
        
        User cooperativeUser = userService.getOne(wrapper);
        return cooperativeUser != null ? cooperativeUser.getId() : null;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cooperativeAuditEmployeeCredit(CooperativeCreditAuditVO request) {
        // 获取当前登录用户（合作社管理员）
        User currentUser = userService.getInfo();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }

        // 验证用户是否为合作社管理员
        if (currentUser.getUserType() != 1) {
            throw new RuntimeException("只有合作社管理员可以审核员工授信申请");
        }

        // 获取授信申请
        CreditApplication creditApplication = this.getById(request.getApplicationId());
        if (creditApplication == null) {
            throw new RuntimeException("授信申请不存在");
        }

        // 验证申请是否属于当前合作社
        User applicantUser = userService.getById(creditApplication.getUserId());
        if (applicantUser == null || !currentUser.getOrganizationId().equals(applicantUser.getOrganizationId())) {
            throw new RuntimeException("无权限审核此申请");
        }

        // 验证申请状态
        if (creditApplication.getAuditStatus() != 0) {
            throw new RuntimeException("该申请已被审核，无法重复审核");
        }

        // 业务验证：根据审核状态验证必填字段
        if (request.getAuditStatus() == 2) { // 审核通过
            if (request.getApprovedAmount() == null || request.getApprovedAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("审核通过时审批额度不能为空且必须大于0");
            }
            if (request.getCreditRatio() == null || request.getCreditRatio().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("审核通过时授信利率不能为空且必须大于0");
            }
            
            // 验证授信利率是否符合系统配置的范围
            validateCreditRateRange(request.getCreditRatio());
            
            if (request.getCreditPeriod() == null || request.getCreditPeriod() <= 0) {
                throw new RuntimeException("审核通过时授信期限不能为空且必须大于0");
            }
            if (request.getCreditPeriod() > 120) {
                throw new RuntimeException("授信期限不能大于120个月");
            }

            // 验证合作社是否有足够的授信额度分配给员工
            if (currentUser.getRemainingCredit() == null || 
                currentUser.getRemainingCredit().compareTo(request.getApprovedAmount()) < 0) {
                throw new RuntimeException("合作社可用授信额度不足，可用额度：" + 
                    (currentUser.getRemainingCredit() != null ? currentUser.getRemainingCredit() : BigDecimal.ZERO) + 
                    "，申请分配额度：" + request.getApprovedAmount());
            }

            // 减少合作社的可用授信额度
            currentUser.setRemainingCredit(currentUser.getRemainingCredit().subtract(request.getApprovedAmount()));
            currentUser.setUsedCredit(currentUser.getUsedCredit() != null ? 
                currentUser.getUsedCredit().add(request.getApprovedAmount()) : request.getApprovedAmount());
            userService.updateById(currentUser);

        } else if (request.getAuditStatus() == 1) { // 审核拒绝
            if (request.getAuditRemark() == null || request.getAuditRemark().trim().isEmpty()) {
                throw new RuntimeException("审核拒绝时必须填写拒绝原因");
            }
        }

        // 更新审核状态
        creditApplication.setAuditStatus(request.getAuditStatus());
        creditApplication.setAuditTime(new Date());
        creditApplication.setAuditRemark(request.getAuditRemark());

        // 如果审核通过，设置授信额度
        if (request.getAuditStatus() == 2) {
            creditApplication.setTotalAmount(request.getApprovedAmount());
            creditApplication.setAvailableCreditAmount(request.getApprovedAmount());
            creditApplication.setTotalRepaymentAmount(BigDecimal.ZERO);
            creditApplication.setCreditRatio(request.getCreditRatio());
            creditApplication.setCreditStartTime(new Date());
            creditApplication.setCreditPeriod(request.getCreditPeriod());
            creditApplication.setCardNumber(request.getCardNumber());

            // 同步更新员工用户表的授信信息
            applicantUser.setCreditLimit(request.getApprovedAmount());
            applicantUser.setRemainingCredit(request.getApprovedAmount());
            applicantUser.setUsedCredit(BigDecimal.ZERO);
            applicantUser.setPendingRepayment(BigDecimal.ZERO);
            applicantUser.setCreditStatus(1); // 1-已授信
            applicantUser.setCreditStartTime(new Date());
            applicantUser.setCreditEndTime(calculateCreditEndTime(new Date(), request.getCreditPeriod()));
            applicantUser.setCreditCoefficient(request.getCreditRatio());
            
            // 新增字段设置
            applicantUser.setCreditRatio(request.getCreditRatio()); // 授信利率
            applicantUser.setRepaymentDays(request.getCreditPeriod() != null ? request.getCreditPeriod() * 7 : null); // 还款期限（天）
            applicantUser.setCardNumber(request.getCardNumber()); // 卡号
            
            applicantUser.setLastCreditAuditTime(new Date());
            applicantUser.setLastCreditAuditUserId(currentUser.getId());
            applicantUser.setCreditAuditRemark(request.getAuditRemark());
            applicantUser.setParentCreditUserId(currentUser.getId());
            applicantUser.setUpdateTime(new Date());
            userService.updateById(applicantUser);
        } else if (request.getAuditStatus() == 1) {
            // 审核拒绝时，更新员工授信状态
            applicantUser.setCreditStatus(0); // 0-未授信
            applicantUser.setUpdateTime(new Date());
            userService.updateById(applicantUser);
        }

        boolean result = this.updateById(creditApplication);
        
        log.info("合作社{}审核员工{}授信申请{}，审核结果：{}，审批额度：{}，授信利率：{}‰", 
                currentUser.getAccount(), applicantUser.getAccount(), 
                request.getAuditStatus() == 2 ? "通过" : "拒绝",
                creditApplication.getId(), request.getApprovedAmount(), request.getCreditRatio());
       CreditApplicationAuditHistory creditApplicationAuditHistory= creditApplicationAuditHistoryService.getByAppId(creditApplication.getId());
        creditApplicationAuditHistory.setAuditStatus(3);
        creditApplicationAuditHistory.setAuditRemark(request.getAuditRemark());
        creditApplicationAuditHistory.setAuditTime(new Date());
        creditApplicationAuditHistoryService.updateById(creditApplicationAuditHistory);
         //新增审核历史记录
        CreditApplicationAuditHistory auditHistory = new CreditApplicationAuditHistory();
        auditHistory.setApplicationId(creditApplication.getId());
        auditHistory.setOrganizationId(creditApplication.getOrganizationId());
        auditHistory.setAuditStatus(request.getAuditStatus());
        auditHistory.setAuditRemark(request.getAuditRemark());
        auditHistory.setAuditorId(currentUser.getId());
        auditHistory.setAuditorName(currentUser.getNickname());
        auditHistory.setAuditTime(new Date());
        auditHistory.setApplyAmount(creditApplication.getApplyAmount());
        auditHistory.setUserId(creditApplication.getUserId());
        auditHistory.setUserName(creditApplication.getUserName());
        auditHistory.setCreateTime(new Date());
        auditHistory.setUpdateTime(new Date());
        creditApplicationAuditHistoryService.save(auditHistory);
        return result;
    }

    @Override
    public CreditApplicationPage getByCurrentUser() {
        User info = userService.getInfo();
        Integer userId = info.getId();
        // 查询授信申请
        LambdaQueryWrapper<CreditApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CreditApplication::getUserId, userId);
        //最新的一条
        queryWrapper.orderByDesc(CreditApplication::getApplyTime);
        queryWrapper.last("LIMIT 1");
        CreditApplication creditApplication = this.getOne(queryWrapper);
        if (creditApplication == null) {
            return null;
        }

        log.info("获取授信申请详情，申请ID：{}，评估ID：{}", creditApplication.getId(), creditApplication.getAssessmentId());

        // 转换为VO对象
        CreditApplicationPage pageVO = new CreditApplicationPage();
        BeanUtils.copyProperties(creditApplication, pageVO);

        // 查询并设置担保人信息
        LambdaQueryWrapper<GuarantorInfo> queryWrappes = new LambdaQueryWrapper<>();
        queryWrappes.eq(GuarantorInfo::getUserId, creditApplication.getUserId());
        List<GuarantorInfo> guarantors = guarantorInfoMapper.selectList(queryWrappes);
        pageVO.setGuarantors(guarantors);

        // 查询资产评估品种信息
        if (StrUtil.isNotBlank(creditApplication.getAssessmentId())) {
            log.info("查询资产评估品种信息，评估ID：{}", creditApplication.getAssessmentId());
            List<AssetAssessmentBreeding> assessmentBreedingList = assetAssessmentBreedingService.getByAssessmentId(creditApplication.getAssessmentId());
            pageVO.setAssessmentBreedingList(assessmentBreedingList);
            log.info("查询到{}条资产评估品种信息", assessmentBreedingList != null ? assessmentBreedingList.size() : 0);
        } else {
            log.warn("授信申请未关联资产评估ID，申请ID：{}", creditApplication.getId());
            pageVO.setAssessmentBreedingList(new ArrayList<>());
        }
        return pageVO;

    }

    @Override
    public List<CreditApplicationAuditHistory> getAuditHistory(AuditStatusRequest auditStatusRequest) {
        User info = userService.getInfo();
        LambdaQueryWrapper<CreditApplicationAuditHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CreditApplicationAuditHistory::getOrganizationId, info.getOrganizationId());
        queryWrapper.eq(CreditApplicationAuditHistory::getAuditStatus, auditStatusRequest.getAuditStatus());
        return this.creditApplicationAuditHistoryService.list(queryWrapper);
    }

    @Override
    public PageInfo<CreditApplicationPage> pageAdminInfo(CreditApplicationPageInfo pageInfo) {
        // 创建分页对象
        Page<CreditApplication> page = PageHelper.startPage(pageInfo.getPageNumber(), pageInfo.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<CreditApplication> queryWrapper = new LambdaQueryWrapper<>();

        // 根据当前用户角色不同，查询条件可能不同

        // 设置其他查询条件
        if (pageInfo.getAuditStatus() != null) {
            queryWrapper.eq(CreditApplication::getAuditStatus, pageInfo.getAuditStatus());
        }
        // 按申请时间倒序排序
        queryWrapper.orderByDesc(CreditApplication::getApplyTime);

        List<CreditApplication> resultList = this.list(queryWrapper);
        List<CreditApplicationPage> resultVOList = resultList.stream().map(this::convertToPageVO).collect(Collectors.toList());
        PageInfo<CreditApplicationPage> creditApplicationPageInfo = CommonPage.copyPageInfo(page, resultVOList);
        return creditApplicationPageInfo;
    }

    /**
     * 验证授信利率是否符合系统配置的范围
     * @param creditRatio 授信利率（千分比，如15表示15‰）
     */
    private void validateCreditRateRange(BigDecimal creditRatio) {
        try {
            // 获取系统配置的最大和最小授信利率（千分比）
            String maxRateStr = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_MAX_CREDIT_RATE);
            String minRateStr = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_MIN_CREDIT_RATE);
            
            if (maxRateStr == null || maxRateStr.trim().isEmpty()) {
                log.warn("系统未配置最大授信利率，跳过利率范围验证");
                return;
            }
            
            if (minRateStr == null || minRateStr.trim().isEmpty()) {
                log.warn("系统未配置最小授信利率，跳过利率范围验证");
                return;
            }
            
            // 直接比较千分比值
            BigDecimal maxCreditRate = new BigDecimal(maxRateStr);
            BigDecimal minCreditRate = new BigDecimal(minRateStr);
            
            if (creditRatio.compareTo(maxCreditRate) > 0) {
                throw new RuntimeException(String.format("授信利率不能超过系统配置的最大值：%.1f‰（当前设置：%.1f‰）", 
                    maxCreditRate, creditRatio));
            }
            
            if (creditRatio.compareTo(minCreditRate) < 0) {
                throw new RuntimeException(String.format("授信利率不能低于系统配置的最小值：%.1f‰（当前设置：%.1f‰）", 
                    minCreditRate, creditRatio));
            }
            
            log.info("授信利率验证通过：{}‰，系统配置范围：{}‰ - {}‰", 
                creditRatio, minCreditRate, maxCreditRate);
                
        } catch (NumberFormatException e) {
            log.error("系统配置的授信利率格式错误，跳过利率范围验证", e);
        } catch (RuntimeException e) {
            // 重新抛出业务异常
            throw e;
        } catch (Exception e) {
            log.error("验证授信利率范围时发生异常，跳过验证", e);
        }
    }
}
