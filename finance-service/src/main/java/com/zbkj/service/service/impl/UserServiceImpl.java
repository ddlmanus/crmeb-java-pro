package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.*;
import com.zbkj.common.dto.IpLocation;
import com.zbkj.common.enums.FarmTypeEnum;
import com.zbkj.common.enums.UserTypeEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.bill.Bill;
import com.zbkj.common.model.bill.UserBill;
import com.zbkj.common.model.employee.Employee;
import com.zbkj.common.model.finance.OrgCategory;
import com.zbkj.common.model.finance.Organization;
import com.zbkj.common.model.member.PaidMemberCard;
import com.zbkj.common.model.member.PaidMemberOrder;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserBalanceRecord;
import com.zbkj.common.model.user.UserIntegralRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.request.merchant.MerchantUserSearchRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.response.employee.FrontMerchantEmployeeResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.LoginResultCode;
import com.zbkj.common.result.MemberResultCode;
import com.zbkj.common.result.UserResultCode;
import com.zbkj.common.token.FrontTokenComponent;
import com.zbkj.common.utils.*;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.common.vo.LoginFrontUserVo;
import com.zbkj.service.dao.EmployeeDao;
import com.zbkj.service.dao.UserDao;
import com.zbkj.service.service.*;
import com.zbkj.service.service.finance.OrgCategoryService;
import com.zbkj.service.service.finance.OrganizationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户表 服务实现类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserDao dao;

    @Autowired
    private FrontTokenComponent tokenComponent;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;
    @Autowired
    private UserBillService userBillService;
    @Autowired
    private BillService billService;
    @Autowired
    private UserBalanceRecordService userBalanceRecordService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RefundOrderService refundOrderService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductRelationService productRelationService;
    @Autowired
    private UserMerchantCollectService userMerchantCollectService;
    @Autowired
    private CommunityNotesService communityNotesService;
    @Autowired
    private CommunityNotesRelationService communityNotesRelationService;
    @Autowired
    private SystemUserLevelService systemUserLevelService;
    @Autowired
    private PaidMemberCardService paidMemberCardService;
    @Autowired
    private PaidMemberOrderService paidMemberOrderService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MerchantEmployeeService merchantEmployeeService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private OrgCategoryService orgCategoryService;
    @Autowired
    private EmployeeDao employeeDao;


    /**
     * 手机号注册用户
     *
     * @param phone     手机号
     * @param spreadUid 推广人编号
     * @return User
     */
    @Override
    public User registerPhone(String phone, Integer spreadUid) {
        User user = new User();
        user.setAccount(phone);
        user.setPwd(CommonUtil.createPwd(phone));
        user.setPhone(phone);
        user.setRegisterType(UserConstants.REGISTER_TYPE_H5);
        user.setNickname(CommonUtil.createNickName(phone));
        user.setAvatar(systemConfigService.getValueByKey(SysConfigConstants.USER_DEFAULT_AVATAR_CONFIG_KEY));
        Date nowDate = CrmebDateUtil.nowDateTime();
        user.setCreateTime(nowDate);
        user.setLastLoginTime(nowDate);
        user.setLevel(1);

        // 推广人
        user.setSpreadUid(0);
        if (spreadUid > 0) {
            Boolean check = checkBingSpread(user, spreadUid, "new");
            if (check) {
                user.setSpreadUid(spreadUid);
                user.setSpreadTime(nowDate);
            }
        }

        try {
            HttpServletRequest request = RequestUtil.getRequest();
            String clientIP = ServletUtil.getClientIP(request, null);
            if (StrUtil.isBlank(clientIP)) {
                logger.error("获取用户ip失败，用户phone = " + user.getPhone());
            } else {
                IpLocation ipLocation = IPUtil.getLocation(clientIP);
                if (ObjectUtil.isNotNull(ipLocation.getCountry())) {
                    user.setCountry(ipLocation.getCountry().equals("中国") ? "CN" : "OTHER");
                    user.setProvince(ipLocation.getProvince());
                    user.setCity(ipLocation.getCity());
                }
            }
        } catch (Exception e) {
            logger.error("通过ip获取用户位置失败，用户phone = {}", user.getPhone());
            logger.error("通过ip获取用户位置失败", e);
        }

        Boolean execute = transactionTemplate.execute(e -> {
            save(user);
            // 推广人处理
            if (user.getSpreadUid() > 0) {
                updateSpreadCountByUid(spreadUid, Constants.OPERATION_TYPE_ADD);
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException(UserResultCode.USER_REGISTER_FAILED);
        }
        return user;
    }

    /**
     * 根据手机号查询用户
     *
     * @param phone 用户手机号
     * @return 用户信息
     */
    @Override
    public User getByPhone(String phone) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getPhone, phone);
        lqw.eq(User::getIsLogoff, 0);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 检测能否绑定关系
     *
     * @param user      当前用户
     * @param spreadUid 推广员Uid
     * @param type      用户类型:new-新用户，old—老用户
     * @return Boolean
     * 1.判断分销功能是否启用
     * 2.判断分销模式
     * 3.根据不同的分销模式校验
     * 4.指定分销，只有分销员才可以分销，需要spreadUid是推广员才可以绑定
     * 5.人人分销，可以直接绑定
     * *推广关系绑定，下级不能绑定自己的上级为下级，A->B->A(❌)
     */
    public Boolean checkBingSpread(User user, Integer spreadUid, String type) {
        if (ObjectUtil.isNull(spreadUid) || spreadUid <= 0) {
            return false;
        }
        if (ObjectUtil.isNull(user)) {
            return false;
        }
        if (user.getSpreadUid() > 0) {
            return false;
        }
        if (ObjectUtil.isNotNull(user.getId()) && user.getId().equals(spreadUid)) {
            return false;
        }
        // 判断分销功能是否启用
        String isOpen = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_SWITCH);
        if (StrUtil.isBlank(isOpen) || isOpen.equals("0")) {
            return false;
        }
        if (type.equals("old")) {
            // 判断分销关系绑定类型（所有、新用户）
            String bindType = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_BINDING_TYPE);
            if (StrUtil.isBlank(bindType) || bindType.equals("1")) {
                return false;
            }
        }
        // 查询推广员
        User spreadUser = getById(spreadUid);
        if (ObjectUtil.isNull(spreadUser) || !spreadUser.getStatus()) {
            return false;
        }
        // 指定分销不是推广员不绑定
        if (!spreadUser.getIsPromoter()) {
            return false;
        }
        // 下级不能绑定自己的上级为自己的下级
        return !ObjectUtil.isNotNull(user.getId()) || !user.getId().equals(spreadUser.getSpreadUid());
    }

    /**
     * 更新推广员推广数
     *
     * @param uid  uid
     * @param type add or sub
     */
    public Boolean updateSpreadCountByUid(Integer uid, String type) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            updateWrapper.setSql("spread_count = spread_count + 1");
        } else {
            updateWrapper.setSql("spread_count = spread_count - 1");
        }
        updateWrapper.eq("id", uid);
        return update(updateWrapper);
    }

    /**
     * 修改密码
     *
     * @param request PasswordRequest 密码
     * @return boolean
     */
    @Override
    public Boolean password(PasswordRequest request) {
        User user = getInfo();
        //检测验证码
        smsService.checkValidateCode(SmsConstants.VERIFICATION_CODE_SCENARIO_USER_UPDATE_PASSWORD, user.getPhone(), request.getValidateCode());
        //密码
        user.setPwd(CrmebUtil.encryptPassword(request.getPassword(), user.getPhone()));
        return updateById(user);
    }

    /**
     * 获取当前用户ID，带异常
     */
    @Override
    public Integer getUserIdException() {
        Integer id = tokenComponent.getUserId();
        if (null == id) {
            throw new CrmebException(LoginResultCode.LOGIN_EXPIRE);
        }
        return id;
    }

    /**
     * 获取当前用户id
     */
    @Override
    public Integer getUserId() {
        Integer id = tokenComponent.getUserId();
        if (null == id) {
            return 0;
        }
        return id;
    }

    /**
     * 获取个人资料
     */
    @Override
    public User getInfo() {
        Integer userId = getUserIdException();
        User user = getById(userId);
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException(LoginResultCode.LOGIN_EXPIRE);
        }
        if (user.getIsLogoff()) {
            throw new CrmebException(UserResultCode.USER_LOGOFF);
        }
        return user;
    }

    /**
     * 移动端当前用户信息
     *
     * @return UserInfoResponse
     */
    @Override
    public UserInfoResponse getUserInfo() {
        User currentUser = getInfo();
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        BeanUtils.copyProperties(currentUser, userInfoResponse);
        userInfoResponse.setPhone(CrmebUtil.maskMobile(userInfoResponse.getPhone()));
        return userInfoResponse;
    }

    @Override
    public Merchant getEmployeeInfo() {
        LoginFrontUserVo userForMerchantEmployee = tokenComponent.getUserForMerchantEmployee();
        // 仅仅查询出当前员工对应的商户信息
        Integer activeMerchantId = userForMerchantEmployee.getActiveMerchant();
        List<FrontMerchantEmployeeResponse> currentMerchantConfig = userForMerchantEmployee.getMerchantEmployeeList().stream()
                .filter(p -> Objects.equals(p.getMerId(), activeMerchantId)).collect(Collectors.toList());
        if(currentMerchantConfig.size() > 1) throw new CrmebException("移动端管理员商户配置数据不正确");
        return merchantService.getByIdException(activeMerchantId);
    }

    /**
     * 修改个人信息
     *
     * @param request 修改信息
     */
    @Override
    public Boolean editUser(UserEditInfoRequest request) {
        User user = getInfo();
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(User::getNickname, request.getNickname());
        wrapper.set(User::getAvatar, systemAttachmentService.clearPrefix(request.getAvatar()));
        wrapper.set(User::getProvince, request.getProvince());
        wrapper.set(User::getCity, request.getCity());
        wrapper.set(User::getSex, request.getSex());
        wrapper.set(User::getBirthday, request.getBirthday());
        wrapper.eq(User::getId, user.getId());
        return update(wrapper);
    }

    /**
     * 获取当前用户手机号验证码
     */
    @Override
    public Boolean getCurrentPhoneCode() {
        User user = getInfo();
        return smsService.sendCommonCode(user.getPhone(), SmsConstants.VERIFICATION_CODE_SCENARIO_USER_UPDATE_BINDING_CURRENT);
    }

    /**
     * 换绑手机号获取验证码
     *
     * @param request 请求参数
     *                captcha 为用户原手机号验证码
     */
    @Override
    public Boolean updatePhoneCode(UserBindingPhoneUpdateRequest request) {
        User user = getInfo();
        smsService.checkValidateCode(SmsConstants.VERIFICATION_CODE_SCENARIO_USER_UPDATE_BINDING_CURRENT, user.getPhone(), request.getCaptcha());
        User tempUser = getByPhone(request.getPhone());
        if (ObjectUtil.isNotNull(tempUser)) {
            throw new CrmebException(UserResultCode.USER_PHONE_EXIST);
        }
        return smsService.sendCommonCode(request.getPhone(), SmsConstants.VERIFICATION_CODE_SCENARIO_USER_UPDATE_BINDING_NEW);
    }

    /**
     * 换绑手机号
     *
     * @param request 请求参数
     */
    @Override
    public Boolean updatePhone(UserBindingPhoneUpdateRequest request) {
        smsService.checkValidateCode(SmsConstants.VERIFICATION_CODE_SCENARIO_USER_UPDATE_BINDING_NEW, request.getPhone(), request.getCaptcha());
        User tempUser = getByPhone(request.getPhone());
        if (ObjectUtil.isNotNull(tempUser)) {
            throw new CrmebException(UserResultCode.USER_PHONE_EXIST);
        }
        User user = getInfo();
        String password = "";
        try {
            password = CrmebUtil.decryptPassowrd(user.getPwd(), user.getPhone());
        } catch (Exception e) {
            e.printStackTrace();
        }

        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(User::getPhone, request.getPhone());
        wrapper.set(User::getAccount, request.getPhone());
        wrapper.set(User::getPwd, CrmebUtil.encryptPassword(password, request.getPhone()));
        wrapper.eq(User::getId, user.getId());
        return update(wrapper);
    }

    /**
     * 分页显示用户表
     *
     * @param request          搜索条件
     */
    @Override
    public PageInfo<UserResponse> getPlatformPage(UserSearchRequest request) {
        Page<User> pageUser = PageHelper.startPage(request.getPage(), request.getLimit());
        Map<String, Object> map = CollUtil.newHashMap();
        if (StrUtil.isNotBlank(request.getTagIds())) {
            String tagIdSql = CrmebUtil.getFindInSetSql("u.tag_id", request.getTagIds());
            map.put("tagIdSql", tagIdSql);
        }
        if (ObjectUtil.isNotNull(request.getSex())) {
            map.put("sex", request.getSex());
        }
        if (StrUtil.isNotBlank(request.getRegisterType())) {
            map.put("registerType", request.getRegisterType());
        }
        if (StrUtil.isNotBlank(request.getPayCount())) {
            map.put("payCount", Integer.valueOf(request.getPayCount()));
        }
        if (ObjectUtil.isNotNull(request.getIdentity())) {
            if (request.getIdentity().equals(1)) {
                map.put("isPromoter", 1);
            }
            if (request.getIdentity().equals(3)) {
                map.put("isPaidMember", 1);
            }
        }
        if (ObjectUtil.isNotNull(request.getIsLogoff())) {
            map.put("isLogoff", request.getIsLogoff());
        }
        // 处理用户类型字段
        if (ObjectUtil.isNotNull(request.getUserType())) {
            map.put("userType", request.getUserType());
        }
        // 处理养殖场机构id字段
        if (ObjectUtil.isNotNull(request.getFarmId())) {
            map.put("farmId", request.getFarmId());
        }
        // 处理养殖场名称字段
        if (StrUtil.isNotBlank(request.getFarmName())) {
            map.put("farmName", request.getFarmName());
        }
        // 处理养殖场代码字段
        if (StrUtil.isNotBlank(request.getFarmCode())) {
            map.put("farmCode", request.getFarmCode());
        }
        // 处理所属机构字段
        if (StrUtil.isNotBlank(request.getOrganizationId())) {
            map.put("organizationId", request.getOrganizationId());
        }
        if (StrUtil.isNotBlank(request.getOrganizationName())) {
            map.put("organizationName", request.getOrganizationName());
        }
        if (StrUtil.isNotBlank(request.getOrganizationCode())) {
            map.put("organizationCode", request.getOrganizationCode());
        }
        DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
        if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
            map.put("startTime", dateLimit.getStartTime());
            map.put("endTime", dateLimit.getEndTime());
            map.put("accessType", request.getAccessType());
        }
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        List<User> userList = dao.findAdminList(map);
        List<UserResponse> userResponses = new ArrayList<>();
        if (CollUtil.isEmpty(userList)) {
            return CommonPage.copyPageInfo(pageUser, userResponses);
        }
        List<Integer> spreadUidList = userList.stream().filter(e -> e.getSpreadUid() > 0).map(User::getSpreadUid).distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = CollUtil.newHashMap();
        if (CollUtil.isNotEmpty(spreadUidList)) {
            userMap = getUidMapList(spreadUidList);
        }
        for (User user : userList) {
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(user, userResponse);
            if (StrUtil.isNotEmpty(userResponse.getPhone())) {
                userResponse.setPhone(CrmebUtil.maskMobile(userResponse.getPhone()));
            }
            if (user.getSpreadUid() > 0) {
                userResponse.setSpreadName(userMap.get(user.getSpreadUid()).getNickname());
            }
            userResponses.add(userResponse);
        }
        return CommonPage.copyPageInfo(pageUser, userResponses);
    }

    /**
     * 新增用户
     *
     * @param userRequest 用户信息
     * @return Boolean
     */
    @Override
    public Boolean addUser(UserAddRequest userRequest) {
        // 检查账号是否已存在
        LambdaQueryWrapper<User> accountWrapper = new LambdaQueryWrapper<>();
        accountWrapper.eq(User::getAccount, userRequest.getAccount());
        accountWrapper.eq(User::getIsLogoff, false);
        User existAccount = dao.selectOne(accountWrapper);
        if (ObjectUtil.isNotNull(existAccount)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "用户账号已存在");
        }

        // 检查手机号是否已存在
        LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
        phoneWrapper.eq(User::getPhone, userRequest.getPhone());
        phoneWrapper.eq(User::getIsLogoff, false);
        User existPhone = dao.selectOne(phoneWrapper);
        if (ObjectUtil.isNotNull(existPhone)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "手机号已存在");
        }
        // 创建用户对象
        User user = new User();
        BeanUtils.copyProperties(userRequest, user);
        Organization organization = organizationService.getById(userRequest.getOrganizationId());
        if(organization!=null){
            OrgCategory orgCategory = orgCategoryService.getById(organization.getCategoryId());
            if(StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().equals("合作社")){
                //判断合作社下是否存在管理员
                User one = this.getOne(new LambdaQueryWrapper<User>().eq(User::getOrganizationId, organization.getId()).
                        eq(User::getFarmType, FarmTypeEnum.TENANT_TYPE_COOPERATIVE.getCode()).eq(User::getUserType, 1));
                //判断前端传入的用户类型
                if(userRequest.getUserType()== UserTypeEnum.USER_TYPE_ADMIN.getCode() &&one!=null){
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "合作社下已存在管理员,请勿重复添加");
                }
                user.setFarmType(FarmTypeEnum.TENANT_TYPE_COOPERATIVE.getCode());// 2表示合作社
            }else if (StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().contains("养殖")) {
                user.setFarmType(FarmTypeEnum.TENANT_TYPE_PLANTING_HOUSE.getCode());// 1表示养殖户
            } else if (StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().contains("供应商")) {
                user.setFarmType(FarmTypeEnum.TENANT_TYPE_SUPPLIER.getCode());// 4表示供应商
            }else if (StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().contains("平台")) {
                user.setFarmType(FarmTypeEnum.TENANT_TYPE_PLATFORM.getCode());//  5表示平台
            }else if (StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().contains("经纪人")) {
                user.setFarmType(FarmTypeEnum.TENANT_TYPE_BROKER.getCode());//  3表示经纪人
            }else {
                user.setFarmType(FarmTypeEnum.TENANT_TYPE_GUEST.getCode());// 0表示游客
            }
        }else{
            user.setFarmType(FarmTypeEnum.TENANT_TYPE_PLANTING_HOUSE.getCode()); // 如果没有选择养殖场，默认为游客
        }
        // 设置默认值
        user.setPwd(CrmebUtil.encryptPassword(userRequest.getPwd(), user.getPhone())); // 默认密码
        user.setRegisterType(UserConstants.REGISTER_TYPE_ADMIN);
        user.setStatus(true);
        user.setIsLogoff(false);
        user.setIsPromoter(false);
        user.setIsWechatPublic(false);
        user.setIsWechatRoutine(false);
        user.setIsWechatIos(false);
        user.setIsWechatAndroid(false);
        user.setIsBindingIos(false);
        user.setIsPaidMember(false);
        user.setIsPermanentPaidMember(false);
        user.setLevel(1);
        user.setSignNum(0);
        user.setPayCount(0);
        user.setSpreadCount(0);
        user.setSpreadUid(0);
        user.setIntegral(0);
        user.setExperience(0);
        user.setNowMoney(BigDecimal.ZERO);
        user.setBrokeragePrice(BigDecimal.ZERO);
        
        // 设置默认头像
        if (StrUtil.isBlank(user.getAvatar())) {
            user.setAvatar(systemConfigService.getValueByKey(SysConfigConstants.USER_DEFAULT_AVATAR_CONFIG_KEY));
        }
        
        // 生成养殖场代码（如果需要）
        // farmCode现在从养殖场选择中获取

        Date nowDate = CrmebDateUtil.nowDateTime();
        user.setCreateTime(nowDate);
        user.setUpdateTime(nowDate);
        user.setLastLoginTime(nowDate);

        // 保存用户
        boolean save = save(user);
        if(user.getUserType().equals(UserTypeEnum.USER_TYPE_EMPLOYEE.getCode())){
            //保存员工表
            Employee employee = new Employee();
            employee.setUserId(user.getId());
            employee.setEmployeeNo(user.getPhone());
            employee.setName(user.getNickname());
            employee.setPhone(user.getPhone());
            employee.setIdCard(user.getIdentityCardNo());
            employee.setFarmId(user.getFarmId());
            employee.setFarmCode(user.getFarmCode());
            employee.setFarmName(user.getFarmName());
            employee.setStatus(1);
            employee.setCreateTime(nowDate);
            employee.setUpdateTime(nowDate);
            employee.setCreateBy(user.getId());
            employee.setUpdateBy(user.getId());
            employee.setIsDeleted(false);
            employee.setConsumedAmount(BigDecimal.ZERO);
            employee.setCreditLimit(BigDecimal.ZERO);
            employee.setCreditCoefficient(BigDecimal.ZERO);
            employee.setFarmType(user.getFarmType());
            employee.setAvatar(user.getAvatar());
            employee.setGender(user.getSex());
            employee.setOrganizationId(user.getOrganizationId());
            employeeDao.insert(employee);
        }
       return save;
    }



    /**
     * 商户端用户分页列表
     *
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserResponse> getMerchantPage(MerchantUserSearchRequest request, PageParamRequest pageParamRequest) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Page<User> pageUser = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("merId", systemAdmin.getMerId());
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        if (StrUtil.isNotBlank(request.getRegisterType())) {
            map.put("registerType", request.getRegisterType());
        }
        if (ObjectUtil.isNotNull(request.getSex())) {
            map.put("sex", request.getSex());
        }
        DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
        if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
            map.put("startTime", dateLimit.getStartTime());
            map.put("endTime", dateLimit.getEndTime());
        }
        List<User> userList = dao.findMerchantList(map);
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : userList) {
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(user, userResponse);
            userResponse.setPhone(CrmebUtil.maskMobile(userResponse.getPhone()));
            userResponses.add(userResponse);
        }
        return CommonPage.copyPageInfo(pageUser, userResponses);
    }

    /**
     * 更新用户
     *
     * @param userRequest 用户参数
     * @return Boolean
     */
    @Override
    public Boolean updateUser(UserUpdateRequest userRequest) {
        User tempUser = getById(userRequest.getId());
        if (ObjectUtil.isNull(tempUser)) {
            throw new CrmebException(UserResultCode.USER_NOT_EXIST);
        }
        if (tempUser.getIsLogoff()) {
            throw new CrmebException(UserResultCode.USER_LOGOFF);
        }
        
        // 检查手机号是否已存在（如果修改了手机号）
        if (StrUtil.isNotBlank(userRequest.getPhone()) && !userRequest.getPhone().equals(tempUser.getPhone())) {
            LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(User::getPhone, userRequest.getPhone());
            phoneWrapper.eq(User::getIsLogoff, false);
            phoneWrapper.ne(User::getId, userRequest.getId());
            User existPhone = dao.selectOne(phoneWrapper);
            if (ObjectUtil.isNotNull(existPhone)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "手机号已存在");
            }
        }
        
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        
        // 基本信息字段
        if (StrUtil.isNotBlank(userRequest.getRealName())) {
            wrapper.set(User::getRealName, userRequest.getRealName());
        }
        if (StrUtil.isNotBlank(userRequest.getNickname())) {
            wrapper.set(User::getNickname, userRequest.getNickname());
        }
        if (StrUtil.isNotBlank(userRequest.getPhone())) {
            wrapper.set(User::getPhone, userRequest.getPhone());
            wrapper.set(User::getAccount, userRequest.getPhone()); // 同时更新账号
        }
        if (StrUtil.isNotBlank(userRequest.getIdentityCardNo())) {
            wrapper.set(User::getIdentityCardNo, userRequest.getIdentityCardNo());
        }
        if (ObjectUtil.isNotNull(userRequest.getSex())) {
            wrapper.set(User::getSex, userRequest.getSex());
        }
        if (StrUtil.isNotBlank(userRequest.getAvatar())) {
            wrapper.set(User::getAvatar, userRequest.getAvatar());
        }
        
        // 机构相关字段
        if (StrUtil.isNotBlank(userRequest.getFarmName())) {
            wrapper.set(User::getFarmName, userRequest.getFarmName());
        }
        if (ObjectUtil.isNotNull(userRequest.getFarmId())) {
            wrapper.set(User::getFarmId, userRequest.getFarmId());
        }
        if (StrUtil.isNotBlank(userRequest.getFarmCode())) {
            wrapper.set(User::getFarmCode, userRequest.getFarmCode());
        }
        if (StrUtil.isNotBlank(userRequest.getOrganizationId())) {
            wrapper.set(User::getOrganizationId, userRequest.getOrganizationId());
            Organization organization = organizationService.getById(userRequest.getOrganizationId());
            if(organization!=null){
                OrgCategory orgCategory = orgCategoryService.getById(organization.getCategoryId());
                if(StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().equals("合作社")){
                    wrapper.set(User::getFarmType, 2);
                }else if (StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().contains("养殖")) {
                    wrapper.set(User::getFarmType, 1);
                } else if (StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().contains("供应商")) {
                    wrapper.set(User::getFarmType, 4);
                }else if (StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().contains("平台")) {
                    wrapper.set(User::getFarmType, 5);
                }else if (StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().contains("经纪人")) {
                    wrapper.set(User::getFarmType, 3);
                }else {
                    wrapper.set(User::getFarmType, 0);
                }
            }else{
                wrapper.set(User::getFarmType, 1); // 如果没有选择养殖场，默认为游客
            }

        }
        if (StrUtil.isNotBlank(userRequest.getOrganizationName())) {
            wrapper.set(User::getOrganizationName, userRequest.getOrganizationName());
        }
        if (StrUtil.isNotBlank(userRequest.getOrganizationCode())) {
            wrapper.set(User::getOrganizationCode, userRequest.getOrganizationCode());
        }
        
        // 地址信息字段
        if (StrUtil.isNotBlank(userRequest.getProvince())) {
            wrapper.set(User::getProvince, userRequest.getProvince());
        }
        if (StrUtil.isNotBlank(userRequest.getCity())) {
            wrapper.set(User::getCity, userRequest.getCity());
        }
        if (StrUtil.isNotBlank(userRequest.getDistrict())) {
            wrapper.set(User::getDistrict, userRequest.getDistrict());
        }
        if (StrUtil.isNotBlank(userRequest.getAddress())) {
            wrapper.set(User::getAddress, userRequest.getAddress());
        }
        
        // 原有字段
        if (StrUtil.isNotBlank(userRequest.getTagId())) {
            wrapper.set(User::getTagId, userRequest.getTagId());
        } else {
            wrapper.set(User::getTagId, "");
        }
        if (StrUtil.isNotBlank(userRequest.getBirthday())) {
            wrapper.set(User::getBirthday, userRequest.getBirthday());
        }
        if (StrUtil.isNotBlank(userRequest.getMark())) {
            wrapper.set(User::getMark, userRequest.getMark());
        }
        if (ObjectUtil.isNotNull(userRequest.getStatus())) {
            wrapper.set(User::getStatus, userRequest.getStatus());
        }
        if (ObjectUtil.isNotNull(userRequest.getUserType())) {
            wrapper.set(User::getUserType, userRequest.getUserType());
        }
        if (ObjectUtil.isNotNull(userRequest.getIsPromoter())) {
            wrapper.set(User::getIsPromoter, userRequest.getIsPromoter());
            if (userRequest.getIsPromoter() && !tempUser.getIsPromoter()) {
                wrapper.set(User::getPromoterTime, DateUtil.date());
            }
        }
        
        // 新增的地区ID字段
        if (ObjectUtil.isNotNull(userRequest.getProvinceId())) {
            wrapper.set(User::getProvinceId, userRequest.getProvinceId());
        }
        if (ObjectUtil.isNotNull(userRequest.getCityId())) {
            wrapper.set(User::getCityId, userRequest.getCityId());
        }
        if (ObjectUtil.isNotNull(userRequest.getDistrictId())) {
            wrapper.set(User::getDistrictId, userRequest.getDistrictId());
        }
        
        wrapper.eq(User::getId, tempUser.getId());
        return update(wrapper);
    }

    /**
     * 是否用户使用标签
     *
     * @param tagId 标签id
     * @return Boolean
     */
    @Override
    public Boolean isUsedTag(Integer tagId) {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.select(User::getId);
        lqw.apply("find_in_set({0}, tag_id)", tagId);
        lqw.last(" limit 1");
        User user = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(user);
    }

    /**
     * 用户分配标签
     *
     * @param request 标签参数
     */
    @Override
    public Boolean tag(UserAssignTagRequest request) {
        //循环id处理
        List<Integer> idList = CrmebUtil.stringToArray(request.getIds());
        idList = idList.stream().distinct().collect(Collectors.toList());
//        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
//        wrapper.set(User::getTagId, request.getTagIds());
//        wrapper.in(User::getId, idList);
//        wrapper.eq(User::getIsLogoff, 0);
//        return update(wrapper);
        List<User> userList = findByIdList(idList);
        List<Integer> tagIdList = CrmebUtil.stringToArray(request.getTagIds());
        return transactionTemplate.execute(e -> {
            for (User user : userList) {
                if (StrUtil.isBlank(user.getTagId())) {
                    user.setTagId(request.getTagIds());
                } else {
                    List<Integer> userTagList = CrmebUtil.stringToArray(user.getTagId());
                    userTagList.addAll(tagIdList);
                    String tagStr = userTagList.stream().distinct().map(String::valueOf).collect(Collectors.joining(","));
                    user.setTagId(tagStr);
                }
                LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
                wrapper.set(User::getTagId, user.getTagId());
                wrapper.eq(User::getId, user.getId());
                update(wrapper);
            }
            return Boolean.TRUE;
        });
    }

    private List<User> findByIdList(List<Integer> idList) {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.in(User::getId, idList);
        return dao.selectList(lqw);
    }

    /**
     * 清除对应的用户等级
     *
     * @param levelId 等级id
     */
    @Override
    public Boolean removeLevelByLevelId(Integer levelId) {
        LambdaUpdateWrapper<User> luw = Wrappers.lambdaUpdate();
        luw.set(User::getLevel, 1);
        luw.eq(User::getLevel, levelId);
        return update(luw);
    }

    /**
     * 获取uidMap
     *
     * @param uidList uid列表
     * @return Map
     */
    @Override
    public Map<Integer, User> getUidMapList(List<Integer> uidList) {
        Map<Integer, User> userMap = new HashMap<>();
        if (CollUtil.isEmpty(uidList)) {
            return userMap;
        }
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.select(User::getId, User::getNickname, User::getPhone, User::getAvatar, User::getIsLogoff, User::getLevel);
        lqw.in(User::getId, uidList);
        List<User> userList = dao.selectList(lqw);
        userList.forEach(user -> {
            userMap.put(user.getId(), user);
        });
        return userMap;
    }

    /**
     * 更新余额
     *
     * @param uid   用户ID
     * @param price 金额
     * @param type  增加add、扣减sub
     * @return Boolean
     */
    @Override
    public Boolean updateNowMoney(Integer uid, BigDecimal price, String type) {
        UpdateWrapper<User> wrapper = Wrappers.update();
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            wrapper.setSql(StrUtil.format("now_money = now_money + {}", price));
        } else {
            wrapper.setSql(StrUtil.format("now_money = now_money - {}", price));
        }
        wrapper.eq("id", uid);
        if (type.equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            wrapper.apply(StrUtil.format(" now_money - {} >= 0", price));
        }
        return update(wrapper);
    }

    /**
     * 更新用户积分
     *
     * @param uid      用户ID
     * @param integral 积分
     * @param type     增加add、扣减sub
     * @return Boolean
     */
    @Override
    public Boolean updateIntegral(Integer uid, Integer integral, String type) {
        UpdateWrapper<User> wrapper = Wrappers.update();
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            wrapper.setSql(StrUtil.format("integral = integral + {}", integral));
        } else {
            wrapper.setSql(StrUtil.format("integral = integral - {}", integral));
        }
        wrapper.eq("id", uid);
        if (type.equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            wrapper.apply(StrUtil.format(" integral - {} >= 0", integral));
        }
        return update(wrapper);
    }

    /**
     * 更新用户佣金
     *
     * @param uid   用户ID
     * @param price 金额
     * @param type  增加add、扣减sub
     * @return Boolean
     */
    @Override
    public Boolean updateBrokerage(Integer uid, BigDecimal price, String type) {
        UpdateWrapper<User> wrapper = Wrappers.update();
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            wrapper.setSql(StrUtil.format("brokerage_price = brokerage_price + {}", price));
        } else {
            wrapper.setSql(StrUtil.format("brokerage_price = brokerage_price - {}", price));
        }
        wrapper.eq("id", uid);
        if (type.equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            wrapper.apply(StrUtil.format(" brokerage_price - {} >= 0", price));
        }
        return update(wrapper);
    }

    /**
     * 更新用户经验
     *
     * @param uid        用户ID
     * @param experience 经验
     * @param type       增加add、扣减sub
     * @return Boolean
     */
    @Override
    public Boolean updateExperience(Integer uid, Integer experience, String type) {
        UpdateWrapper<User> wrapper = Wrappers.update();
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            wrapper.setSql(StrUtil.format("experience = experience + {}", experience));
        } else {
            wrapper.setSql(StrUtil.format("experience = experience - {}", experience));
        }
        wrapper.eq("id", uid);
        if (type.equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            wrapper.apply(StrUtil.format(" experience - {} >= 0", experience));
        }
        return update(wrapper);
    }

    /**
     * 佣金转余额
     *
     * @param uid   用户ID
     * @param price 转入金额
     * @return Boolean
     */
    @Override
    public Boolean brokerageToYue(Integer uid, BigDecimal price) {
        UpdateWrapper<User> wrapper = Wrappers.update();
        wrapper.setSql(StrUtil.format("now_money = now_money + {}", price));
        wrapper.setSql(StrUtil.format("brokerage_price = brokerage_price - {}", price));
        wrapper.eq("id", uid);
        wrapper.apply(StrUtil.format(" brokerage_price - {} >= 0", price));
        return update(wrapper);
    }

    /**
     * 操作用户积分
     *
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean operateUserInteger(UserOperateIntegralRequest request) {
        User user = getById(request.getUid());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException(UserResultCode.USER_NOT_EXIST);
        }
        if (user.getIsLogoff()) {
            throw new CrmebException(UserResultCode.USER_LOGOFF);
        }
        if (request.getOperateType().equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            if (user.getIntegral() - request.getIntegral() < 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "积分扣减后不能小于0");
            }
        }
        if (request.getOperateType().equals(Constants.OPERATION_TYPE_ADD)) {
            if ((user.getIntegral() + request.getIntegral()) > 99999999) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "积分添加后不能大于99999999");
            }
        }
        return transactionTemplate.execute(e -> {
            // 生成记录
            UserIntegralRecord integralRecord = new UserIntegralRecord();
            integralRecord.setUid(user.getId());
            integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_SYSTEM);
            integralRecord.setTitle(IntegralRecordConstants.INTEGRAL_RECORD_TITLE_SYSTEM);
            integralRecord.setIntegral(request.getIntegral());
            integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
            if (request.getOperateType().equals(Constants.OPERATION_TYPE_ADD)) {// 增加
                integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
                integralRecord.setBalance(user.getIntegral() + request.getIntegral());
                integralRecord.setMark(StrUtil.format("后台操作增加了{}积分", request.getIntegral()));
                updateIntegral(user.getId(), request.getIntegral(), Constants.OPERATION_TYPE_ADD);
            } else {
                integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB);
                integralRecord.setBalance(user.getIntegral() - request.getIntegral());
                integralRecord.setMark(StrUtil.format("后台操作减少了{}积分", request.getIntegral()));
                updateIntegral(user.getId(), request.getIntegral(), Constants.OPERATION_TYPE_SUBTRACT);
            }
            userIntegralRecordService.save(integralRecord);
            return Boolean.TRUE;
        });
    }

    /**
     * 操作用户余额
     *
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean operateUserBalance(UserOperateBalanceRequest request) {
        User user = getById(request.getUid());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException(UserResultCode.USER_NOT_EXIST);
        }
        if (user.getIsLogoff()) {
            throw new CrmebException(UserResultCode.USER_LOGOFF);
        }
        // 减少时要判断小于0的情况,添加时判断是否超过数据限制
        if (request.getOperateType().equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            if (user.getNowMoney().subtract(request.getMoney()).compareTo(BigDecimal.ZERO) < 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "余额扣减后不能小于0");
            }
        }
        if (request.getOperateType().equals(Constants.OPERATION_TYPE_ADD)) {
            if (user.getNowMoney().add(request.getMoney()).compareTo(new BigDecimal("99999999.99")) > 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "余额添加后后不能大于99999999.99");
            }
        }
        // 生成UserBill
        UserBill userBill = new UserBill();
        userBill.setUid(user.getId());
        userBill.setLinkId("0");
        userBill.setTitle("后台操作");
        userBill.setCategory(BillConstants.BILL_TYPE_SYSTEM);
        userBill.setNumber(request.getMoney());
        userBill.setStatus(1);
        userBill.setCreateTime(CrmebDateUtil.nowDateTime());

        Bill bill = new Bill();
        bill.setUid(user.getId());
        bill.setAmount(request.getMoney());
        bill.setType(BillConstants.BILL_TYPE_SYSTEM);

        UserBalanceRecord balanceRecord = new UserBalanceRecord();
        balanceRecord.setUid(user.getId());
        balanceRecord.setAmount(request.getMoney());
        balanceRecord.setLinkId("0");
        balanceRecord.setLinkType(BalanceRecordConstants.BALANCE_RECORD_LINK_TYPE_SYSTEM);
        return transactionTemplate.execute(e -> {
            if (request.getOperateType().equals(Constants.OPERATION_TYPE_ADD)) {// 增加
                userBill.setPm(1);
                userBill.setType(BillConstants.USER_BILL_TYPE_SYSTEM_ADD);
                userBill.setBalance(user.getNowMoney().add(request.getMoney()));
                userBill.setMark(StrUtil.format("后台操作增加了{}余额", request.getMoney()));

                bill.setPm(BillConstants.BILL_PM_SUB);
                bill.setMark(StrUtil.format("后台操作给用户增加余额{}元", request.getMoney()));

                balanceRecord.setType(BalanceRecordConstants.BALANCE_RECORD_TYPE_ADD);
                balanceRecord.setBalance(user.getNowMoney().add(request.getMoney()));
                balanceRecord.setRemark(StrUtil.format(BalanceRecordConstants.BALANCE_RECORD_REMARK_SYSTEM_ADD, request.getMoney().setScale(2).toString()));

                updateNowMoney(user.getId(), request.getMoney(), Constants.OPERATION_TYPE_ADD);
            } else {
                userBill.setPm(0);
                userBill.setType(BillConstants.USER_BILL_TYPE_SYSTEM_SUB);
                userBill.setBalance(user.getNowMoney().subtract(request.getMoney()));
                userBill.setMark(StrUtil.format("后台操作减少了{}余额", request.getMoney()));

                bill.setPm(BillConstants.BILL_PM_ADD);
                bill.setMark(StrUtil.format("后台操作给用户扣减余额{}元", request.getMoney()));

                balanceRecord.setType(BalanceRecordConstants.BALANCE_RECORD_TYPE_SUB);
                balanceRecord.setBalance(user.getNowMoney().subtract(request.getMoney()));
                balanceRecord.setRemark(StrUtil.format(BalanceRecordConstants.BALANCE_RECORD_REMARK_SYSTEM_SUB, request.getMoney().setScale(2).toString()));

                updateNowMoney(user.getId(), request.getMoney(), Constants.OPERATION_TYPE_SUBTRACT);
            }
            billService.save(bill);
            userBillService.save(userBill);
            userBalanceRecordService.save(balanceRecord);
            return Boolean.TRUE;
        });
    }

    /**
     * 支付成功，用户信息变更
     *
     * @param id           用户id
     * @param isPromoter   是否成为推广员
     */
    @Override
    public Boolean paySuccessChange(Integer id, Boolean isPromoter) {
        UpdateWrapper<User> wrapper = Wrappers.update();
        wrapper.setSql("pay_count = pay_count + 1");
        if (isPromoter) {
            wrapper.set("is_promoter", 1);
            wrapper.set("promoter_time", DateUtil.date());
        }
        wrapper.eq("id", id);
        return update(wrapper);
    }

    /**
     * 根据用户id获取自己本身的推广用户
     *
     * @param userIdList List<Integer> 用户id集合
     * @return List<User>
     */
    @Override
    public List<Integer> getSpreadPeopleIdList(List<Integer> userIdList) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(User::getId); //查询用户id
        lambdaQueryWrapper.in(User::getSpreadUid, userIdList); //xx的下线集合
        List<User> list = dao.selectList(lambdaQueryWrapper);

        if (null == list || list.size() < 1) {
            return new ArrayList<>();
        }
        return list.stream().map(User::getId).distinct().collect(Collectors.toList());
    }

    /**
     * 根据用户id获取自己本身的推广用户
     *
     * @param userId           自己的id
     * @param spreadUserIdList 自己推广用户的id列表
     * @param sortKey          排序, 排序|childCount=团队排序,amountCount=金额排序,orderCount=订单排序
     * @param isAsc            排序值 DESC ASC
     */
    @Override
    public List<UserSpreadPeopleItemResponse> getSpreadPeopleList(Integer userId, List<Integer> spreadUserIdList, String keywords, String sortKey,
                                                                  String isAsc, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        Map<String, Object> map = new HashMap<>();
        map.put("uid", userId);
        map.put("userIdList", spreadUserIdList.stream().distinct().map(String::valueOf).collect(Collectors.joining(",")));
        if (StrUtil.isNotBlank(keywords)) {
            keywords = URLUtil.decode(keywords);
            map.put("keywords", "%" + keywords + "%");
        }
        map.put("sortKey", "create_time");
        if (StrUtil.isNotBlank(sortKey)) {
            map.put("sortKey", sortKey);
        }
        map.put("sortValue", Constants.SORT_DESC);
        if (isAsc.equalsIgnoreCase(Constants.SORT_ASC)) {
            map.put("sortValue", Constants.SORT_ASC);
        }

        return dao.getSpreadPeopleList(map);
    }

    /**
     * 推广人排行(取前50)
     *
     * @param type 时间范围(week-周，month-月)
     * @return List<User>
     */
    @Override
    public List<User> getSpreadPeopleTopByDate(String type) {
        QueryWrapper<User> queryWrapper = Wrappers.query();
        queryWrapper.select("count(id) as spread_count, spread_uid")
                .gt("spread_uid", 0)
                .eq("status", true);
        if (StrUtil.isNotBlank(type)) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(type);
            queryWrapper.between("spread_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        queryWrapper.groupBy("spread_uid").orderByDesc("spread_count");
        queryWrapper.last(" limit 50");
        List<User> spreadVoList = dao.selectList(queryWrapper);
        if (CollUtil.isEmpty(spreadVoList)) {
            return spreadVoList;
        }
        List<Integer> spreadIdList = spreadVoList.stream().map(User::getSpreadUid).collect(Collectors.toList());
        Map<Integer, User> userMap = getUidMapList(spreadIdList);
        for (User spreadVo : spreadVoList) {
            User user = userMap.get(spreadVo.getSpreadUid());
            spreadVo.setId(spreadVo.getSpreadUid());
            spreadVo.setAvatar(user.getAvatar());
            spreadVo.setNickname(user.getNickname());
        }
        return spreadVoList;
    }

    /**
     * 绑定推广关系（登录状态）
     *
     * @param spreadUid 推广人id
     */
    @Override
    public void bindSpread(Integer spreadUid) {
        //新用户会在注册的时候单独绑定，此处只处理登录用户
        if (ObjectUtil.isNull(spreadUid) || spreadUid <= 0) {
            return;
        }
        User user = getInfo();
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException(LoginResultCode.LOGIN_EXPIRE);
        }
        Boolean checkBingSpread = checkBingSpread(user, spreadUid, "old");
        if (!checkBingSpread) return;

        user.setSpreadUid(spreadUid);
        user.setSpreadTime(CrmebDateUtil.nowDateTime());
        Boolean execute = transactionTemplate.execute(e -> {
            updateById(user);
            updateSpreadCountByUid(spreadUid, Constants.OPERATION_TYPE_ADD);
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("绑定推广人时出错，userUid = {}, spreadUid = {}", user.getId(), spreadUid));
        }
    }

    /**
     * 获取用户总人数
     */
    @Override
    public Integer getTotalNum() {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.select(User::getId);
        lqw.eq(User::getIsLogoff, false);
        return dao.selectCount(lqw);
    }

    /**
     * 获取日期新增用户数
     *
     * @param date 日期 yyyy-MM-dd
     * @return 新增用户数
     */
    @Override
    public Integer getRegisterNumByDate(String date) {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(lqw);
    }

    /**
     * 获取用户渠道数据
     *
     * @return List
     */
    @Override
    public List<User> getChannelData() {
        QueryWrapper<User> wrapper = Wrappers.query();
        wrapper.select("register_type", "count(id) as pay_count");
        wrapper.eq("is_logoff", 0);
        wrapper.groupBy("register_type");
        return dao.selectList(wrapper);
    }

    /**
     * 更新用户推广人
     *
     * @param userId    用户ID
     * @param spreadUid 推广人ID
     */
    @Override
    public Boolean updateSpreadByUid(Integer userId, Integer spreadUid) {
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(User::getSpreadUid, spreadUid);
        wrapper.set(User::getSpreadTime, DateUtil.date());
        wrapper.eq(User::getId, userId);
        return update(wrapper);
    }

    /**
     * PC后台分销员列表
     */
    @Override
    public PageInfo<User> getRetailStorePeoplePage(RetailStorePeopleSearchRequest request) {
        Map<String, Object> map = CollUtil.newHashMap();
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        //时间范围
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), DateConstants.DATE_FORMAT);
            if (compareDateResult == -1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间不能大于结束时间！");
            }
            if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
                map.put("startTime", dateLimit.getStartTime());
                map.put("endTime", dateLimit.getEndTime());
            }
        }
        Page<User> pageUser = PageHelper.startPage(request.getPage(), request.getLimit());
        List<User> userList = dao.findRetailStorePeopleList(map);
        return CommonPage.copyPageInfo(pageUser, userList);
    }

    /**
     * 根据推广级别和其他参数当前用户下的推广列表
     *
     * @param request 推广列表参数
     * @return 当前用户的推广人列表
     */
    @Override
    public PageInfo<User> getRetailStoreSubUserList(RetailStoreSubUserSearchRequest request, PageParamRequest pageRequest) {
        if (request.getType().equals(1)) {// 一级推广人
            return getFirstSpreadUserListPage(request, pageRequest);
        }
        if (request.getType().equals(2)) {// 二级推广人
            return getSecondSpreadUserListPage(request, pageRequest);
        }
        return getAllSpreadUserListPage(request, pageRequest);
    }

    /**
     * 更新用户连续签到天数
     * @param day 连续签到天数
     * @param id 用户ID
     * @return Boolean
     */
    @Override
    public Boolean updateSignNumByUid(Integer day, Integer id) {
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(User::getSignNum, day);
        wrapper.eq(User::getId, id);
        return update(wrapper);
    }

    /**
     * 用户注销数据前置
     * 判断用户还存在积分、余额、佣金
     * 判断用户是否存在待发货订单
     * 判断用户是否存在退款申请中的退款单
     */
    @Override
    public UserLogoffBeforeResponse logoffBefore() {
        User user = getInfo();
        if (user.getIsLogoff()) {
            throw new CrmebException(UserResultCode.USER_LOGOFF);
        }
        UserLogoffBeforeResponse response = new UserLogoffBeforeResponse();
        response.setIsTip(true);
        if (user.getIntegral() > 0 || user.getNowMoney().compareTo(BigDecimal.ZERO) > 0
                || user.getBrokeragePrice().compareTo(BigDecimal.ZERO) > 0) {
            return response;
        }
        if (orderService.isExistPendingOrderByUid(user.getId())) {
            return response;
        }
        if (refundOrderService.getRefundingCount(user.getId()) > 0) {
            return response;
        }
        response.setIsTip(false);
        return response;
    }

    /**
     * 用户注销
     * 1.用户置为注销状态
     * 2.user_token软删除
     * 3.清除购物车数据
     * 4.清除关注、收藏数据
     * 5.接触分销关系
     * 6.清空用户登录状态
     * 7.清除用户标签
     * 8.关闭推广人按钮
     * 9.删除用户社区笔记
     * 10.删除用户社区笔记关系
     */
    @Override
    public Boolean logoff() {
        User user = getInfo();
        user.setIsLogoff(true);
        user.setLogoffTime(DateUtil.date());
        user.setTagId("");
        user.setIsPromoter(false);

        Boolean execute = transactionTemplate.execute(e -> {
            userTokenService.deleteByUid(user.getId());
            cartService.deleteByUid(user.getId());
            productRelationService.deleteByUid(user.getId());
            userMerchantCollectService.deleteByUid(user.getId());
            if (user.getSpreadUid() > 0) {
                updateSpreadCountByUid(user.getSpreadUid(), Constants.OPERATION_TYPE_SUBTRACT);
                user.setSpreadUid(0);
            }
            if (user.getSpreadCount() > 0) {
                batchRemoveSpreadUid(user.getId());
                user.setSpreadCount(0);
            }
            communityNotesService.deleteByUid(user.getId());
            communityNotesRelationService.deleteByAuthorId(user.getId());
            boolean update = updateById(user);
            if (!update) {
                logger.error("更新用户注销状态失败，用户id： {}", user.getId());
                e.setRollbackOnly();
                return update;
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException(UserResultCode.USER_LOGOFF_FAILED);
        }
        tokenComponent.logout(RequestUtil.getRequest());
        return execute;
    }

    /**
     * 管理端用户详情
     * @param id 用户ID
     */
    @Override
    public UserAdminDetailResponse getAdminDetail(Integer id) {
        User user = getById(id);
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException(UserResultCode.USER_NOT_EXIST);
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (admin.getMerId() > 0) {
            if (!userMerchantCollectService.isCollect(user.getId(), admin.getMerId())) {
                throw new CrmebException(UserResultCode.USER_NOT_COLLECT_MERCHANT);
            }
        }
        UserAdminDetailResponse response = new UserAdminDetailResponse();
        BeanUtils.copyProperties(user, response);
        
        // 设置推广人信息
        if (user.getSpreadUid() > 0) {
            User spreadUser = getById(user.getSpreadUid());
            if (ObjectUtil.isNotNull(spreadUser)) {
                response.setSpreadName(spreadUser.getNickname());
            }
        }
        
        // 手机号码脱敏（编辑时不需要脱敏，注释掉）
        // if (StrUtil.isNotBlank(user.getPhone())) {
        //     response.setPhone(CrmebUtil.maskMobile(user.getPhone()));
        // }
        
        // 设置用户等级
        SystemUserLevel systemUserLevel = systemUserLevelService.getByLevelId(user.getLevel());
        response.setGrade(ObjectUtil.isNotNull(systemUserLevel) ? systemUserLevel.getGrade() : 0);
        
        // 设置新增的字段
        response.setIdentityCardNo(user.getIdentityCardNo());
        response.setUserType(user.getUserType());
        response.setFarmId(user.getFarmId());
        response.setFarmCode(user.getFarmCode());
        response.setFarmName(user.getFarmName());
        response.setOrganizationId(user.getOrganizationId());
        response.setOrganizationName(user.getOrganizationName());
        response.setOrganizationCode(user.getOrganizationCode());
        response.setProvinceId(user.getProvinceId());
        response.setCityId(user.getCityId());
        response.setDistrictId(user.getDistrictId());
        
        return response;
    }

    /**
     * 更新用户等级
     * @param userId 用户ID
     * @param level 用户等级
     */
    @Override
    public Boolean updateUserLevel(Integer userId, Integer level) {
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(User::getLevel, level);
        wrapper.eq(User::getId, userId);
        return update(wrapper);
    }

    /**
     * 通过生日获取用户列表
     * @param birthday 生日日期
     */
    @Override
    public List<User> findByBirthday(String birthday) {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.select(User::getId, User::getPhone);
        lqw.eq(User::getIsLogoff, 0);
        lqw.eq(User::getStatus, 1);
//        lqw.eq(User::getBirthday, birthday);
        lqw.apply("date_format(birthday, '%m-%d') = {0}", birthday);
        return dao.selectList(lqw);
    }

    /**
     * 手机号修改密码获取验证码
     */
    @Override
    public Boolean getUpdatePasswordPhoneCode() {
        User user = getInfo();
        return smsService.sendCommonCode(user.getPhone(), SmsConstants.VERIFICATION_CODE_SCENARIO_USER_UPDATE_PASSWORD);
    }

    /**
     * 获取当前操作管理用户 或者移动端管理用户
     * @param isEmployee
     * @return
     */
    @Override
    public SystemAdmin getSystemAdminOrMerchantEmployee(Boolean isEmployee) {
        SystemAdmin systemAdmin = null;
        if(isEmployee){
            Merchant employeeInfo = getEmployeeInfo();
            UserInfoResponse userInfo = getUserInfo();
            systemAdmin = new SystemAdmin();
            systemAdmin.setId(userInfo.getId());
            systemAdmin.setMerId(employeeInfo.getId());
        }else{
            systemAdmin = SecurityUtil.getLoginUserVo().getUser();

        }
        return systemAdmin;
    }

    /**
     * 获取当前的移动端商户管理者信息
     * @return 移动端管理者身份
     */
    @Override
    public SystemAdmin getSystemAdminByMerchantEmployee() {
        SystemAdmin systemAdmin = new SystemAdmin();
        Merchant employeeInfo = getEmployeeInfo();
        UserInfoResponse userInfo = getUserInfo();
        systemAdmin.setId(userInfo.getId());
        systemAdmin.setMerId(employeeInfo.getId());

        return systemAdmin;
    }

    /**
     * 批量清除用户推广人
     * @param spreadUid 推广人id
     */
    private Boolean batchRemoveSpreadUid(Integer spreadUid) {
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(User::getSpreadUid, 0);
        wrapper.eq(User::getSpreadUid, spreadUid);
        return update(wrapper);
    }

    /**
     * 分页获取所有推广员
     */
    private PageInfo<User> getAllSpreadUserListPage(RetailStoreSubUserSearchRequest request, PageParamRequest pageRequest) {
        // 先所有一级推广员
        List<User> firstUserList = getSpreadListBySpreadIdAndType(request.getUid(), 0);
        if (CollUtil.isEmpty(firstUserList)) {
            return new PageInfo<>(CollUtil.newArrayList());
        }
        List<Integer> userIds = firstUserList.stream().map(User::getId).distinct().collect(Collectors.toList());
        Page<User> userPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getId, User::getAvatar, User::getNickname, User::getIsPromoter, User::getSpreadCount, User::getPayCount);
        queryWrapper.in(User::getId, userIds);
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String decode = URLUtil.decode(request.getKeywords());
            queryWrapper.and(e -> e.like(User::getNickname, decode).or().eq(User::getId, decode)
                    .or().eq(User::getPhone, decode));
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            queryWrapper.between(User::getSpreadTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        List<User> userList = dao.selectList(queryWrapper);
        return CommonPage.copyPageInfo(userPage, userList);
    }

    /**
     * 分页获取二级推广员
     */
    private PageInfo<User> getSecondSpreadUserListPage(RetailStoreSubUserSearchRequest request, PageParamRequest pageRequest) {
        // 先获取一级推广员
        List<User> firstUserList = getSpreadListBySpreadIdAndType(request.getUid(), 1);
        if (CollUtil.isEmpty(firstUserList)) {
            return new PageInfo<>(CollUtil.newArrayList());
        }
        List<Integer> userIds = firstUserList.stream().map(User::getId).distinct().collect(Collectors.toList());
        Page<User> userPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getId, User::getAvatar, User::getNickname, User::getIsPromoter, User::getSpreadCount, User::getPayCount);
        queryWrapper.in(User::getSpreadUid, userIds);
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String decode = URLUtil.decode(request.getKeywords());
            queryWrapper.and(e -> e.like(User::getNickname, decode).or().eq(User::getId, decode)
                    .or().eq(User::getPhone, decode));
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            queryWrapper.between(User::getSpreadTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        List<User> userList = dao.selectList(queryWrapper);
        return CommonPage.copyPageInfo(userPage, userList);
    }

    /**
     * 获取推广人列表
     *
     * @param spreadUid 父Uid
     * @param type      类型 0 = 全部 1=一级推广人 2=二级推广人
     */
    private List<User> getSpreadListBySpreadIdAndType(Integer spreadUid, Integer type) {
        // 获取一级推广人
        List<User> userList = getSpreadListBySpreadId(spreadUid);
        if (CollUtil.isEmpty(userList)) return userList;
        if (type.equals(1)) return userList;
        // 获取二级推广人
        List<User> userSecondList = CollUtil.newArrayList();
        userList.forEach(user -> {
            List<User> childUserList = getSpreadListBySpreadId(user.getId());
            if (CollUtil.isNotEmpty(childUserList)) {
                userSecondList.addAll(childUserList);
            }
        });
        if (type.equals(2)) {
            return userSecondList;
        }
        userList.addAll(userSecondList);
        return userList;
    }

    /**
     * 获取推广人列表
     *
     * @param spreadUid 父Uid
     */
    private List<User> getSpreadListBySpreadId(Integer spreadUid) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getSpreadUid, spreadUid);
        return dao.selectList(queryWrapper);
    }

    /**
     * 分页获取一级推广员
     */
    private PageInfo<User> getFirstSpreadUserListPage(RetailStoreSubUserSearchRequest request, PageParamRequest pageRequest) {
        Page<User> userPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.select(User::getId, User::getAvatar, User::getNickname, User::getIsPromoter, User::getSpreadCount, User::getPayCount);
        lqw.eq(User::getSpreadUid, request.getUid());
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String decode = URLUtil.decode(request.getKeywords());
            lqw.and(e -> e.like(User::getNickname, decode).or().eq(User::getId, decode)
                    .or().eq(User::getPhone, decode));
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            lqw.between(User::getSpreadTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        List<User> userList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(userPage, userList);
    }


    /**
     * 赠送用户付费会员
     */
    @Override
    public Boolean giftPaidMember(GiftPaidMemberRequest request) {
        List<Integer> uidList = CrmebUtil.stringToArray(request.getIds());
        PaidMemberCard paidMemberCard = paidMemberCardService.getByIdException(request.getCardId());
        if (!paidMemberCard.getStatus()) {
            throw new CrmebException(MemberResultCode.PAID_MEMBER_CARD_CLOSE);
        }
        if (!paidMemberCard.getType().equals(1)) {
            throw new CrmebException(MemberResultCode.PAID_MEMBER_CARD_RESELECT);
        }
        List<User> userList = findByIdList(uidList);
        for (int i = 0; i < userList.size(); ) {
            User user = userList.get(i);
            if (user.getIsLogoff() || user.getIsPermanentPaidMember()) {
                userList.remove(i);
                continue;
            }
            i++;
        }
        if (CollUtil.isEmpty(userList)) {
            return Boolean.TRUE;
        }
        return generatePaidMemberOrder(userList, paidMemberCard);
    }

    /**
     * 生成付费会员订单
     */
    private Boolean generatePaidMemberOrder(List<User> userList, PaidMemberCard paidMemberCard) {
        DateTime dateTime = DateUtil.date();
        return transactionTemplate.execute(e -> {
            boolean update = false;
            for (User user : userList) {
                PaidMemberOrder order = new PaidMemberOrder();
                order.setUid(user.getId());
                order.setOrderNo(CrmebUtil.getOrderNo(OrderConstants.PAID_MEMBER_ORDER_PREFIX));
                order.setCardId(paidMemberCard.getId());
                order.setCardName(paidMemberCard.getName());
                order.setType(paidMemberCard.getType());
                order.setDeadlineDay(paidMemberCard.getDeadlineDay());
                order.setOriginalPrice(paidMemberCard.getOriginalPrice());
                order.setPrice(BigDecimal.ZERO);
                order.setGiftBalance(BigDecimal.ZERO);
                order.setPayType("give");
                order.setPayChannel("give");
                order.setPaid(true);
                order.setPayTime(dateTime);
                if (user.getIsPaidMember()) {
                    DateTime offsetDay = DateUtil.offsetDay(DateTime.of(user.getPaidMemberExpirationTime()), paidMemberCard.getDeadlineDay());
                    order.setCardExpirationTime(offsetDay);
                    update = updateUserPaidMember(user.getId(), true, false, offsetDay, user.getPaidMemberExpirationTime());
                } else {
                    DateTime offsetDay = DateUtil.offsetDay(dateTime, paidMemberCard.getDeadlineDay());
                    order.setCardExpirationTime(offsetDay);
                    update = updateUserPaidMember(user.getId(), true, false, offsetDay, null);
                }
                if (!update) {
                    logger.error("赠送会员卡，更新用户数据失败,userID={},cardId={}", user.getId(), paidMemberCard.getId());
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
                paidMemberOrderService.save(order);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 更新用户付费会员信息
     */
    @Override
    public Boolean updateUserPaidMember(Integer uid, boolean isPaidMember, boolean isPermanentPaidMember, DateTime expirationTime, Date oldTime) {
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(User::getIsPaidMember, isPaidMember);
        if (isPermanentPaidMember) {
            wrapper.set(User::getIsPermanentPaidMember, true);
        }
        wrapper.set(User::getPaidMemberExpirationTime, expirationTime);
        wrapper.eq(User::getId, uid);
        if (ObjectUtil.isNotNull(oldTime)) {
            wrapper.eq(User::getPaidMemberExpirationTime, oldTime);
        }
        return update(wrapper);
    }

    /**
     * 查询过期的会员
     */
    @Override
    public List<User> findMemberExpirationList() {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.eq(User::getIsLogoff, 0);
        lqw.eq(User::getIsPaidMember, 1);
        lqw.eq(User::getIsPermanentPaidMember, 0);
        String format = DateUtil.format(DateUtil.date(), DateConstants.DATE_FORMAT_HHMM);
        lqw.apply("DATE_FORMAT(paid_member_expiration_time, '%Y-%m-%d %H:%i') < {0}", format);
        return dao.selectList(lqw);
    }

    /**
     * 会员过期处理
     */
    @Override
    public Boolean memberExpirationProcessing() {
        List<User> userList = findMemberExpirationList();
        return transactionTemplate.execute(e -> {
            for (User user : userList) {
                LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
                wrapper.set(User::getIsPaidMember, 0);
                wrapper.eq(User::getId, user.getId());
                wrapper.eq(User::getPaidMemberExpirationTime, user.getPaidMemberExpirationTime());
                update(wrapper);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 获取用户授信统计信息
     */
    @Override
    public com.zbkj.common.response.UserCreditStatisticsResponse getUserCreditStatistics() {
        com.zbkj.common.response.UserCreditStatisticsResponse statistics = 
            new com.zbkj.common.response.UserCreditStatisticsResponse();
        
        // 获取总用户数（排除注销用户）
        LambdaQueryWrapper<User> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(User::getIsLogoff, false);
        Long totalUsers = dao.selectCount(totalWrapper).longValue();
        statistics.setTotalUsers(totalUsers);
        
        // 获取已授信用户数（授信状态为1）
        LambdaQueryWrapper<User> creditedWrapper = new LambdaQueryWrapper<>();
        creditedWrapper.eq(User::getIsLogoff, false);
        creditedWrapper.eq(User::getCreditStatus, 1);
        Long creditedUsers = dao.selectCount(creditedWrapper).longValue();
        statistics.setCreditedUsers(creditedUsers);
        
        // 获取申请中用户数（授信状态为0）
        LambdaQueryWrapper<User> applyingWrapper = new LambdaQueryWrapper<>();
        applyingWrapper.eq(User::getIsLogoff, false);
        applyingWrapper.eq(User::getCreditStatus, 0);
        Long applyingUsers = dao.selectCount(applyingWrapper).longValue();
        statistics.setApplyingUsers(applyingUsers);
        
        // 未授信用户数 = 总用户数 - 已授信用户数 - 申请中用户数
        Long uncreditedUsers = totalUsers - creditedUsers - applyingUsers;
        statistics.setUncreditedUsers(uncreditedUsers);
        
        // 获取游客数量（用户类型为0或null）
        LambdaQueryWrapper<User> guestWrapper = new LambdaQueryWrapper<>();
        guestWrapper.eq(User::getIsLogoff, false);
        guestWrapper.and(wrapper -> wrapper.eq(User::getUserType, 0).or().isNull(User::getUserType));
        Long guestUsers = dao.selectCount(guestWrapper).longValue();
        statistics.setGuestUsers(guestUsers);
        
        // 获取管理员数量（用户类型为1）
        LambdaQueryWrapper<User> adminWrapper = new LambdaQueryWrapper<>();
        adminWrapper.eq(User::getIsLogoff, false);
        adminWrapper.eq(User::getUserType, 1);
        Long adminUsers = dao.selectCount(adminWrapper).longValue();
        statistics.setAdminUsers(adminUsers);
        
        // 获取员工数量（用户类型为2）
        LambdaQueryWrapper<User> employeeWrapper = new LambdaQueryWrapper<>();
        employeeWrapper.eq(User::getIsLogoff, false);
        employeeWrapper.eq(User::getUserType, 2);
        Long employeeUsers = dao.selectCount(employeeWrapper).longValue();
        statistics.setEmployeeUsers(employeeUsers);
        
        // 获取授信金额统计（只统计已授信的用户）
        LambdaQueryWrapper<User> amountWrapper = new LambdaQueryWrapper<>();
        amountWrapper.eq(User::getIsLogoff, false);
        amountWrapper.eq(User::getCreditStatus, 1);
        amountWrapper.isNotNull(User::getCreditLimit);
        List<User> creditUsers = dao.selectList(amountWrapper);
        
        BigDecimal totalCreditAmount = BigDecimal.ZERO;
        BigDecimal usedCreditAmount = BigDecimal.ZERO;
        BigDecimal remainingCreditAmount = BigDecimal.ZERO;
        BigDecimal totalAssessmentAmount = BigDecimal.ZERO;
        
        for (User user : creditUsers) {
            if (user.getCreditLimit() != null) {
                totalCreditAmount = totalCreditAmount.add(user.getCreditLimit());
            }
            if (user.getUsedCredit() != null) {
                usedCreditAmount = usedCreditAmount.add(user.getUsedCredit());
            }
            if (user.getRemainingCredit() != null) {
                remainingCreditAmount = remainingCreditAmount.add(user.getRemainingCredit());
            }
            if (user.getAssessmentAmount() != null) {
                totalAssessmentAmount = totalAssessmentAmount.add(user.getAssessmentAmount());
            }
        }
        
        statistics.setTotalCreditAmount(totalCreditAmount);
        statistics.setUsedCreditAmount(usedCreditAmount);
        statistics.setRemainingCreditAmount(remainingCreditAmount);
        statistics.setTotalAssessmentAmount(totalAssessmentAmount);
        
        // 计算授信使用率（已使用授信额度 / 总授信额度）
        BigDecimal creditUtilizationRate = BigDecimal.ZERO;
        if (totalCreditAmount.compareTo(BigDecimal.ZERO) > 0) {
            creditUtilizationRate = usedCreditAmount.divide(totalCreditAmount, 4, BigDecimal.ROUND_HALF_UP);
        }
        statistics.setCreditUtilizationRate(creditUtilizationRate);
        
        // 计算授信覆盖率（已授信用户数 / 总用户数）
        BigDecimal creditCoverageRate = BigDecimal.ZERO;
        if (totalUsers > 0) {
            creditCoverageRate = new BigDecimal(creditedUsers).divide(new BigDecimal(totalUsers), 4, BigDecimal.ROUND_HALF_UP);
        }
        statistics.setCreditCoverageRate(creditCoverageRate);
        
        return statistics;
    }

    @Override
    public List<Integer> getUserIdsByOrganizationId(String organizationId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOrganizationId, organizationId);
        return dao.selectList(queryWrapper).stream().map(User::getId).collect(Collectors.toList());
    }

    @Override
    public List<User> getByOrganizationId(String organizationId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOrganizationId, organizationId);
         //用户id不等于当前登录人的用户ID
        queryWrapper.ne(User::getId, this.getUserId());
        return dao.selectList(queryWrapper);
    }
}
