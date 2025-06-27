package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.enums.FarmTypeEnum;
import com.zbkj.common.enums.UserTypeEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.employee.Employee;
import com.zbkj.common.model.finance.AssetAssessmentBreeding;
import com.zbkj.common.model.finance.CreditApplication;
import com.zbkj.common.model.finance.FarmInstitution;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.employee.EmployeePageRequest;
import com.zbkj.common.request.employee.EmployeeRequest;
import com.zbkj.common.response.employee.EmployeeResponse;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.finance.EmployeeCreditAllocationRequestVO;
import com.zbkj.service.dao.EmployeeDao;
import com.zbkj.service.service.EmployeeService;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.finance.AssetAssessmentBreedingService;
import com.zbkj.service.service.finance.CreditApplicationService;
import com.zbkj.service.service.finance.FarmInstitutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 员工表 服务实现类 - 专门管理养殖场管理员信息
 * </p>
 *
 * @author zbkj
 * @since 2024-12-19
 */
@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeDao, Employee> implements EmployeeService {

    @Resource
    private EmployeeDao employeeDao;

    @Autowired
    private UserService userService;

    @Autowired
    private CreditApplicationService creditApplicationService;
    
    @Autowired
    private FarmInstitutionService farmInstitutionService;
    @Autowired
    private AssetAssessmentBreedingService assetAssessmentBreedingService;

    @Override
    public IPage<EmployeeResponse> getEmployeePage(EmployeePageRequest request) {
        Page<EmployeeResponse> page = new Page<>(request.getPage(), request.getLimit());
        
        List<EmployeeResponse> list = employeeDao.getEmployeeList(
                request.getKeywords(),
                request.getStatus(),
                request.getDepartment(),
                request.getPosition(),
                request.getGender(),
                request.getFarmType(),
                request.getFarmName(),
                request.getCompanyName(),
                request.getBreedingVariety()
        );
        
        // 手动分页
        int total = list.size();
        int startIndex = (request.getPage() - 1) * request.getLimit();
        int endIndex = Math.min(startIndex + request.getLimit(), total);
        
        if (startIndex < total) {
            list = list.subList(startIndex, endIndex);
        } else {
            list.clear();
        }
        
        page.setRecords(list);
        page.setTotal(total);
        
        return page;
    }

    @Override
    public EmployeeResponse getEmployeeDetail(Integer id) {
        if (ObjectUtil.isNull(id)) {
            throw new CrmebException("员工ID不能为空");
        }
        return employeeDao.getEmployeeDetail(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createEmployee(EmployeeRequest request) {
        log.info("开始创建养殖场管理员，请求参数：farmId={}, name={}, phone={}", 
                request.getFarmId(), request.getName(), request.getPhone());
        
        // 基础数据验证
        validateEmployeeRequest(request);
        
        // 1. 根据养殖场ID获取养殖场信息
        FarmInstitution farmInstitution = farmInstitutionService.getById(request.getFarmId());
        if (farmInstitution == null) {
            throw new CrmebException("养殖场不存在");
        }
        
        log.info("获取到养殖场信息：{}", farmInstitution.getFarmName());
        
        // 2. 检查该养殖场是否已有员工记录
        Employee existingEmployee = getEmployeeByFarmId(request.getFarmId());
        if (existingEmployee != null) {
            throw new CrmebException("该养殖场已存在管理员：" + existingEmployee.getName());
        }
        
        // 3. 检查该养殖场是否已有用户记录
        User existingUser = getFarmAdminUser(farmInstitution);
        User adminUser;
        
        if (existingUser != null) {
            log.info("养殖场{}已存在用户记录，将基于现有用户创建员工记录", farmInstitution.getFarmName());
            // 如果存在用户但手机号不匹配，更新用户信息
            if (!existingUser.getPhone().equals(request.getPhone())) {
                // 检查新手机号是否被其他用户使用
                checkPhoneNotUsedByOtherUser(request.getPhone(), existingUser.getId());
                existingUser.setPhone(request.getPhone());
                existingUser.setRealName(request.getName());
                existingUser.setNickname(request.getName());
                if (StrUtil.isNotBlank(request.getPassword())) {
                    existingUser.setPwd(CrmebUtil.encryptPassword(request.getPassword(), existingUser.getAccount()));
                }
                existingUser.setUpdateTime(new Date());
                userService.updateById(existingUser);
            }
            adminUser = existingUser;
        } else {
            log.info("养殖场{}不存在用户记录，创建新的管理员用户", farmInstitution.getFarmName());
            // 创建新的管理员用户账号
            adminUser = createAdminUserAccount(request, farmInstitution);
        }
        
        // 4. 计算授信额度和系数
        CreditInfo creditInfo = calculateCreditLimit(farmInstitution);
        
        // 5. 生成员工工号
        String employeeNo = generateEmployeeNo();
        
        // 6. 创建员工记录（实际是管理员记录）
        Employee employee = new Employee();
        employee.setOrganizationId(userService.getInfo().getOrganizationId());
        employee.setUserId(adminUser.getId());
        employee.setEmployeeNo(employeeNo);
        employee.setFarmType(1); // 养殖场类型
        employee.setName(request.getName());
        employee.setPhone(request.getPhone());
        employee.setIdCard(request.getIdCard());
        employee.setFarmId(request.getFarmId());
        employee.setFarmCode(farmInstitution.getFarmCode());
        employee.setFarmName(farmInstitution.getFarmName());
        employee.setStatus(1); // 默认在职状态
        employee.setCreateTime(new Date());
        employee.setUpdateTime(new Date());
        employee.setCreateBy(getCurrentUserId());
        employee.setUpdateBy(getCurrentUserId());
        employee.setIsDeleted(false);
        employee.setConsumedAmount(BigDecimal.ZERO);
        employee.setCreditLimit(BigDecimal.ZERO); // 创建员工时授信额度为0
        employee.setCreditCoefficient(creditInfo.getCreditCoefficient());
        employee.setAssessmentAmount(creditInfo.getAssessmentAmount());
        employee.setTotalStockQuantity(creditInfo.getTotalStockQuantity());
        employee.setBreedingVarietyNames(creditInfo.getBreedingVarietyNames());
        employee.setBreedingVarietyTypes(creditInfo.getBreedingVarietyTypes());
        
        boolean result = save(employee);
        
        // 更新用户表的授信信息
        updateUserCreditInfoForEmployee(adminUser, creditInfo);
        
        log.info("养殖场管理员创建{}，员工工号：{}，授信额度：{}，授信系数：{}，评估金额：{}，总存栏量：{}，养殖品种：{}，品种类型：{}", 
                result ? "成功" : "失败", employeeNo, BigDecimal.ZERO, 
                creditInfo.getCreditCoefficient(), creditInfo.getAssessmentAmount(),
                creditInfo.getTotalStockQuantity(), creditInfo.getBreedingVarietyNames(), 
                creditInfo.getBreedingVarietyTypes());
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateEmployee(EmployeeRequest request) {
        log.info("开始编辑养殖场管理员，请求参数：id={}, farmId={}, name={}, phone={}", 
                request.getId(), request.getFarmId(), request.getName(), request.getPhone());
        
        if (request.getId() == null) {
            throw new CrmebException("员工ID不能为空");
        }
        
        // 获取原员工信息
        Employee existEmployee = getById(request.getId());
        if (existEmployee == null || existEmployee.getIsDeleted()) {
            throw new CrmebException("员工不存在");
        }
        
        // 基础数据验证
        validateEmployeeRequest(request);
        
        // 验证手机号是否已被其他员工使用
        checkPhoneExists(request.getPhone(), request.getId());
        
        // 根据养殖场ID获取养殖场信息
        FarmInstitution farmInstitution = farmInstitutionService.getById(request.getFarmId());
        if (farmInstitution == null) {
            throw new CrmebException("养殖场不存在");
        }
        
        // 更新员工关联的用户信息
        updateAdminUserAccount(request, existEmployee.getUserId(), farmInstitution);
        
        // 更新员工信息
        existEmployee.setName(request.getName());
        existEmployee.setPhone(request.getPhone());
        existEmployee.setFarmId(request.getFarmId());
        existEmployee.setFarmCode(farmInstitution.getFarmCode());
        existEmployee.setFarmName(farmInstitution.getFarmName());
        existEmployee.setUpdateTime(new Date());
        existEmployee.setUpdateBy(getCurrentUserId());
        
        boolean result = updateById(existEmployee);
        log.info("养殖场管理员编辑{}", result ? "成功" : "失败");
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteEmployee(Integer id) {
        if (ObjectUtil.isNull(id)) {
            throw new CrmebException("员工ID不能为空");
        }
        
        Employee employee = getById(id);
        if (ObjectUtil.isNull(employee) || employee.getIsDeleted()) {
            throw new CrmebException("员工不存在");
        }
        
        // 检查是否存在相关的授信记录
        if (employee.getUserId() != null) {
            LambdaQueryWrapper<CreditApplication> creditWrapper = new LambdaQueryWrapper<>();
            creditWrapper.eq(CreditApplication::getUserId, employee.getUserId());
            creditWrapper.eq(CreditApplication::getAuditStatus, 2); // 审核通过
            long creditCount = creditApplicationService.count(creditWrapper);
            if (creditCount > 0) {
                throw new CrmebException("该管理员存在有效的授信记录，无法删除");
            }
        }
        
        // 逻辑删除员工
        employee.setIsDeleted(true);
        employee.setUpdateTime(new Date());
        employee.setUpdateBy(getCurrentUserId());
        boolean employeeResult = updateById(employee);
        
        // 禁用关联的用户账号
        if (ObjectUtil.isNotNull(employee.getUserId())) {
            User user = userService.getById(employee.getUserId());
            if (ObjectUtil.isNotNull(user)) {
                user.setStatus(false);
                userService.updateById(user);
            }
        }
        
        return employeeResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteEmployee(String ids) {
        if (StrUtil.isBlank(ids)) {
            throw new CrmebException("员工ID不能为空");
        }
        
        List<String> idList = Arrays.asList(ids.split(","));
        for (String idStr : idList) {
            deleteEmployee(Integer.valueOf(idStr));
        }
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateEmployeeStatus(Integer id, Integer status) {
        if (ObjectUtil.isNull(id)) {
            throw new CrmebException("员工ID不能为空");
        }
        
        Employee employee = getById(id);
        if (ObjectUtil.isNull(employee) || employee.getIsDeleted()) {
            throw new CrmebException("员工不存在");
        }
        
        // 更新员工状态
        employee.setStatus(status);
        employee.setUpdateTime(new Date());
        employee.setUpdateBy(getCurrentUserId());
        
        boolean employeeResult = updateById(employee);
        
        // 同步更新用户状态
        if (ObjectUtil.isNotNull(employee.getUserId())) {
            User user = userService.getById(employee.getUserId());
            if (ObjectUtil.isNotNull(user)) {
                user.setStatus(status == 1 || status == 2); // 在职或试用期时启用用户
                userService.updateById(user);
            }
        }
        
        return employeeResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateEmployeeCreditLimit(Integer id, BigDecimal creditLimit, BigDecimal creditCoefficient) {
        if (ObjectUtil.isNull(id)) {
            throw new CrmebException("员工ID不能为空");
        }
        
        if (creditLimit != null && creditLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new CrmebException("授信额度不能为负数");
        }
        
        if (creditCoefficient != null && (creditCoefficient.compareTo(BigDecimal.ZERO) < 0 || creditCoefficient.compareTo(BigDecimal.ONE) > 0)) {
            throw new CrmebException("授信系数必须在0-1之间");
        }
        
        Employee employee = getById(id);
        if (ObjectUtil.isNull(employee) || employee.getIsDeleted()) {
            throw new CrmebException("员工不存在");
        }
        
        // 更新员工授信信息
        employee.setCreditLimit(creditLimit);
        employee.setCreditCoefficient(creditCoefficient);
        employee.setUpdateTime(new Date());
        employee.setUpdateBy(getCurrentUserId());
        
        return updateById(employee);
    }

    /**
     * 根据养殖场ID查找已存在的员工（管理员）
     */
    private Employee getEmployeeByFarmId(Integer farmId) {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getFarmId, farmId);
        wrapper.eq(Employee::getIsDeleted, false);
        wrapper.last("LIMIT 1");
        
        return this.getOne(wrapper);
    }

    /**
     * 获取养殖场管理员用户
     */
    private User getFarmAdminUser(FarmInstitution farmInstitution) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getFarmId, farmInstitution.getId());
        wrapper.eq(User::getUserType, 1); // 管理员类型
        wrapper.eq(User::getStatus, true);
        wrapper.orderByDesc(User::getCreateTime);
        wrapper.last("LIMIT 1");
        
        return userService.getOne(wrapper);
    }

    /**
     * 检查手机号是否被其他用户使用
     */
    private void checkPhoneNotUsedByOtherUser(String phone, Integer excludeUserId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        wrapper.ne(User::getId, excludeUserId);
        
        User existingUser = userService.getOne(wrapper);
        if (existingUser != null) {
            throw new CrmebException("手机号" + phone + "已被其他用户使用");
        }
    }

    /**
     * 计算授信额度和系数
     */
    private CreditInfo calculateCreditLimit(FarmInstitution farmInstitution) {
        log.info("开始计算养殖场{}的授信额度", farmInstitution.getFarmName());
        User info = userService.getInfo();
        CreditInfo creditInfo = new CreditInfo();
        
        try {
            // 1. 根据机构ID查询最新审核通过的授信记录（可能存在多条，取最新的）
            LambdaQueryWrapper<CreditApplication> creditWrapper = new LambdaQueryWrapper<>();
            creditWrapper.eq(CreditApplication::getOrganizationId, info.getOrganizationId());
            creditWrapper.eq(CreditApplication::getAuditStatus, 2); // 审核通过
            creditWrapper.orderByDesc(CreditApplication::getAuditTime);
            creditWrapper.last("LIMIT 1");
            
            CreditApplication latestCreditApplication = creditApplicationService.getOne(creditWrapper);
            
            if (latestCreditApplication == null) {
                log.warn("养殖场{}暂无授信通过的记录，使用默认值", farmInstitution.getFarmName());
                creditInfo.setCreditLimit(BigDecimal.ZERO);
                creditInfo.setCreditCoefficient(BigDecimal.ZERO);
                creditInfo.setAssessmentAmount(BigDecimal.ZERO);
                creditInfo.setTotalStockQuantity(0);
                creditInfo.setBreedingVarietyNames("");
                creditInfo.setBreedingVarietyTypes("");
                return creditInfo;
            }
            
            log.info("找到养殖场{}的最新授信记录，申请ID：{}，总额度：{}", 
                    farmInstitution.getFarmName(), latestCreditApplication.getId(), latestCreditApplication.getTotalAmount());
            
            // 2. 根据养殖场编码查询该养殖场的所有资产评估记录
            LambdaQueryWrapper<AssetAssessmentBreeding> breedingWrapper = new LambdaQueryWrapper<>();
            breedingWrapper.eq(AssetAssessmentBreeding::getFarmCode, farmInstitution.getFarmCode());
            breedingWrapper.orderByDesc(AssetAssessmentBreeding::getCreateTime);
            
            List<AssetAssessmentBreeding> breedingList = assetAssessmentBreedingService.list(breedingWrapper);
            
            if (breedingList.isEmpty()) {
                log.warn("养殖场{}（编码：{}）暂无资产评估品种信息，使用默认系数", 
                        farmInstitution.getFarmName(), farmInstitution.getFarmCode());
                creditInfo.setCreditLimit(latestCreditApplication.getTotalAmount().multiply(new BigDecimal("0.1"))); // 默认10%
                creditInfo.setCreditCoefficient(new BigDecimal("0.1"));
                creditInfo.setAssessmentAmount(BigDecimal.ZERO);
                creditInfo.setTotalStockQuantity(0);
                creditInfo.setBreedingVarietyNames("");
                creditInfo.setBreedingVarietyTypes("");
                return creditInfo;
            }
            
            // 3. 汇总该养殖场的所有授信系数、评估金额、存栏量和品种信息
            BigDecimal totalCreditCoefficient = breedingList.stream()
                .map(AssetAssessmentBreeding::getCreditCoefficient)
                .filter(coefficient -> coefficient != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            BigDecimal totalAssessmentAmount = breedingList.stream()
                .map(AssetAssessmentBreeding::getAssessmentTotalPrice)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 计算总存栏量
            Integer totalStockQuantity = breedingList.stream()
                .map(AssetAssessmentBreeding::getStockQuantity)
                .filter(quantity -> quantity != null)
                .mapToInt(Integer::intValue)
                .sum();
            
            // 拼接养殖品种名称（去重）
            String breedingVarietyNames = breedingList.stream()
                .map(AssetAssessmentBreeding::getBreedName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .distinct()
                .collect(Collectors.joining("、"));
            
            // 拼接养殖品种类型（去重）
            String breedingVarietyTypes = breedingList.stream()
                .map(AssetAssessmentBreeding::getBreedType)
                .filter(type -> type != null && !type.trim().isEmpty())
                .distinct()
                .collect(Collectors.joining("、"));

            
            // 4. 计算授信额度 = 机构授信额度 × 该养殖场的总授信系数
            BigDecimal creditLimit = latestCreditApplication.getTotalAmount().multiply(totalCreditCoefficient);
            
            creditInfo.setCreditLimit(creditLimit);
            creditInfo.setCreditCoefficient(totalCreditCoefficient);
            creditInfo.setAssessmentAmount(totalAssessmentAmount);
            creditInfo.setTotalStockQuantity(totalStockQuantity);
            creditInfo.setBreedingVarietyNames(breedingVarietyNames);
            creditInfo.setBreedingVarietyTypes(breedingVarietyTypes);
            
            log.info("养殖场{}授信计算完成，基于养殖场编码：{}，授信额度：{}，授信系数：{}，评估金额：{}，总存栏量：{}，养殖品种：{}，品种类型：{}，养殖品种数量：{}", 
                    farmInstitution.getFarmName(), farmInstitution.getFarmCode(), 
                    creditLimit, totalCreditCoefficient, totalAssessmentAmount, totalStockQuantity, 
                    breedingVarietyNames, breedingVarietyTypes, breedingList.size());
            
            // 详细日志记录每个养殖品种的系数和评估金额
            for (AssetAssessmentBreeding breeding : breedingList) {
                log.debug("养殖品种：{} - {}，授信系数：{}，评估金额：{}", 
                    breeding.getBreedName(), breeding.getBreedType(), 
                    breeding.getCreditCoefficient(), breeding.getAssessmentTotalPrice());
            }
            
        } catch (Exception e) {
            log.error("计算养殖场{}授信额度时发生异常：{}", farmInstitution.getFarmName(), e.getMessage(), e);
            // 异常情况下使用默认值
            creditInfo.setCreditLimit(BigDecimal.ZERO);
            creditInfo.setCreditCoefficient(BigDecimal.ZERO);
            creditInfo.setAssessmentAmount(BigDecimal.ZERO);
            creditInfo.setTotalStockQuantity(0);
            creditInfo.setBreedingVarietyNames("");
            creditInfo.setBreedingVarietyTypes("");
        }
        
        return creditInfo;
    }

    /**
     * 授信信息内部类
     */
    private static class CreditInfo {
        private BigDecimal creditLimit = BigDecimal.ZERO;
        private BigDecimal creditCoefficient = BigDecimal.ZERO;
        private BigDecimal assessmentAmount = BigDecimal.ZERO;
        //总存栏量
        private Integer totalStockQuantity = 0;
        //养殖品种名称（拼接）
        private String breedingVarietyNames;
        //养殖品种类型（拼接）
        private String breedingVarietyTypes;

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

        public BigDecimal getCreditLimit() {
            return creditLimit;
        }
        
        public void setCreditLimit(BigDecimal creditLimit) {
            this.creditLimit = creditLimit;
        }
        
        public BigDecimal getCreditCoefficient() {
            return creditCoefficient;
        }
        
        public void setCreditCoefficient(BigDecimal creditCoefficient) {
            this.creditCoefficient = creditCoefficient;
        }
        
        public BigDecimal getAssessmentAmount() {
            return assessmentAmount;
        }
        
        public void setAssessmentAmount(BigDecimal assessmentAmount) {
            this.assessmentAmount = assessmentAmount;
        }
    }

    /**
     * 创建管理员用户账号
     */
    private User createAdminUserAccount(EmployeeRequest request, FarmInstitution farmInstitution) {
        User info = userService.getInfo();
        // 检查手机号是否已被使用
        LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
        phoneWrapper.eq(User::getPhone, request.getPhone());
        User existingPhoneUser = userService.getOne(phoneWrapper);
        if (existingPhoneUser != null) {
            throw new CrmebException("手机号" + request.getPhone() + "已被其他用户使用");
        }
        
        // 生成用户账号（使用手机号作为账号）
        String account = request.getPhone();
        
        // 检查账号是否已存在
        LambdaQueryWrapper<User> accountWrapper = new LambdaQueryWrapper<>();
        accountWrapper.eq(User::getAccount, account);
        User existingAccountUser = userService.getOne(accountWrapper);
        if (existingAccountUser != null) {
            throw new CrmebException("账号" + account + "已存在");
        }
        
        User user = new User();
        user.setAccount(account);
        user.setPwd(CrmebUtil.encryptPassword(request.getPassword(), account));
        user.setRealName(request.getName());
        user.setNickname(request.getName());
        user.setPhone(request.getPhone());
        user.setUserType(UserTypeEnum.USER_TYPE_EMPLOYEE.getCode()); //员工
        user.setFarmType(FarmTypeEnum.TENANT_TYPE_COOPERATIVE.getCode()); // 养殖场类型
        user.setStatus(true);
        user.setFarmId(farmInstitution.getId());
        user.setFarmCode(farmInstitution.getFarmCode());
        user.setFarmName(farmInstitution.getFarmName());
        user.setOrganizationId(farmInstitution.getOrganizationId());
        user.setOrganizationName(farmInstitution.getOrganizationName());
        user.setOrganizationCode(farmInstitution.getOrganizationCode());
        user.setNowMoney(BigDecimal.ZERO);
        user.setBrokeragePrice(BigDecimal.ZERO);
        user.setIntegral(0);
        user.setExperience(0);
        user.setLevel(0);
        user.setSignNum(0);
        user.setPayCount(0);
        user.setSpreadCount(0);
        user.setIsPromoter(false);
        user.setIsWechatPublic(false);
        user.setIsWechatRoutine(false);
        user.setIsWechatIos(false);
        user.setIsWechatAndroid(false);
        user.setIsBindingIos(false);
        user.setIsPaidMember(false);
        user.setIsPermanentPaidMember(false);
        user.setIsLogoff(false);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setOrganizationId(info.getOrganizationId());
        user.setOrganizationName(info.getOrganizationName());
        user.setOrganizationCode(info.getOrganizationCode());
        if (!userService.save(user)) {
            throw new CrmebException("创建管理员用户账号失败");
        }
        
        log.info("养殖场{}管理员用户创建成功，用户ID：{}", farmInstitution.getFarmName(), user.getId());
        return user;
    }

    /**
     * 更新管理员用户账号
     */
    private void updateAdminUserAccount(EmployeeRequest request, Integer userId, FarmInstitution farmInstitution) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new CrmebException("员工关联的用户账号不存在");
        }
        
        // 检查手机号是否已被其他用户使用
        if (!request.getPhone().equals(user.getPhone())) {
            LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(User::getPhone, request.getPhone());
            phoneWrapper.ne(User::getId, userId);
            User existingPhoneUser = userService.getOne(phoneWrapper);
            if (existingPhoneUser != null) {
                throw new CrmebException("手机号" + request.getPhone() + "已被其他用户使用");
            }
        }
        
        user.setRealName(request.getName());
        user.setNickname(request.getName());
        user.setPhone(request.getPhone());
        user.setFarmId(farmInstitution.getId());
        user.setFarmCode(farmInstitution.getFarmCode());
        user.setFarmName(farmInstitution.getFarmName());
        user.setOrganizationId(farmInstitution.getOrganizationId());
        user.setOrganizationName(farmInstitution.getOrganizationName());
        user.setOrganizationCode(farmInstitution.getOrganizationCode());
        user.setUpdateTime(new Date());
        
        // 如果提供了新密码，则更新密码
        if (StrUtil.isNotBlank(request.getPassword())) {
            user.setPwd(CrmebUtil.encryptPassword(request.getPassword(), user.getAccount()));
        }
        
        if (!userService.updateById(user)) {
            throw new CrmebException("更新管理员用户账号失败");
        }
    }

    /**
     * 检查手机号是否存在
     */
    private void checkPhoneExists(String phone, Integer excludeId) {
        LambdaQueryWrapper<Employee> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Employee::getPhone, phone);
        wrapper.eq(Employee::getIsDeleted, false);
        if (excludeId != null) {
            wrapper.ne(Employee::getId, excludeId);
        }
        
        long count = count(wrapper);
        if (count > 0) {
            throw new CrmebException("手机号已存在");
        }
    }

    /**
     * 验证员工请求数据
     */
    private void validateEmployeeRequest(EmployeeRequest request) {
        if (request.getFarmId() == null) {
            throw new CrmebException("养殖场ID不能为空");
        }
        if (StrUtil.isBlank(request.getName())) {
            throw new CrmebException("员工姓名不能为空");
        }
        if (StrUtil.isBlank(request.getPhone())) {
            throw new CrmebException("员工手机号不能为空");
        }
        if (!request.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new CrmebException("手机号格式不正确");
        }
        if (StrUtil.isBlank(request.getPassword())) {
            throw new CrmebException("密码不能为空");
        }
        if (request.getPassword().length() < 6) {
            throw new CrmebException("密码长度不能少于6位");
        }
    }

    /**
     * 获取当前用户ID
     */
    private Integer getCurrentUserId() {
        try {
            return SecurityUtil.getLoginUserVo().getUser().getId();
        } catch (Exception e) {
            return 0; // 系统用户
        }
    }

    // ==================== 员工授信统计相关方法实现 ====================

    @Override
    public com.zbkj.common.response.employee.EmployeeCreditStatisticsResponse getEmployeeCreditStatistics() {
        return employeeDao.getEmployeeCreditStatistics();
    }

    @Override
    public IPage<com.zbkj.common.response.employee.EmployeeCreditDetailResponse> getEmployeeCreditDetailPage(com.zbkj.common.request.employee.EmployeeCreditPageRequest request) {
        Page<com.zbkj.common.response.employee.EmployeeCreditDetailResponse> page = new Page<>(request.getPage(), request.getLimit());
        
        List<com.zbkj.common.response.employee.EmployeeCreditDetailResponse> list = employeeDao.getEmployeeCreditDetailList(
                request.getName(),
                request.getPhone(),
                request.getFarmInstitutionId(),
                request.getCreditStatus()
        );
        
        // 手动分页
        int total = list.size();
        int startIndex = (request.getPage() - 1) * request.getLimit();
        int endIndex = Math.min(startIndex + request.getLimit(), total);
        
        if (startIndex < total) {
            list = list.subList(startIndex, endIndex);
        } else {
            list.clear();
        }
        
        page.setRecords(list);
        page.setTotal(total);
        
        return page;
    }

    @Override
    public com.zbkj.common.response.employee.EmployeeCreditTrendResponse getEmployeeCreditTrend(String startDate, String endDate) {
        return employeeDao.getEmployeeCreditTrend(startDate, endDate);
    }

    @Override
    public void exportEmployeeCreditData(String name, String phone, Integer farmInstitutionId, String creditStatus, javax.servlet.http.HttpServletResponse response) throws Exception {
        // 查询数据
        List<com.zbkj.common.response.employee.EmployeeCreditDetailResponse> dataList = employeeDao.getEmployeeCreditDetailList(
                name, phone, farmInstitutionId, creditStatus
        );
        
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = java.net.URLEncoder.encode("员工授信统计数据_" + new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()), "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        
        // 创建工作簿
        org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("员工授信统计");
        
        // 创建标题行
        org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
        String[] headers = {"员工姓名", "手机号", "养殖机构", "授信额度", "已使用额度", "可用额度", "授信系数", "使用率(%)", "最近授信时间", "状态"};
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        // 填充数据
        for (int i = 0; i < dataList.size(); i++) {
            com.zbkj.common.response.employee.EmployeeCreditDetailResponse data = dataList.get(i);
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);
            
            row.createCell(0).setCellValue(data.getName());
            row.createCell(1).setCellValue(data.getPhone());
            row.createCell(2).setCellValue(data.getFarmInstitutionName());
            row.createCell(3).setCellValue(data.getCreditLimit() != null ? data.getCreditLimit().toString() : "0");
            row.createCell(4).setCellValue(data.getUsedCredit() != null ? data.getUsedCredit().toString() : "0");
            row.createCell(5).setCellValue(data.getAvailableCredit() != null ? data.getAvailableCredit().toString() : "0");
            row.createCell(6).setCellValue(data.getCreditCoefficient() != null ? data.getCreditCoefficient().toString() : "");
            row.createCell(7).setCellValue(data.getCreditUtilizationRate() != null ? data.getCreditUtilizationRate().toString() : "0");
            row.createCell(8).setCellValue(data.getLastCreditTime() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data.getLastCreditTime()) : "");
            row.createCell(9).setCellValue(data.getCreditStatus() != null && data.getCreditStatus() == 1 ? "已授信" : "未授信");
        }
        
        // 输出到响应流
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean allocateEmployeeCredit(EmployeeCreditAllocationRequestVO request) {
        // 获取当前登录的管理员用户
        Integer currentAdminUserId = getCurrentUserId();
        if (currentAdminUserId == null || currentAdminUserId == 0) {
            throw new CrmebException("无法获取当前登录用户信息");
        }
        
        // 查询管理员的授信记录
        LambdaQueryWrapper<CreditApplication> adminCreditWrapper = new LambdaQueryWrapper<>();
        adminCreditWrapper.eq(CreditApplication::getUserId, currentAdminUserId);
        adminCreditWrapper.eq(CreditApplication::getAuditStatus, 2); // 审核通过
        adminCreditWrapper.orderByDesc(CreditApplication::getAuditTime);
        adminCreditWrapper.last("LIMIT 1");
        
        CreditApplication adminCreditApplication = creditApplicationService.getOne(adminCreditWrapper);
        if (ObjectUtil.isNull(adminCreditApplication)) {
            throw new CrmebException("管理员没有可用的授信额度，无法分配给员工");
        }
        
        // 验证管理员是否有足够的可用授信额度
        if (adminCreditApplication.getAvailableCreditAmount().compareTo(request.getCreditAmount()) < 0) {
            throw new CrmebException("管理员可用授信额度不足，可用额度：" + adminCreditApplication.getAvailableCreditAmount() + "，申请分配额度：" + request.getCreditAmount());
        }
        
        // 验证员工是否存在
        Employee employee = getById(request.getEmployeeId());
        if (ObjectUtil.isNull(employee) || employee.getIsDeleted()) {
            throw new CrmebException("员工不存在");
        }
        
        // 获取员工关联的用户信息
        if (ObjectUtil.isNull(employee.getUserId())) {
            throw new CrmebException("员工未关联用户账号，无法分配授信额度");
        }
        
        User user = userService.getById(employee.getUserId());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("员工关联的用户账号不存在");
        }
        
        // 检查员工是否已经有授信记录
        LambdaQueryWrapper<CreditApplication> employeeCreditWrapper = new LambdaQueryWrapper<>();
        employeeCreditWrapper.eq(CreditApplication::getUserId, user.getId());
        employeeCreditWrapper.eq(CreditApplication::getAuditStatus, 2); // 审核通过
        
        CreditApplication existingEmployeeCredit = creditApplicationService.getOne(employeeCreditWrapper);
        if (ObjectUtil.isNotNull(existingEmployeeCredit)) {
            throw new CrmebException("该员工已有授信额度，请先处理现有授信记录");
        }
        
        // 减少管理员的可用授信额度
        adminCreditApplication.setAvailableCreditAmount(
            adminCreditApplication.getAvailableCreditAmount().subtract(request.getCreditAmount())
        );
        boolean adminCreditUpdateResult = creditApplicationService.updateById(adminCreditApplication);
        
        // 创建员工授信申请记录
        CreditApplication employeeCreditApplication = new CreditApplication();
        employeeCreditApplication.setUserId(user.getId());
        employeeCreditApplication.setUserType(user.getUserType());
        employeeCreditApplication.setUserName(user.getRealName() != null ? user.getRealName() : user.getAccount());
        employeeCreditApplication.setOrganizationId(user.getOrganizationId());
        employeeCreditApplication.setFarmName(user.getFarmName());
        employeeCreditApplication.setManagerPhone(user.getPhone());
        employeeCreditApplication.setManagerName(user.getRealName());
        employeeCreditApplication.setApplyType(0); // 社员申请
        employeeCreditApplication.setApplyAmount(request.getCreditAmount());
        employeeCreditApplication.setTotalAmount(request.getCreditAmount());
        employeeCreditApplication.setAvailableCreditAmount(request.getCreditAmount());
        employeeCreditApplication.setTotalRepaymentAmount(BigDecimal.ZERO);
        employeeCreditApplication.setCreditRatio(request.getCreditRatio());
        employeeCreditApplication.setCardNumber(request.getCardNumber());
        employeeCreditApplication.setCreditStartTime(new Date());
        employeeCreditApplication.setCreditPeriod(request.getCreditPeriod());
        employeeCreditApplication.setAuditStatus(2); // 直接设置为通过状态
        employeeCreditApplication.setAuditRemark("管理员直接分配授信额度：" + (StrUtil.isNotBlank(request.getRemark()) ? request.getRemark() : "无备注"));
        employeeCreditApplication.setAuditTime(new Date());
        employeeCreditApplication.setApplyTime(new Date());
        employeeCreditApplication.setApplyStatus(1); // 已提交
        
        // 保存员工授信申请记录
        boolean employeeCreditResult = creditApplicationService.save(employeeCreditApplication);
        
        // 更新员工授信信息
        employee.setCreditLimit(request.getCreditAmount());
        employee.setCreditCoefficient(request.getCreditRatio());
        employee.setUpdateTime(new Date());
        employee.setUpdateBy(currentAdminUserId);
        
        // 同步更新用户表的授信信息
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
        user.setRepaymentDays(request.getCreditPeriod() != null ? request.getCreditPeriod() * 30 : null); // 还款期限（天）
        user.setCardNumber(request.getCardNumber()); // 卡号
        
        user.setLastCreditAuditTime(new Date());
        user.setLastCreditAuditUserId(currentAdminUserId);
        user.setCreditAuditRemark("管理员直接分配授信额度");
        user.setParentCreditUserId(currentAdminUserId);
        user.setCreditLevel(3); // 员工层级
        user.setUpdateTime(new Date());
        
        boolean userResult = userService.updateById(user);
        boolean employeeResult = updateById(employee);
        
        return adminCreditUpdateResult && employeeCreditResult && employeeResult && userResult;
    }

    /**
     * 生成6位有顺序的员工工号
     * 格式：YG + 4位数字（从0001开始递增）
     */
    private String generateEmployeeNo() {
        // 查询当前最大的员工工号
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Employee::getEmployeeNo);
        wrapper.like(Employee::getEmployeeNo, "YG%");
        wrapper.eq(Employee::getIsDeleted, false);
        wrapper.orderByDesc(Employee::getEmployeeNo);
        wrapper.last("LIMIT 1");
        
        Employee lastEmployee = this.getOne(wrapper);
        
        int nextNumber = 1;
        if (lastEmployee != null && StrUtil.isNotBlank(lastEmployee.getEmployeeNo())) {
            String lastEmployeeNo = lastEmployee.getEmployeeNo();
            if (lastEmployeeNo.startsWith("YG") && lastEmployeeNo.length() == 6) {
                try {
                    String numberPart = lastEmployeeNo.substring(2);
                    nextNumber = Integer.parseInt(numberPart) + 1;
                } catch (NumberFormatException e) {
                    log.warn("解析员工工号失败，使用默认编号：{}", lastEmployeeNo);
                    nextNumber = 1;
                }
            }
        }
        
        // 格式化为6位工号：YG + 4位数字
        String employeeNo = String.format("YG%04d", nextNumber);
        
        // 检查是否重复（防止并发情况下重复）
        LambdaQueryWrapper<Employee> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(Employee::getEmployeeNo, employeeNo);
        checkWrapper.eq(Employee::getIsDeleted, false);
        
        Employee existingEmployee = this.getOne(checkWrapper);
        if (existingEmployee != null) {
            // 如果重复，递归生成下一个工号
            nextNumber++;
            employeeNo = String.format("YG%04d", nextNumber);
            log.warn("员工工号重复，生成新工号：{}", employeeNo);
        }
        
        log.info("生成员工工号：{}", employeeNo);
        return employeeNo;
    }

    /**
     * 更新用户表的授信信息（员工创建时）
     */
    private void updateUserCreditInfoForEmployee(User user, CreditInfo creditInfo) {
        if (user == null || creditInfo == null) {
            return;
        }
        
        // 员工创建时只设置评估额度，授信额度为0
        user.setAssessmentAmount(creditInfo.getAssessmentAmount());
        user.setCreditLimit(BigDecimal.ZERO);
        user.setRemainingCredit(BigDecimal.ZERO);
        user.setUsedCredit(BigDecimal.ZERO);
        user.setPendingRepayment(BigDecimal.ZERO);
        user.setCreditStatus(0); // 0-未授信
        user.setCreditCoefficient(creditInfo.getCreditCoefficient());
        user.setTotalStockQuantity(creditInfo.getTotalStockQuantity());
        user.setBreedingVarietyNames(creditInfo.getBreedingVarietyNames());
        user.setBreedingVarietyTypes(creditInfo.getBreedingVarietyTypes());
        user.setCreditLevel(3); // 员工层级为3
        user.setUpdateTime(new Date());
        
        userService.updateById(user);
        
        log.info("更新用户{}授信信息成功，评估金额：{}，授信额度：0", 
                user.getAccount(), creditInfo.getAssessmentAmount());
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
} 