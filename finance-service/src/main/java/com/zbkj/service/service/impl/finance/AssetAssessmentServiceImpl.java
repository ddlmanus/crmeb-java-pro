package com.zbkj.service.service.impl.finance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.enums.AuditStatus;
import com.zbkj.common.enums.UserTypeEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.finance.*;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.vo.finance.*;
import com.zbkj.service.dao.finance.AssetAssessmentBreedingDao;
import com.zbkj.service.dao.finance.AssetAssessmentDao;
import com.zbkj.service.dao.finance.CreditApplicationDao;
import com.zbkj.service.dao.finance.GuarantorInfoDao;
import com.zbkj.service.service.finance.*;
import com.zbkj.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AssetAssessmentServiceImpl extends ServiceImpl<AssetAssessmentDao, AssetAssessment> implements AssetAssessmentService {

    @Resource
    private AssetAssessmentBreedingDao assetAssessmentBreedingDao;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private FarmInstitutionService farmInstitutionService;
    @Autowired
    private FarmValuationConfigService farmValuationConfigService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private OrgCategoryService orgCategoryService;
    @Autowired
    private GuarantorInfoDao guarantorInfoDao;
    @Autowired
    private CreditApplicationDao creditApplicationDao;
    @Autowired
    private CreditApplicationAuditHistoryService creditApplicationAuditHistoryService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrUpdateAssessment(AssetAssessmentRequestVO request) {
        // 获取当前登录用户
        User currentUser = userService.getInfo();
        if (currentUser == null) {
            throw new CrmebException("用户未登录");
        }
        //检查当前用户是合作社下下的养殖场还是管理员
        if(currentUser.getFarmType()==2){ //合作社
           //判断用户类型 员工还是管理员
           if(currentUser.getUserType().equals(UserTypeEnum.USER_TYPE_EMPLOYEE.getCode())){
               //判断合作社是否存在审核通过的授信申请
               CreditApplication creditApplication = creditApplicationDao.selectOne(new LambdaQueryWrapper<CreditApplication>()
                       .eq(CreditApplication::getOrganizationId,  currentUser.getOrganizationId())
                       .eq(CreditApplication::getAuditStatus, 2)
                       .eq(CreditApplication::getUserType, 1));
               if(creditApplication==null){
                   throw new CrmebException("合作社下没有审核通过授信申请,暂不能申请");
               }else{
                   //需要检查当前用户的申请额度是否大于养殖户的评估额度
                   List<AssetAssessmentBreeding> assetAssessmentBreedings = assetAssessmentBreedingDao.selectList(new LambdaQueryWrapper<AssetAssessmentBreeding>().eq(AssetAssessmentBreeding::getFarmCode, currentUser.getFarmCode()));
                   if(assetAssessmentBreedings.size()>0){
                       BigDecimal  creditCoefficient= assetAssessmentBreedings.stream().map(AssetAssessmentBreeding::getCreditCoefficient).reduce(BigDecimal.ZERO, BigDecimal::add);
                       //评估金额=合作社授信总额度乘以授信系数
                       BigDecimal assessmentAmount = creditApplication.getTotalAmount().multiply(creditCoefficient);
                      //判断申请金额是否大于评估金额
                          if(request.getApplyAmount().compareTo(assessmentAmount) > 0){
                            throw new CrmebException("申请额度不能大于评估额度");
                          }
                          request.setAssessmentAmount(assessmentAmount);
                   }
               }
           }
        }
        //如果当前用户是养殖户，则需要检查是否存在申请审核中或者是审核通过的
        if(currentUser.getFarmType()==1){
            List<CreditApplication> creditApplicationList = creditApplicationDao.selectList(new LambdaQueryWrapper<CreditApplication>()
                    .eq(CreditApplication::getUserId, currentUser.getId())
                    .eq(CreditApplication::getAuditStatus, AuditStatus.PENDING.getCode())
                    .or()
                    .eq(CreditApplication::getUserId, currentUser.getId())
                    .eq(CreditApplication::getAuditStatus, AuditStatus.PASS.getCode()));
            if (!creditApplicationList.isEmpty()) {
                throw new CrmebException("当前用户有申请审核中的或者审核通过的授信额度，暂不能申请");
            }
        }

        // 验证养殖品种参数（已移除详细验证，仅做基本检查）
        if (request.getBreedingProducts() == null || request.getBreedingProducts().isEmpty()) {
            throw new CrmebException("养殖品种列表不能为空");
        }

        AssetAssessment assessment;
        
        // 检查是否是修改操作
        if (StringUtils.hasText(request.getAssessmentId())) {
            assessment = this.getById(request.getAssessmentId());
            if (assessment == null) {
                throw new CrmebException("资产评估记录不存在");
            }
            // 检查是否是当前用户的评估
            if (!assessment.getUserId().equals(currentUser.getId())) {
                throw new CrmebException("无权限修改此评估记录");
            }
            // 检查评估状态
            if (assessment.getIsUsed() == 1) {
                throw new CrmebException("该评估记录已被使用，无法修改");
            }
        } else {
            // 新建评估
            assessment = new AssetAssessment();
            FarmInstitution farmInstitutionByFarmCode = farmInstitutionService.getFarmInstitutionByFarmCode(currentUser.getFarmCode());
            if(farmInstitutionByFarmCode == null){
                assessment.setAssessmentType(2);
            }else{
                Organization organization = organizationService.getById(farmInstitutionByFarmCode.getOrganizationId());
                if (organization != null) {
                    String categoryId = organization.getCategoryId();
                    OrgCategory orgCategory = orgCategoryService.getById(categoryId);
                    if (orgCategory != null) {
                         if(orgCategory.getTypeName().equals("合作社")){
                             assessment.setAssessmentType(1);
                         }else {
                             assessment.setAssessmentType(2);
                         }
                    }else {
                        assessment.setAssessmentType(2);
                    }
                }else {
                    assessment.setAssessmentType(2);
                }

            }
            assessment.setUserId(currentUser.getId());
            assessment.setUserName(currentUser.getAccount());
            assessment.setCreateTime(new Date());
        }

        // 更新评估信息
        BeanUtils.copyProperties(request, assessment);
        assessment.setUpdateTime(new Date());
        
        // 获取养殖场信息
        FarmInstitution farmInstitution = farmInstitutionService.getById(currentUser.getFarmId());
        if (farmInstitution != null) {
            assessment.setFarmInstitutionId(farmInstitution.getId());
            if (!StringUtils.hasText(assessment.getFarmName())) {
                assessment.setFarmName(farmInstitution.getFarmName());
            }
            if (!StringUtils.hasText(assessment.getManagerName())) {
                assessment.setManagerName(farmInstitution.getContactName());
            }
            if (!StringUtils.hasText(assessment.getManagerPhone())) {
                assessment.setManagerPhone(farmInstitution.getContactPhone());
            }
        }
        
        // 设置评估状态：1-已提交
        assessment.setAssessmentStatus(1);
        assessment.setIsUsed(0);
        assessment.setDeleteFlag(0);
        
        // 保存评估记录
        this.saveOrUpdate(assessment);
        
        // 删除原有的养殖品种记录
        if (StringUtils.hasText(request.getAssessmentId())) {
            LambdaQueryWrapper<AssetAssessmentBreeding> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(AssetAssessmentBreeding::getAssessmentId, assessment.getId());
            assetAssessmentBreedingDao.delete(deleteWrapper);
        }
        
        // 保存养殖品种信息 - 传入多少条保存多少条，每条单独计算授信系数
        if (request.getBreedingProducts() != null && !request.getBreedingProducts().isEmpty()) {
            
            // 1. 计算所有数据的总存栏量
            int totalStockQuantity = request.getBreedingProducts().stream()
                .mapToInt(FarmBreedType::getStockQuantity)
                .sum();
            
            log.info("开始处理资产评估养殖品种数据，总共{}条记录，总存栏量：{}", 
                    request.getBreedingProducts().size(), totalStockQuantity);
            
            // 2. 遍历每条数据，分别保存并计算授信系数
            for (int i = 0; i < request.getBreedingProducts().size(); i++) {
                FarmBreedType breeding = request.getBreedingProducts().get(i);
                
                // 3. 计算该条记录的授信系数（该条存栏量 / 总存栏量）
                BigDecimal creditCoefficient = BigDecimal.ZERO;
                if (totalStockQuantity > 0) {
                    creditCoefficient = new BigDecimal(breeding.getStockQuantity())
                        .divide(new BigDecimal(totalStockQuantity), 4, RoundingMode.HALF_UP);
                }
                
                // 4. 获取评估价格配置
                BigDecimal assessmentPrice = new BigDecimal("1.00");
                String assessmentUnit = "头";
                
                try {
                    FarmValuationConfig config = farmValuationConfigService.getByBreedingType(
                        breeding.getFarmCode(), 
                        breeding.getBreedName(), 
                        breeding.getBreedType() != null ? breeding.getBreedType().toString() : null);
                    if (config != null) {
                        assessmentUnit = config.getUnitName();
                        assessmentPrice = config.getUnitPrice();
                    }
                } catch (Exception e) {
                    log.warn("获取养殖品种{}的评估配置失败，使用默认值：{}", breeding.getBreedName(), e.getMessage());
                }
                
                // 5. 计算评估总价 = 存栏数量 * 单价
                BigDecimal assessmentTotalPrice = new BigDecimal(breeding.getStockQuantity())
                    .multiply(assessmentPrice);
                
                // 6. 创建并保存每条养殖品种记录
                AssetAssessmentBreeding breedingRecord = new AssetAssessmentBreeding();
                breedingRecord.setAssessmentId(assessment.getId());
                breedingRecord.setFarmCode(breeding.getFarmCode());
                breedingRecord.setFarmName(breeding.getFarmName());
                breedingRecord.setBreedName(breeding.getBreedName());
                breedingRecord.setBreedType(breeding.getBreedType() != null ? breeding.getBreedType().toString() : null);
                breedingRecord.setStockQuantity(breeding.getStockQuantity());
                breedingRecord.setAssessmentUnit(assessmentUnit);
                breedingRecord.setAssessmentPrice(assessmentPrice);
                breedingRecord.setAssessmentTotalPrice(assessmentTotalPrice);
                breedingRecord.setCreditCoefficient(creditCoefficient); // 该条记录的授信系数
                breedingRecord.setCreateTime(new Date());
                breedingRecord.setUpdateTime(new Date());
                
                assetAssessmentBreedingDao.insert(breedingRecord);
                
                log.info("保存第{}条记录：{} - {} - {} - 存栏量：{} - 授信系数：{}", 
                        i + 1, breeding.getFarmName(), breeding.getBreedName(), 
                        breeding.getBreedType(), breeding.getStockQuantity(), creditCoefficient);
            }
            
            log.info("资产评估养殖品种数据处理完成，共保存{}条记录", request.getBreedingProducts().size());
        }

        //如果当前用户是养殖户 ，需要保存担保人信息和申请记录
        if (currentUser.getFarmType() == 1|| currentUser.getFarmType() == 2&&currentUser.getUserType().equals(UserTypeEnum.USER_TYPE_EMPLOYEE.getCode())) {
            //保存申请记录
            CreditApplication creditApplication = new CreditApplication();
            creditApplication.setUserId(currentUser.getId());
            creditApplication.setUserType(currentUser.getUserType());
            creditApplication.setApplyType(0);
            creditApplication.setApplyAmount(request.getApplyAmount());
            creditApplication.setApplyImages(request.getApplyImages());
            creditApplication.setApplyTime(new Date());
            creditApplication.setApplyStatus(1);
            creditApplication.setAuditStatus(AuditStatus.PENDING.getCode());
            creditApplication.setUserName(currentUser.getRealName());
            creditApplication.setIdNumber(request.getIdNumber());
            creditApplication.setManagerName(request.getManagerName());
            creditApplication.setManagerPhone(request.getManagerPhone());
            creditApplication.setOrganizationId(currentUser.getOrganizationId());
            creditApplication.setFarmType(currentUser.getFarmType());
            creditApplication.setFarmName(currentUser.getFarmName());
            creditApplication.setAssessmentId(assessment.getId());
            creditApplication.setCreditCode(request.getCreditCode());
            creditApplicationDao.insert(creditApplication);

            // 保存担保人信息
            if(CollectionUtils.isNotEmpty( request.getGuarantors())){
                request.getGuarantors().forEach(guarantor -> {
                    GuarantorInfo guarantorInfo = new GuarantorInfo();
                    BeanUtils.copyProperties(guarantor, guarantorInfo);
                    guarantorInfoDao.insert(guarantorInfo);
                });
            }

            //更新用户申请金额
           currentUser.setApplyAmount(request.getApplyAmount());
           currentUser.setCreditStatus(0);
           currentUser.setAssessmentAmount(request.getAssessmentAmount());
           userService.updateById(currentUser);
            // 标记资产评估为已使用
            this.markAssessmentAsUsed(assessment.getId());

            CreditApplicationAuditHistory auditHistory = new CreditApplicationAuditHistory();
            auditHistory.setApplicationId(creditApplication.getId());
            auditHistory.setAuditStatus(AuditStatus.PENDING.getCode());
            auditHistory.setAuditRemark("");
            auditHistory.setAuditorId(currentUser.getId());
            auditHistory.setAuditorName(currentUser.getNickname());
            auditHistory.setApplyAmount(creditApplication.getApplyAmount());
            auditHistory.setUserId(creditApplication.getUserId());
            auditHistory.setUserName(creditApplication.getUserName());
            auditHistory.setCreateTime(new Date());
            auditHistory.setUpdateTime(new Date());
            auditHistory.setOrganizationId(currentUser.getOrganizationId());
            creditApplicationAuditHistoryService.save(auditHistory);
        }
        return assessment.getId();
    }



    @Override
    public AssetAssessmentDetailVO getAssessmentDetail(String assessmentId) {
        AssetAssessment assessment = this.getById(assessmentId);
        if (assessment == null || assessment.getDeleteFlag() == 1) {
            throw new CrmebException("资产评估记录不存在");
        }
        
        AssetAssessmentDetailVO detailVO = new AssetAssessmentDetailVO();
        BeanUtils.copyProperties(assessment, detailVO);
        // ID字段已通过BeanUtils.copyProperties自动映射
        
        // 获取养殖品种信息
        LambdaQueryWrapper<AssetAssessmentBreeding> breedingWrapper = new LambdaQueryWrapper<>();
        breedingWrapper.eq(AssetAssessmentBreeding::getAssessmentId, assessmentId);
        List<AssetAssessmentBreeding> breedingList = assetAssessmentBreedingDao.selectList(breedingWrapper);
        
        List<BreedingRequest> breedingRequests = breedingList.stream().map(breeding -> {
            BreedingRequest request = new BreedingRequest();
            request.setBreedName(breeding.getBreedName());
            request.setBreedType(breeding.getBreedType());
            request.setStockQuantity(breeding.getStockQuantity());
            return request;
        }).collect(Collectors.toList());
        
        detailVO.setBreedingProducts(breedingRequests);
        
        return detailVO;
    }

    @Override
    public IPage<AssetAssessmentPageVO> pageAssessment(AssetAssessmentPageRequest pageRequest) {
        // 获取当前登录用户

        User currentUser = userService.getInfo();
        if (currentUser == null) {
            throw new CrmebException("用户未登录");
        }
        Page<AssetAssessment> page = new Page<>(pageRequest.getPageNumber(), pageRequest.getPageSize());
        LambdaQueryWrapper<AssetAssessment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AssetAssessment::getUserId, currentUser.getId());
        queryWrapper.eq(AssetAssessment::getDeleteFlag, 0);
        
        // 条件查询
        if (pageRequest.getAssessmentStatus() != null) {
            queryWrapper.eq(AssetAssessment::getAssessmentStatus, pageRequest.getAssessmentStatus());
        }
        if (pageRequest.getIsUsed() != null) {
            queryWrapper.eq(AssetAssessment::getIsUsed, pageRequest.getIsUsed());
        }
        if (StringUtils.hasText(pageRequest.getFarmName())) {
            queryWrapper.like(AssetAssessment::getFarmName, pageRequest.getFarmName());
        }
        
        queryWrapper.orderByDesc(AssetAssessment::getCreateTime);
        
        IPage<AssetAssessment> assessmentPage = this.page(page, queryWrapper);
        
        // 转换为VO
        IPage<AssetAssessmentPageVO> resultPage = assessmentPage.convert(assessment -> {
            AssetAssessmentPageVO pageVO = new AssetAssessmentPageVO();
            BeanUtils.copyProperties(assessment, pageVO);
            // ID字段已通过BeanUtils.copyProperties自动映射
            return pageVO;
        });
        
        return resultPage;
    }
    @Override
    public String getAvailableAssessmentByUser(Integer userId) {
        LambdaQueryWrapper<AssetAssessment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AssetAssessment::getUserId, userId);
        queryWrapper.eq(AssetAssessment::getAssessmentStatus, 1); // 已提交
        queryWrapper.eq(AssetAssessment::getIsUsed, 0); // 未使用
        queryWrapper.eq(AssetAssessment::getDeleteFlag, 0);
        queryWrapper.orderByDesc(AssetAssessment::getCreateTime);
        queryWrapper.last("LIMIT 1");
        
        AssetAssessment assessment = this.getOne(queryWrapper);
        return assessment != null ? assessment.getId() : null;
    }

    @Override
    public void markAssessmentAsUsed(String assessmentId) {
        AssetAssessment assessment = this.getById(assessmentId);
        if (assessment != null) {
            assessment.setIsUsed(1);
            assessment.setUpdateTime(new Date());
            this.updateById(assessment);
        }
    }

    @Override
    public PageInfo<AssetAssessmentPageVO> getPageForAdmin(AssetAssessmentSearchVO searchVO) {
        com.github.pagehelper.Page<AssetAssessment> page = PageHelper.startPage(searchVO.getPage(), searchVO.getLimit());

        LambdaQueryWrapper<AssetAssessment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AssetAssessment::getDeleteFlag, 0);
        
        // 条件查询
        if (StringUtils.hasText(searchVO.getKeywords())) {
            queryWrapper.and(wrapper -> wrapper
                .like(AssetAssessment::getUserName, searchVO.getKeywords())
                .or()
                .like(AssetAssessment::getFarmName, searchVO.getKeywords())
            );
        }
        if (searchVO.getUserId() != null) {
            queryWrapper.eq(AssetAssessment::getUserId, searchVO.getUserId());
        }
        if (StringUtils.hasText(searchVO.getUserName())) {
            queryWrapper.like(AssetAssessment::getUserName, searchVO.getUserName());
        }
        if (StringUtils.hasText(searchVO.getFarmName())) {
            queryWrapper.like(AssetAssessment::getFarmName, searchVO.getFarmName());
        }
        if (searchVO.getFarmInstitutionId() != null) {
            queryWrapper.eq(AssetAssessment::getFarmInstitutionId, searchVO.getFarmInstitutionId());
        }
        if (searchVO.getAssessmentType() != null) {
            queryWrapper.eq(AssetAssessment::getAssessmentType, searchVO.getAssessmentType());
        }
        if (searchVO.getAssessmentStatus() != null) {
            queryWrapper.eq(AssetAssessment::getAssessmentStatus, searchVO.getAssessmentStatus());
        }
        if (searchVO.getIsUsed() != null) {
            queryWrapper.eq(AssetAssessment::getIsUsed, searchVO.getIsUsed());
        }
        // 时间查询暂时简化处理
        // if (StringUtils.hasText(searchVO.getStartTime())) {
        //     queryWrapper.ge(AssetAssessment::getCreateTime, searchVO.getStartTime());
        // }
        // if (StringUtils.hasText(searchVO.getEndTime())) {
        //     queryWrapper.le(AssetAssessment::getCreateTime, searchVO.getEndTime());
        // }
        
        // 排序
        queryWrapper.orderByDesc(AssetAssessment::getCreateTime);
        List<AssetAssessment> list = this.list(queryWrapper);
        List<AssetAssessmentPageVO> pageVOList = list.stream().map(assetAssessment -> {
            AssetAssessmentPageVO pageVO = new AssetAssessmentPageVO();
            BeanUtils.copyProperties(assetAssessment, pageVO);
            return pageVO;
        }).collect(Collectors.toList());
        PageInfo<AssetAssessmentPageVO> assetAssessmentPageInfo = CommonPage.copyPageInfo(page, pageVOList);
        return assetAssessmentPageInfo;
    }

    @Override
    public AssetAssessmentDetailVO getAssessmentByCreditApplication(String applicationId) {
        // 通过授信申请ID查找对应的资产评估
        // 这里需要根据业务逻辑来实现，假设存在关联关系
        // 暂时返回null，具体实现需要根据数据库设计
        return null;
    }

    @Override
    public Object getAssessmentStatistics() {
        // 统计资产评估相关数据
        Map<String, Object> statistics = new HashMap<>();
        
        // 总评估数量
        long totalCount = this.count(new LambdaQueryWrapper<AssetAssessment>()
            .eq(AssetAssessment::getDeleteFlag, 0));
        statistics.put("totalCount", totalCount);
        
        // 已提交评估数量
        long submittedCount = this.count(new LambdaQueryWrapper<AssetAssessment>()
            .eq(AssetAssessment::getDeleteFlag, 0)
            .eq(AssetAssessment::getAssessmentStatus, 1));
        statistics.put("submittedCount", submittedCount);
        
        // 已使用评估数量
        long usedCount = this.count(new LambdaQueryWrapper<AssetAssessment>()
            .eq(AssetAssessment::getDeleteFlag, 0)
            .eq(AssetAssessment::getIsUsed, 1));
        statistics.put("usedCount", usedCount);
        
        // 未使用评估数量
        long availableCount = this.count(new LambdaQueryWrapper<AssetAssessment>()
            .eq(AssetAssessment::getDeleteFlag, 0)
            .eq(AssetAssessment::getAssessmentStatus, 1)
            .eq(AssetAssessment::getIsUsed, 0));
        statistics.put("availableCount", availableCount);
        
        return statistics;
    }

    @Override
    public AssetAssessmentPageVO getLatestAssessmentByCurrentUser() {
        // 获取当前登录用户
        User currentUser = userService.getInfo();
        if (currentUser == null) {
            throw new CrmebException("用户未登录");
        }
        
        LambdaQueryWrapper<AssetAssessment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AssetAssessment::getUserId, currentUser.getId());
        queryWrapper.eq(AssetAssessment::getDeleteFlag, 0);
        queryWrapper.orderByDesc(AssetAssessment::getCreateTime);
        queryWrapper.last("LIMIT 1");
        
        AssetAssessment assessment = this.getOne(queryWrapper);
        if (assessment == null) {
            return null;
        }
        
        AssetAssessmentPageVO pageVO = new AssetAssessmentPageVO();
        BeanUtils.copyProperties(assessment, pageVO);
        return pageVO;
    }


} 