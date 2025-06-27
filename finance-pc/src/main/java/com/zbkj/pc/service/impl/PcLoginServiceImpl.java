package com.zbkj.pc.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zbkj.common.constants.*;
import com.zbkj.common.dto.ThirdLogin;
import com.zbkj.common.enums.UserTypeEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.finance.FarmInstitution;
import com.zbkj.common.model.finance.OrgCategory;
import com.zbkj.common.model.finance.Organization;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.merchant.MerchantEmployee;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserToken;
import com.zbkj.common.request.*;
import com.zbkj.common.response.FrontLoginConfigResponse;
import com.zbkj.common.response.LoginResponse;
import com.zbkj.common.response.employee.EmployeeMerchantResponse;
import com.zbkj.common.response.employee.FrontMerchantEmployeeResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.WechatResultCode;
import com.zbkj.common.token.FrontTokenComponent;
import com.zbkj.common.utils.*;
import com.zbkj.common.vo.*;
import com.zbkj.pc.service.PcLoginService;
import com.zbkj.service.service.*;
import com.zbkj.service.service.finance.FarmInstitutionService;
import com.zbkj.service.service.finance.OrgCategoryService;
import com.zbkj.service.service.finance.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 移动端登录服务类
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
@Slf4j
@Service
public class PcLoginServiceImpl implements PcLoginService {

    private static final Logger logger = LoggerFactory.getLogger(PcLoginServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FrontTokenComponent tokenComponent;
    @Autowired
    private SmsService smsService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private WechatService wechatService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private MerchantEmployeeService merchantEmployeeService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private FarmInstitutionService farmInstitutionService;
    @Autowired
    private OrgCategoryService orgCategoryService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private SystemAdminService systemAdminService;
    @Autowired
    private MerchantApplyService merchantApplyService;
    @Autowired
    private SystemRoleService systemRoleService;
    private final static String secret="9b0fe37da784d19ac31268829a5999b3";
    private final static String iv="fde66913e2f6ecb2b65631d86d3fc050";

    /**
     * 发送短信验证码
     * @param phone 手机号
     * @return Boolean
     */
    @Override
    public Boolean sendLoginCode(String phone) {
        return smsService.sendCommonCode(phone, SmsConstants.VERIFICATION_CODE_SCENARIO_LOGIN);
    }

    /**
     * 退出登录
     * @param request HttpServletRequest
     */
    @Override
    public void loginOut(HttpServletRequest request) {
        tokenComponent.logout(request);
    }

    /**
     * 手机号验证码登录
     * @param loginRequest 登录信息
     * @return LoginResponse
     */
    @Override
    public LoginResponse phoneCaptchaLogin(LoginMobileRequest loginRequest) {
        if (StrUtil.isBlank(loginRequest.getCaptcha())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "手机号码验证码不能为空");
        }
        Integer spreadPid = Optional.ofNullable(loginRequest.getSpreadPid()).orElse(0);
        //检测验证码
        smsService.checkValidateCode(SmsConstants.VERIFICATION_CODE_SCENARIO_LOGIN, loginRequest.getPhone(), loginRequest.getCaptcha());
        //查询用户信息
        User user = userService.getByPhone(loginRequest.getPhone());
        if (ObjectUtil.isNull(user)) {// 此用户不存在，走新用户注册流程
            user = userService.registerPhone(loginRequest.getPhone(), spreadPid);
            return getLoginResponse_V1_3(user, true);
        }
        return commonLogin(user, spreadPid);

    }

    /**
     * 手机号密码登录
     * @param loginRequest 登录信息
     * @return LoginResponse
     */
    @Override
    public LoginResponse phonePasswordLogin(LoginPasswordRequest loginRequest) {
        if (StrUtil.isBlank(loginRequest.getPassword())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "密码不能为空");
        }

        //查询用户信息
        User user = userService.getByPhone(loginRequest.getPhone());
        if (ObjectUtil.isNull(user)) {// 此用户不存在，走新用户注册流程
            throw new CrmebException("用户名或密码不正确");
        }
         String password=  CrmebUtil.encryptPassword(loginRequest.getPassword(), loginRequest.getPhone());
        if (!CrmebUtil.encryptPassword(loginRequest.getPassword(), loginRequest.getPhone()).equals(user.getPwd())) {
            throw new CrmebException("用户名或密码不正确");
        }
        if (!user.getStatus()) {
            throw new CrmebException("当前帐户已禁用，请与管理员联系！");
        }
        Integer spreadPid = Optional.ofNullable(loginRequest.getSpreadPid()).orElse(0);
        return commonLogin(user, spreadPid);
    }

    /**
     * 微信公众号授权登录
     * @param request 登录参数
     * @return LoginResponse
     */
    @Override
    public LoginResponse wechatPublicLogin(WechatPublicLoginRequest request) {
        // 通过code获取获取公众号授权信息
        WeChatOauthToken oauthToken = wechatService.getOauth2AccessToken(request.getCode());
        //检测是否存在
        UserToken userToken = userTokenService.getByOpenidAndType(oauthToken.getOpenId(),  UserConstants.USER_TOKEN_TYPE_WECHAT);
        Integer spreadPid = Optional.ofNullable(request.getSpreadPid()).orElse(0);
        LoginResponse loginResponse = new LoginResponse();
        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
            User user = userService.getById(userToken.getUid());
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }
            return commonLogin(user, spreadPid);
        }
        // 没有用户，走创建用户流程
        // 从微信获取用户信息，存入Redis中，将key返回给前端，前端在下一步绑定手机号的时候下发
        WeChatAuthorizeLoginUserInfoVo userInfo = wechatService.getSnsUserInfo(oauthToken.getAccessToken(), oauthToken.getOpenId());
        RegisterThirdUserRequest registerThirdUserRequest = new RegisterThirdUserRequest();
        BeanUtils.copyProperties(userInfo, registerThirdUserRequest);
        registerThirdUserRequest.setSpreadPid(spreadPid);
        registerThirdUserRequest.setType(UserConstants.REGISTER_TYPE_WECHAT);
        registerThirdUserRequest.setOpenId(oauthToken.getOpenId());
        String key = SecureUtil.md5(oauthToken.getOpenId());
        redisUtil.set(key, JSONObject.toJSONString(registerThirdUserRequest), (long) (60 * 2), TimeUnit.MINUTES);

        loginResponse.setType(LoginConstants.LOGIN_STATUS_REGISTER);
        loginResponse.setKey(key);
        return loginResponse;
    }

    /**
     * 微信登录小程序授权登录
     * @param request 用户参数
     * @return LoginResponse
     */
    @Override
    public LoginResponse wechatRoutineLogin(RegisterThirdUserRequest request) {
        WeChatMiniAuthorizeVo response = wechatService.miniAuthCode(request.getCode());
        //检测是否存在
        UserToken userToken = userTokenService.getByOpenidAndType(response.getOpenId(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        Integer spreadPid = Optional.ofNullable(request.getSpreadPid()).orElse(0);
        LoginResponse loginResponse = new LoginResponse();
        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
            User user = userService.getById(userToken.getUid());
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }
            return commonLogin(user, spreadPid);
        }
        request.setSpreadPid(spreadPid);
        request.setType(UserConstants.REGISTER_TYPE_ROUTINE);
        request.setOpenId(response.getOpenId());
        String key = SecureUtil.md5(response.getOpenId());
        redisUtil.set(key, JSONObject.toJSONString(request), (long) (60 * 2), TimeUnit.MINUTES);
        loginResponse.setType(LoginConstants.LOGIN_STATUS_REGISTER);
        loginResponse.setKey(key);
        return loginResponse;
    }

    /**
     * 微信注册绑定手机号
     * @param request 请求参数
     * @return 登录信息
     */
    @Override
    public LoginResponse wechatRegisterBindingPhone(WxBindingPhoneRequest request) {
        // 检验并获取手机号
        checkBindingPhone(request);

        // 进入创建用户绑定手机号流程
        String value = redisUtil.get(request.getKey());
        if (StrUtil.isBlank(value)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "用户缓存已过期，请清除缓存重新登录");
        }
        RegisterThirdUserRequest registerThirdUserRequest = JSONObject.parseObject(value, RegisterThirdUserRequest.class);
        if (!request.getType().equals(registerThirdUserRequest.getType())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "用户的类型与缓存中的类型不符");
        }

        boolean isNew = true;
        User user = userService.getByPhone(request.getPhone());
        // 查询是否用对应得token
        Integer userTokenType = getUserTokenType(request.getType());
        if (ObjectUtil.isNotNull(user)) {// 历史用户校验
            if (request.getType().equals(UserConstants.REGISTER_TYPE_WECHAT) && user.getIsWechatPublic()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "该手机号已绑定微信公众号");
            }
            if (request.getType().equals(UserConstants.REGISTER_TYPE_ROUTINE) && user.getIsWechatRoutine()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "该手机号已绑定微信小程序");
            }
            if (request.getType().equals(UserConstants.REGISTER_TYPE_ANDROID_WX) && user.getIsWechatAndroid()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "该手机号已绑定微信Android");
            }
            if (request.getType().equals(UserConstants.REGISTER_TYPE_IOS_WX) && user.getIsWechatIos()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "该手机号已绑定微信IOS");
            }
            UserToken userToken = userTokenService.getTokenByUserId(user.getId(), userTokenType);
            if (ObjectUtil.isNotNull(userToken)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "该手机号已被注册");
            }
            isNew = false;
        } else {
            user = new User();
            user.setRegisterType(registerThirdUserRequest.getType());
            user.setPhone(request.getPhone());
            user.setAccount(request.getPhone());
            user.setSpreadUid(0);
            user.setPwd(CommonUtil.createPwd(request.getPhone()));
            user.setNickname(CommonUtil.createNickName(request.getPhone()));
            user.setAvatar(systemConfigService.getValueByKey(SysConfigConstants.USER_DEFAULT_AVATAR_CONFIG_KEY));
            user.setSex(0);
            user.setUserType(UserTypeEnum.USER_TYPE_GUEST.getCode());
            user.setAddress("");
            user.setLevel(1);
        }
        switch (request.getType()) {
            case UserConstants.REGISTER_TYPE_WECHAT:
                user.setIsWechatPublic(true);
                break;
            case UserConstants.REGISTER_TYPE_ROUTINE:
                user.setIsWechatRoutine(true);
                break;
            case UserConstants.REGISTER_TYPE_IOS_WX:
                user.setIsWechatIos(true);
                break;
            case UserConstants.REGISTER_TYPE_ANDROID_WX:
                user.setIsWechatAndroid(true);
                break;
        }
        user.setLastLoginTime(CrmebDateUtil.nowDateTime());
        User finalUser = user;
        boolean finalIsNew = isNew;
        Boolean execute = transactionTemplate.execute(e -> {
            Integer spreadPid = Optional.ofNullable(registerThirdUserRequest.getSpreadPid()).orElse(0);
            if (finalIsNew) {// 新用户
                // 分销绑定
                if (spreadPid > 0 && userService.checkBingSpread(finalUser, registerThirdUserRequest.getSpreadPid(), "new")) {
                    finalUser.setSpreadUid(registerThirdUserRequest.getSpreadPid());
                    finalUser.setSpreadTime(CrmebDateUtil.nowDateTime());
                    userService.updateSpreadCountByUid(registerThirdUserRequest.getSpreadPid(), Constants.OPERATION_TYPE_ADD);
                }
                userService.save(finalUser);
            } else {
                userService.updateById(finalUser);
                if (finalUser.getSpreadUid().equals(0) && spreadPid > 0) {
                    // 绑定推广关系
                    bindSpread(finalUser, spreadPid);
                }
            }
            userTokenService.bind(registerThirdUserRequest.getOpenId(), userTokenType, finalUser.getId());
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("微信用户注册生成失败，openid = {}, key = {}", registerThirdUserRequest.getOpenId(), request.getKey()));
            throw new CrmebException(StrUtil.format("微信用户注册生成失败，openid = {}, key = {}", registerThirdUserRequest.getOpenId(), request.getKey()));
        }
        return getLoginResponse_V1_3(finalUser, isNew);
    }

    /**
     * 如果是商家管理员，那么自动赋予当前登录账号权限 谨慎调用
     * @param user 当前登录的用户信息
     * @return 移动端商家管理权限
     */
    @Override
    public LoginResponse isEmployeeAuto(User user) {
        List<MerchantEmployee> shopMangerList = merchantEmployeeService.getShopMangerByUserId(user.getId());
        if(!shopMangerList.isEmpty()){
            return commonLogin(user, 0);
        }
        return null;

    }

    /**
     * 获取用户Token类型
     * @param type 用户注册类型
     */
    private Integer getUserTokenType(String type) {
        Integer userTokenType = 0;
        switch (type) {
            case UserConstants.REGISTER_TYPE_WECHAT:
                userTokenType = UserConstants.USER_TOKEN_TYPE_WECHAT;
                break;
            case UserConstants.REGISTER_TYPE_ROUTINE:
                userTokenType = UserConstants.USER_TOKEN_TYPE_ROUTINE;
                break;
            case UserConstants.REGISTER_TYPE_IOS_WX:
                userTokenType = UserConstants.USER_TOKEN_TYPE_IOS_WX;
                break;
            case UserConstants.REGISTER_TYPE_ANDROID_WX:
                userTokenType = UserConstants.USER_TOKEN_TYPE_ANDROID_WX;
                break;
        }
        return userTokenType;
    }

    /**
     * 绑定手机号数据校验
     */
    private void checkBindingPhone(WxBindingPhoneRequest request) {
        if (request.getType().equals(UserConstants.REGISTER_TYPE_WECHAT) || request.getType().equals(UserConstants.REGISTER_TYPE_IOS_WX) || request.getType().equals(UserConstants.REGISTER_TYPE_ANDROID_WX)) {
            if (StrUtil.isBlank(request.getPhone()) || StrUtil.isBlank(request.getCaptcha())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "手机号、验证码不能为空");
            }
            smsService.checkValidateCode(SmsConstants.VERIFICATION_CODE_SCENARIO_LOGIN, request.getPhone(), request.getCaptcha());
        } else {
            // 小程序自填手机号校验
            if (StrUtil.isNotBlank(request.getCaptcha())) {
                if (StrUtil.isBlank(request.getPhone())) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "手机号不能为空");
                }
                smsService.checkValidateCode(SmsConstants.VERIFICATION_CODE_SCENARIO_LOGIN, request.getPhone(), request.getCaptcha());
                return;
            }
            //  获取微信小程序手机号 参数校验
            if (StrUtil.isBlank(request.getCode())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "小程序获取手机号code不能为空");
            }
            if (StrUtil.isBlank(request.getEncryptedData())) {
//                throw new CrmebException("小程序获取手机号加密数据不能为空");
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请认证微信账号：获取手机号码失败");
            }
            if (StrUtil.isBlank(request.getIv())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "小程序获取手机号加密算法的初始向量不能为空");
            }
            // 获取appid
            String programAppId = systemConfigService.getValueByKey(WeChatConstants.WECHAT_MINI_APPID);
            if (StringUtils.isBlank(programAppId)) {
                throw new CrmebException(WechatResultCode.ROUTINE_APPID_NOT_CONFIG);
            }

            WeChatMiniAuthorizeVo response = wechatService.miniAuthCode(request.getCode());
            System.out.println("小程序登陆成功 = " + JSON.toJSONString(response));
            String decrypt = WxUtil.decrypt(programAppId, request.getEncryptedData(), response.getSessionKey(), request.getIv());
            if (StrUtil.isBlank(decrypt)) {
                throw new CrmebException("微信小程序获取手机号解密失败");
            }
            JSONObject jsonObject = JSONObject.parseObject(decrypt);
            if (StrUtil.isBlank(jsonObject.getString("phoneNumber"))) {
                throw new CrmebException("微信小程序没有获取到有效的手机号");
            }
            request.setPhone(jsonObject.getString("phoneNumber"));
        }
    }

    /**
     * 绑定分销关系
     *
     * @param user      User 用户user类
     * @param spreadUid Integer 推广人id
     * @return Boolean
     */
    private Boolean bindSpread(User user, Integer spreadUid) {
        Boolean checkBingSpread = userService.checkBingSpread(user, spreadUid, "old");
        if (!checkBingSpread) return false;

        user.setSpreadUid(spreadUid);
        user.setSpreadTime(CrmebDateUtil.nowDateTime());

        Boolean execute = transactionTemplate.execute(e -> {
            userService.updateById(user);
            userService.updateSpreadCountByUid(spreadUid, Constants.OPERATION_TYPE_ADD);
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("绑定推广人时出错，userUid = {}, spreadUid = {}", user.getId(), spreadUid));
        }
        return execute;
    }

    /**
     * 获取登录配置
     */
    @Override
    public FrontLoginConfigResponse getLoginConfig() {
        List<String> keyList = new ArrayList<>();
        keyList.add(SysConfigConstants.CONFIG_KEY_MOBILE_LOGIN_LOGO);
        keyList.add(SysConfigConstants.WECHAT_PUBLIC_LOGIN_TYPE);
        keyList.add(SysConfigConstants.WECHAT_ROUTINE_PHONE_VERIFICATION);
        keyList.add(SysConfigConstants.CONFIG_KEY_MOBILE_LOGIN_LOGO);
        MyRecord record = systemConfigService.getValuesByKeyList(keyList);
        FrontLoginConfigResponse response = new FrontLoginConfigResponse();
        response.setLogo(record.getStr(SysConfigConstants.CONFIG_KEY_MOBILE_LOGIN_LOGO));
        response.setWechatBrowserVisit(record.getStr(SysConfigConstants.WECHAT_PUBLIC_LOGIN_TYPE));
        response.setRoutinePhoneVerification(record.getStr(SysConfigConstants.WECHAT_ROUTINE_PHONE_VERIFICATION));
        response.setMobileLoginLogo(record.getStr(SysConfigConstants.CONFIG_KEY_MOBILE_LOGIN_LOGO));
        return response;
    }

    /**
     * 微信登录App授权登录
     */
    @Override
    public LoginResponse wechatAppLogin(RegisterAppWxRequest request) {
        //检测是否存在
        UserToken userToken = null;

        if (request.getType().equals(UserConstants.REGISTER_TYPE_IOS_WX)) {
            userToken = userTokenService.getByOpenidAndType(request.getOpenId(),  UserConstants.USER_TOKEN_TYPE_IOS_WX);
        }
        if (request.getType().equals(UserConstants.REGISTER_TYPE_ANDROID_WX)) {
            userToken = userTokenService.getByOpenidAndType(request.getOpenId(),  UserConstants.USER_TOKEN_TYPE_ANDROID_WX);
        }
        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
            User user = userService.getById(userToken.getUid());
            if (ObjectUtil.isNull(user) && user.getIsLogoff()) {
                throw new CrmebException("当前账户异常，请联系管理员！");
            }
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }
            // 记录最后一次登录时间
            user.setLastLoginTime(CrmebDateUtil.nowDateTime());
            Boolean execute = transactionTemplate.execute(e -> {
                userService.updateById(user);
                return Boolean.TRUE;
            });
            if (!execute) {
                logger.error(StrUtil.format("APP微信登录记录最后一次登录时间失败，uid={}", user.getId()));
            }
            return getLoginResponse(user);
        }
        // 没有用户，走创建用户流程
        // 从微信获取用户信息，存入Redis中，将key返回给前端，前端在下一步绑定手机号的时候下发
        RegisterThirdUserRequest registerThirdUserRequest = new RegisterThirdUserRequest();
        registerThirdUserRequest.setSpreadPid(0);
        registerThirdUserRequest.setType(request.getType());
        registerThirdUserRequest.setOpenId(request.getOpenId());
        String key = SecureUtil.md5(request.getOpenId());
        redisUtil.set(key, JSONObject.toJSONString(registerThirdUserRequest), (long) (60 * 2), TimeUnit.MINUTES);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setType(LoginConstants.LOGIN_STATUS_REGISTER);
        loginResponse.setKey(key);
        return loginResponse;
    }

    /**
     * ios登录
     */
    @Override
    public LoginResponse ioslogin(IosLoginRequest loginRequest) {
        // 检测是否存在
        UserToken userToken = userTokenService.getByOpenidAndType(loginRequest.getOpenId(), UserConstants.USER_TOKEN_TYPE_IOS);
        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
            User user = userService.getById(userToken.getUid());
            if (ObjectUtil.isNull(user) && user.getIsLogoff()) {
                throw new CrmebException("当前账户异常，请联系管理员！");
            }
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }
            // 记录最后一次登录时间
            user.setLastLoginTime(CrmebDateUtil.nowDateTime());
            Boolean execute = transactionTemplate.execute(e -> {
                userService.updateById(user);
                return Boolean.TRUE;
            });
            if (!execute) {
                logger.error(StrUtil.format("App记录用户最后一次登陆时间失败，uid={}", user.getId()));
            }
            return getLoginResponse(user);
        }
        // 没有用户Ios直接创建新用户
        User user = new User();
        String randomString = RandomUtil.randomString(11);
        user.setPhone("");
        user.setAccount(randomString);
        user.setSpreadUid(0);
        user.setPwd("123");
        user.setRegisterType(UserConstants.REGISTER_TYPE_IOS);
        user.setNickname(CommonUtil.createNickName(randomString));
        user.setAvatar(systemConfigService.getValueByKey(SysConfigConstants.USER_DEFAULT_AVATAR_CONFIG_KEY));
        user.setSex(0);
        user.setAddress("");
        user.setIsBindingIos(true);
        user.setLastLoginTime(CrmebDateUtil.nowDateTime());
        user.setLevel(1);
        Boolean execute = transactionTemplate.execute(e -> {
            userService.save(user);
            userTokenService.bind(loginRequest.getOpenId(), UserConstants.USER_TOKEN_TYPE_IOS, user.getId());
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("App用户注册生成失败，nickName = " + user.getNickname());
        }
        return getLoginResponse_V1_3(user, true);
    }

    /**
     * 校验token是否有效
     * @return true 有效， false 无效
     */
    @Override
    public Boolean tokenIsExist() {
        Integer userId = userService.getUserId();
        return userId > 0;
    }



    private LoginResponse commonLogin(User user, Integer spreadPid) {
        if (!user.getStatus()) {
            throw new CrmebException("当前账户已禁用，请联系管理员！");
        }
        if (user.getSpreadUid().equals(0) && spreadPid > 0) {
            // 绑定推广关系
            bindSpread(user, spreadPid);
        }
        // 记录最后一次登录时间
        user.setLastLoginTime(CrmebDateUtil.nowDateTime());
        boolean b = userService.updateById(user);
        if (!b) {
            logger.error("用户登录时，记录最后一次登录时间出错,uid = " + user.getId());
        }
        return getLoginResponse(user);
    }

    private LoginResponse getLoginResponse(User user) {
        //生成token
        LoginResponse loginResponse = new LoginResponse();
        LoginFrontUserVo loginUserVo = createToken(new LoginFrontUserVo(user));
        loginResponse.setToken(loginUserVo.getToken());
        loginResponse.setId(user.getId());
        loginResponse.setNikeName(user.getNickname());
        loginResponse.setPhone(CrmebUtil.maskMobile(user.getPhone()));
        loginResponse.setType(LoginConstants.LOGIN_STATUS_LOGIN);
        loginResponse.setAvatar(user.getAvatar());
        loginResponse.setMerchantEmployeeList(loginUserVo.getMerchantEmployeeList());
        return loginResponse;
    }


    private LoginResponse getLoginResponse_V1_3(User user, Boolean isNew) {
        //生成token
        LoginResponse loginResponse = new LoginResponse();
        LoginFrontUserVo loginUserVo = createToken(new LoginFrontUserVo(user));
        loginResponse.setToken(loginUserVo.getToken());
        loginResponse.setId(user.getId());
        loginResponse.setNikeName(user.getNickname());
        loginResponse.setPhone(CrmebUtil.maskMobile(user.getPhone()));
        loginResponse.setType(LoginConstants.LOGIN_STATUS_LOGIN);
        loginResponse.setAvatar(user.getAvatar());
        loginResponse.setMerchantEmployeeList(loginUserVo.getMerchantEmployeeList());
        if (isNew) {
            loginResponse.setIsNew(true);
            List<Coupon> couponList = couponService.sendNewPeopleGift(user.getId());
            if (CollUtil.isNotEmpty(couponList)) {
                loginResponse.setNewPeopleCouponList(couponList);
            }
        }
        return loginResponse;
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public LoginFrontUserVo createToken(LoginFrontUserVo loginUser) {
        String token = UUID.randomUUID().toString().replace("-", "");
        loginUser.setToken(token);
        refreshToken(loginUser);

        redisUtil.set(loginUser.getToken(), loginUser.getUser().getId(), Constants.TOKEN_EXPRESS_MINUTES, TimeUnit.MINUTES);
        return loginUser;
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginFrontUser 登录信息
     */
    private void refreshToken(LoginFrontUserVo loginFrontUser) {
        loginFrontUser.setLoginTime(System.currentTimeMillis());
        loginFrontUser.setExpireTime(loginFrontUser.getLoginTime() + FrontTokenComponent.expireTime * FrontTokenComponent.MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = "";
        // 根据当前用户检测是否店铺管理员
        List<MerchantEmployee> shopMangerByUserIds = merchantEmployeeService.getShopMangerByUserId(loginFrontUser.getUser().getId());
        // 如果是普通用户
        if (shopMangerByUserIds.isEmpty()) {
            userKey = getNormalUserTokenKey(loginFrontUser.getToken());
            loginFrontUser.setToken(RedisConstants.TOKEN_USER_NORMAL_REDIS + loginFrontUser.getToken());
            loginFrontUser.setMerchantEmployeeList(null);
        }
        // 如果是店铺管理员
        if (!shopMangerByUserIds.isEmpty()) {
            userKey = getShopManagerTokenKey(loginFrontUser.getToken());
            loginFrontUser.setToken(RedisConstants.TOKEN_USER_SHOP_MANAGER_REDIS + loginFrontUser.getToken());
            List<FrontMerchantEmployeeResponse> currentMerchantList = new ArrayList<>();
            shopMangerByUserIds.stream().map(employee -> {
                Merchant merchant = merchantService.getByIdException(employee.getMerId());
                FrontMerchantEmployeeResponse frEmployee = new FrontMerchantEmployeeResponse();
                BeanUtils.copyProperties(employee, frEmployee);
                EmployeeMerchantResponse employeeMerchant = new EmployeeMerchantResponse();
                BeanUtils.copyProperties(merchant, employeeMerchant);
                frEmployee.setCurrentMerchant(employeeMerchant);
                currentMerchantList.add(frEmployee);
                return employee;
            }).collect(Collectors.toList());

            loginFrontUser.setMerchantEmployeeList(currentMerchantList);

        }
        // 缓存用户信息
        redisUtil.set(userKey, loginFrontUser, (long) FrontTokenComponent.expireTime, TimeUnit.MINUTES);
    }

    private String getNormalUserTokenKey(String uuid) {
        return FrontTokenComponent.TOKEN_USER_NORMAL_REDIS + uuid;
    }

    private String getShopManagerTokenKey(String uuid) {
        return FrontTokenComponent.TOKEN_USER_SHOP_MANAGER_REDIS + uuid;
    }

    /**
     * 第三方登录
     * @param ciphertext
     * @param keyword
     * @return
     */
    @Override
    public LoginResponse thirdLogin(String ciphertext, String keyword) {
        String decrypt = null;
        try {
            decrypt = AESEncryptUtils.decrypt(secret, iv, ciphertext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JSONObject jsonObject = (JSONObject) JSONObject.parse(decrypt);
        ThirdLogin thirdLogin = JSONObject.toJavaObject(jsonObject, ThirdLogin.class);
        
        // 1. 先检查机构分类是否存在，不存在则新增
        String orgCategoryName = "养殖户"; // 默认分类名称
        if (StringUtils.isNotEmpty(thirdLogin.getOrgCode())) { // 这里是机构分类编码
            OrgCategory orgCategory = orgCategoryService.getByCode(thirdLogin.getOrgCategory());
            if (ObjectUtil.isNull(orgCategory)) {
                // 新增机构分类
                OrgCategory orgCategoryNew = new OrgCategory();
                orgCategoryNew.setCode(thirdLogin.getOrgCode());
                orgCategoryNew.setName(orgCategoryName);
                orgCategoryNew.setLevel(1);
                orgCategoryNew.setSortOrder(0);
                orgCategoryNew.setParentCode("0");
                orgCategoryService.save(orgCategoryNew);
                log.info("新增机构分类，分类代码: {}, 分类名称: {}", thirdLogin.getOrgCategory(), orgCategoryName);
            } else {
                orgCategoryName = orgCategory.getName();
            }
        }
        
        // 2. 查询或创建养殖场机构信息
        FarmInstitution farmInstitution = null;
        Organization organization = null;
        
        log.info("第三方登录数据解析 - 手机号: {}, farmCode: {}, orgCode(机构分类编码): {}, orgName: {}, roles: {}", 
                thirdLogin.getMobile(), thirdLogin.getOrgCode(), thirdLogin.getOrgCode(), thirdLogin.getOrgName(), thirdLogin.getRoles());
        
        // 根据farmCode查询养殖场信息
        if (StringUtils.isNotEmpty(thirdLogin.getOrgCode())) {
            farmInstitution = farmInstitutionService.getFarmInstitutionByFarmCode(thirdLogin.getOrgCode());
            log.info("根据farmCode查询养殖场信息 - farmCode: {}, 查询结果: {}", thirdLogin.getOrgCode(), 
                    farmInstitution != null ? "找到养殖场ID=" + farmInstitution.getId() : "未找到");
                    
            if (ObjectUtil.isNull(farmInstitution)) {
                // 养殖机构不存在，同步创建
                try {
                    if (StringUtils.isNotEmpty(thirdLogin.getMobile())) {
                        log.info("开始同步创建养殖机构 - 手机号: {}, farmCode: {}", thirdLogin.getMobile(), thirdLogin.getOrgCode());
                        Boolean syncResult = farmInstitutionService.syncMemberFarmInstitution(null, thirdLogin.getMobile(), thirdLogin.getOrgCode());
                        if (!syncResult) {
                            log.error("同步养殖机构信息失败，手机号: {}, 机构代码: {}", thirdLogin.getMobile(), thirdLogin.getOrgCode());
                        } else {
                            log.info("同步养殖机构信息成功，手机号: {}, 机构代码: {}", thirdLogin.getMobile(), thirdLogin.getOrgCode());
                            // 重新查询同步后的机构信息
                            farmInstitution = farmInstitutionService.getFarmInstitutionByFarmCode(thirdLogin.getOrgCode());
                            log.info("重新查询同步后的养殖场信息 - farmCode: {}, 查询结果: {}", thirdLogin.getOrgCode(), 
                                    farmInstitution != null ? "找到养殖场ID=" + farmInstitution.getId() : "仍未找到");
                        }
                    }
                } catch (Exception e) {
                    log.error("同步养殖机构信息异常", e);
                    // 不影响用户登录，捕获异常继续执行
                }
            } else {
                // 养殖机构存在，更新机构信息
                if (StringUtils.isNotEmpty(thirdLogin.getOrgName())) {
                    farmInstitution.setFarmName(thirdLogin.getOrgName());
                    farmInstitutionService.updateById(farmInstitution);
                    log.info("更新养殖机构信息，机构代码: {}, 机构名称: {}", thirdLogin.getOrgCode(), thirdLogin.getOrgName());
                }
            }
        }
        
        // 根据养殖场中的机构编码查询机构信息
        if (ObjectUtil.isNotNull(farmInstitution) && StringUtils.isNotEmpty(farmInstitution.getOrganizationCode())) {
            log.info("开始查询机构信息 - 从养殖场获取的机构编码: {}", farmInstitution.getOrganizationCode());
            
            // 查询机构信息
            LambdaQueryWrapper<Organization> orgWrapper = new LambdaQueryWrapper<>();
            orgWrapper.eq(Organization::getOrgCode, farmInstitution.getOrganizationCode());
            orgWrapper.eq(Organization::getDeleteFlag, 0);
            organization = organizationService.getOne(orgWrapper);
            
            log.info("机构信息查询结果 - 机构编码: {}, 查询结果: {}", farmInstitution.getOrganizationCode(), 
                    organization != null ? "找到机构ID=" + organization.getId() + ", 名称=" + organization.getOrgName() : "未找到");
            
            if (ObjectUtil.isNotNull(organization)) {
                // 更新养殖场的机构信息（确保数据一致性）
                if (ObjectUtil.isNull(farmInstitution.getOrganizationId()) || 
                    !farmInstitution.getOrganizationId().equals(organization.getId())) {
                    log.info("更新养殖场机构关联 - 养殖场ID: {}, 当前机构ID: {}, 新机构ID: {}", 
                            farmInstitution.getId(), farmInstitution.getOrganizationId(), organization.getId());
                            
                    farmInstitution.setOrganizationId(organization.getId());
                    farmInstitution.setOrganizationName(organization.getOrgName());
                    farmInstitution.setOrganizationCode(organization.getOrgCode());
                    farmInstitutionService.updateById(farmInstitution);
                    log.info("更新养殖场机构信息成功，养殖场ID: {}, 机构ID: {}", farmInstitution.getId(), organization.getId());
                } else {
                    log.info("养殖场机构信息已是最新，无需更新 - 养殖场ID: {}, 机构ID: {}", farmInstitution.getId(), organization.getId());
                }
            } else {
                log.warn("根据养殖场机构编码未找到机构信息 - 养殖场ID: {}, 机构编码: {}", 
                        farmInstitution.getId(), farmInstitution.getOrganizationCode());
            }
        } else if (ObjectUtil.isNotNull(farmInstitution)) {
            log.warn("养殖场信息中没有机构编码 - 养殖场ID: {}, farmCode: {}", farmInstitution.getId(), thirdLogin.getOrgCode());
        }
        
        // 3. 处理用户信息：判断用户是否存在，不存在则新增，存在则更新
        User user = userService.getByPhone(thirdLogin.getMobile());
        boolean isNewUser = false;
        
        if (ObjectUtil.isNull(user)) {
            // 用户不存在，创建新用户
            user = createNewThirdUser(thirdLogin, orgCategoryName, farmInstitution, organization);
            userService.save(user);
            isNewUser = true;
            log.info("创建新用户，手机号: {}, 用户ID: {}", thirdLogin.getMobile(), user.getId());
            
            // 为所有第三方登录用户创建相关账号信息
            createThirdUserRelatedInfo(user, thirdLogin, orgCategoryName);
        } else {
            // 用户存在，更新用户信息
            updateExistingUserWithFarmAndOrgInfo(user, thirdLogin, farmInstitution, organization);
            log.info("更新用户信息，用户ID: {}, 机构代码: {}", user.getId(), thirdLogin.getOrgCode());
            
            // 为已存在用户也创建相关账号信息（如果还没有的话）
            createThirdUserRelatedInfo(user, thirdLogin, orgCategoryName);
        }
        
        return getLoginResponse_V1_3(user, isNewUser);
    }
    
    /**
     * 创建新的第三方登录用户
     */
    private User createNewThirdUser(ThirdLogin thirdLogin, String orgCategoryName, FarmInstitution farmInstitution, Organization organization) {
        User newUser = new User();
        OrgCategory orgCategory = orgCategoryService.getByCode(thirdLogin.getOrgCategory());
        if(StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().equals("合作社")){
            newUser.setFarmType(2);// 2表示合作社
        }else if (StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().contains("养殖")) {
            newUser.setFarmType(1);// 1表示养殖户
        } else if (StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().contains("供应商")) {
            newUser.setFarmType(4);// 4表示供应商
        }else if (StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().contains("平台")) {
            newUser.setFarmType(5);//  5表示平台
        }else if (StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().contains("经纪人")) {
            newUser.setFarmType(3);//  3表示经纪人
        }else {
            newUser.setFarmType(0);// 0表示游客
        }

        // 基本信息
        if (StringUtils.isNotEmpty(thirdLogin.getMobile())) {
            newUser.setPhone(thirdLogin.getMobile());
            newUser.setAccount(thirdLogin.getMobile());
        }
        if (StringUtils.isNotEmpty(thirdLogin.getRealName())) {
            newUser.setRealName(thirdLogin.getRealName());
            newUser.setNickname(thirdLogin.getRealName());
        } else {
            newUser.setNickname(CommonUtil.createNickName(thirdLogin.getMobile()));
        }
        if (StringUtils.isNotEmpty(thirdLogin.getGender())) {
            newUser.setSex(Integer.parseInt(thirdLogin.getGender()));
        } else {
            newUser.setSex(0);
        }
        if (Objects.nonNull(thirdLogin.getAvatar())) {
            newUser.setAvatar(thirdLogin.getAvatar());
        } else {
            newUser.setAvatar(systemConfigService.getValueByKey(SysConfigConstants.USER_DEFAULT_AVATAR_CONFIG_KEY));
        }
        newUser.setFarmName(thirdLogin.getOrgName());
        newUser.setFarmCode(thirdLogin.getOrgCode()); // 使用farmCode而不是orgCode
        
        // 设置养殖场机构ID
        if (Objects.nonNull(farmInstitution)) {
            newUser.setFarmId(farmInstitution.getId());
            log.info("设置新用户养殖场信息 - 养殖场ID: {}, 名称: {}, 代码: {}", 
                    farmInstitution.getId(), farmInstitution.getFarmName(), farmInstitution.getFarmCode());
        } else {
            log.warn("新用户创建时，养殖场信息为空 - 手机号: {}", thirdLogin.getMobile());
        }
        
        // 设置机构信息
        if (Objects.nonNull(organization)) {
            newUser.setOrganizationId(organization.getId());
            newUser.setOrganizationName(organization.getOrgName());
            newUser.setOrganizationCode(organization.getOrgCode());
            log.info("设置新用户机构信息 - 机构ID: {}, 名称: {}, 代码: {}", 
                    organization.getId(), organization.getOrgName(), organization.getOrgCode());
        } else {
            log.warn("新用户创建时，机构信息为空 - 手机号: {}", thirdLogin.getMobile());
        }
        
        // 用户状态和类型
        newUser.setStatus(true);
        newUser.setUserType(UserTypeEnum.USER_TYPE_ADMIN.getCode()); // 默认用户类型是管理员
        newUser.setLastLoginTime(new Date());
        newUser.setSpreadUid(0);
        newUser.setPwd(CommonUtil.createPwd(thirdLogin.getMobile()));
        newUser.setAddress("");
        newUser.setLevel(1);
        newUser.setRegisterType(UserConstants.REGISTER_TYPE_ROUTINE.toLowerCase());
        return newUser;
    }
    
    /**
     * 更新现有用户的信息，包括养殖场和机构信息
     * @param user 用户信息
     * @param thirdLogin 第三方登录信息
     * @param farmInstitution 养殖场信息
     * @param organization 机构信息
     */
    private void updateExistingUserWithFarmAndOrgInfo(User user, ThirdLogin thirdLogin, FarmInstitution farmInstitution, Organization organization) {
        boolean needUpdate = false;
        
        // 更新基本信息
        if (StringUtils.isNotEmpty(thirdLogin.getRealName()) && !thirdLogin.getRealName().equals(user.getRealName())) {
            user.setRealName(thirdLogin.getRealName());
            user.setNickname(thirdLogin.getRealName());
            needUpdate = true;
        }
        if(StringUtils.isNotEmpty(user.getOrganizationId())){
            Organization userOrganization = organizationService.getById(user.getOrganizationId());
            if(userOrganization != null){
                OrgCategory orgCategory = orgCategoryService.getById(userOrganization.getCategoryId());
                if(StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().equals("合作社")){
                    user.setFarmType(2);// 2表示合作社
                }else if (StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().contains("养殖")) {
                    user.setFarmType(1);// 1表示养殖户
                } else if (StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().contains("供应商")) {
                    user.setFarmType(4);// 4表示供应商
                }else if (StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().contains("平台")) {
                    user.setFarmType(5);//  5表示平台
                }else if (StringUtils.isNotEmpty(orgCategory.getTypeName())&&orgCategory.getTypeName().contains("经纪人")) {
                    user.setFarmType(3);//  3表示经纪人
                }else {
                    user.setFarmType(0);// 0表示游客
                }
            }
        }
        
        if (StringUtils.isNotEmpty(thirdLogin.getGender())) {
            Integer gender = Integer.parseInt(thirdLogin.getGender());
            if (!gender.equals(user.getSex())) {
                user.setSex(gender);
                needUpdate = true;
            }
        }
        
        if (StringUtils.isNotEmpty(thirdLogin.getAvatar()) && !thirdLogin.getAvatar().equals(user.getAvatar())) {
            user.setAvatar(thirdLogin.getAvatar());
            needUpdate = true;
        }
        
        // 更新养殖场信息 - 强制更新为最新的养殖场信息
        if (ObjectUtil.isNotNull(farmInstitution)) {
            // 如果用户当前没有养殖场信息，或者养殖场信息不一致，则更新
            if (ObjectUtil.isNull(user.getFarmId()) || !user.getFarmId().equals(farmInstitution.getId())) {
                user.setFarmId(farmInstitution.getId());
                user.setFarmName(farmInstitution.getFarmName());
                user.setFarmCode(farmInstitution.getFarmCode());
                needUpdate = true;
                log.info("更新用户养殖场信息，用户ID: {}, 当前养殖场ID: {}, 新养殖场ID: {}", 
                        user.getId(), user.getFarmId(), farmInstitution.getId());
            } else {
                log.info("用户养殖场信息已是最新，无需更新 - 用户ID: {}, 养殖场ID: {}", user.getId(), farmInstitution.getId());
            }
        } else if (StringUtils.isNotEmpty(thirdLogin.getOrgCode())) {
            // 第三方传来了养殖场代码但系统中找不到对应的养殖场
            log.warn("第三方传入养殖场代码但系统中未找到对应养殖场 - 用户ID: {}, farmCode: {}", user.getId(), thirdLogin.getOrgCode());
            // 仍然更新用户的养殖场基本信息
            if (StringUtils.isNotEmpty(thirdLogin.getOrgName()) && !thirdLogin.getOrgName().equals(user.getFarmName())) {
                user.setFarmName(thirdLogin.getOrgName());
                user.setFarmCode(thirdLogin.getOrgCode());
                needUpdate = true;
                log.info("更新用户养殖场基本信息，用户ID: {}, 养殖场名称: {}, 代码: {}", 
                        user.getId(), thirdLogin.getOrgName(), thirdLogin.getOrgCode());
            }
        }
        
        // 更新机构信息
        if (ObjectUtil.isNotNull(organization)) {
            // 如果用户当前没有机构信息，或者机构信息不一致，则更新
            if (ObjectUtil.isNull(user.getOrganizationId()) || !user.getOrganizationId().equals(organization.getId())) {
                user.setOrganizationId(organization.getId());
                user.setOrganizationName(organization.getOrgName());
                user.setOrganizationCode(organization.getOrgCode());
                needUpdate = true;
                log.info("更新用户机构信息，用户ID: {}, 当前机构ID: {}, 新机构ID: {}", 
                        user.getId(), user.getOrganizationId(), organization.getId());
            } else {
                log.info("用户机构信息已是最新，无需更新 - 用户ID: {}, 机构ID: {}", user.getId(), organization.getId());
            }
        } else if (StringUtils.isNotEmpty(thirdLogin.getOrgCode())) {
            // 第三方传来了机构分类编码，但没有找到对应的养殖场或机构信息
            log.warn("第三方传入机构分类编码，但未找到对应的养殖场或机构信息 - 用户ID: {}, 机构分类编码: {}", user.getId(), thirdLogin.getOrgCode());
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(new Date());
        needUpdate = true;
        
        // 如果用户类型不是管理员，则更新为管理员
        if (!UserTypeEnum.USER_TYPE_ADMIN.getCode().equals(user.getUserType())) {
            user.setUserType(UserTypeEnum.USER_TYPE_ADMIN.getCode());
            needUpdate = true;
        }
        
        if (needUpdate) {
            userService.updateById(user);
            log.info("更新用户信息完成，用户ID: {}", user.getId());
        }
    }

    /**
     * 判断是否为供应商
     * @param orgCategoryName 机构分类名称
     * @return 是否为供应商
     */
    private boolean isSupplier(String orgCategoryName) {
        // 根据机构分类名称判断是否为供应商
        return StringUtils.isNotEmpty(orgCategoryName) && 
               (orgCategoryName.equals("兽药生产企业") || 
                orgCategoryName.equals("兽药经营企业") || 
                orgCategoryName.equals("饲料生产企业"));
    }
    
    /**
     * 为第三方登录用户创建相关账号信息
     * @param user 用户信息
     * @param thirdLogin 第三方登录信息
     * @param orgCategoryName 机构分类名称
     */
    private void createThirdUserRelatedInfo(User user, ThirdLogin thirdLogin, String orgCategoryName) {
        try {
            // 1. 为所有用户创建系统管理员账号
            createSystemAdminAccount(user, thirdLogin);
            
            // 2. 如果是供应商，额外创建商户相关信息
            if (isSupplier(orgCategoryName)) {
                createMerchantRelatedInfo(user, thirdLogin);
            }
            
            log.info("为第三方登录用户创建相关账号信息成功，用户ID: {}, 机构分类: {}", user.getId(), orgCategoryName);
        } catch (Exception e) {
            log.error("为第三方登录用户创建相关账号信息失败，用户ID: {}", user.getId(), e);
            // 不影响登录流程，只记录错误
        }
    }
    
    /**
     * 创建系统管理员账号
     * @param user 用户信息
     * @param thirdLogin 第三方登录信息
     */
    private void createSystemAdminAccount(User user, ThirdLogin thirdLogin) {
        // 检查是否已经存在管理员账号
        SystemAdmin existingAdmin = null;
        if (systemAdminService.checkAccount(user.getPhone())) {
            // 已存在管理员账号，检查是否需要更新角色
            LambdaQueryWrapper<SystemAdmin> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SystemAdmin::getAccount, user.getPhone());
            wrapper.eq(SystemAdmin::getIsDel, false);
            existingAdmin = systemAdminService.getOne(wrapper);
            
            if (existingAdmin != null) {
                log.info("系统管理员账号已存在，检查角色更新 - 手机号: {}, 管理员ID: {}", user.getPhone(), existingAdmin.getId());
                
                // 根据第三方角色更新管理员角色
                String newRoles = mapThirdPartyRolesToSystemRoles(thirdLogin.getRoles());
                if (StringUtils.isNotEmpty(newRoles) && !newRoles.equals(existingAdmin.getRoles())) {
                    existingAdmin.setRoles(newRoles);
                    existingAdmin.setUpdateTime(new Date());
                    systemAdminService.updateById(existingAdmin);
                    log.info("更新系统管理员角色，账号: {}, 原角色: {}, 新角色: {}", 
                            existingAdmin.getAccount(), existingAdmin.getRoles(), newRoles);
                }
                return;
            }
        }
        
        // 创建系统管理员账号
        SystemAdmin admin = new SystemAdmin();
        admin.setAccount(user.getPhone());
        admin.setPwd(CrmebUtil.encryptPassword("000000", user.getPhone())); // 默认密码
        admin.setRealName(user.getRealName());
        admin.setPhone(user.getPhone());
        admin.setStatus(true);
        admin.setType(4); // 系统管理员类型
        admin.setMerId(0); // 平台管理员
        
        // 根据第三方角色映射系统角色
        String systemRoles = mapThirdPartyRolesToSystemRoles(thirdLogin.getRoles());
        admin.setRoles(systemRoles);
        
        admin.setLevel(1);
        admin.setLoginCount(0);
        admin.setIsSms(false);
        admin.setIsDel(false);
        admin.setCreateTime(new Date());
        admin.setUpdateTime(new Date());
        
        boolean saveResult = systemAdminService.save(admin);
        if (saveResult) {
            log.info("创建系统管理员账号成功，账号: {}, 用户ID: {}, 角色: {}", admin.getAccount(), user.getId(), admin.getRoles());
        } else {
            log.error("创建系统管理员账号失败，账号: {}, 用户ID: {}", admin.getAccount(), user.getId());
        }
    }
    
    /**
     * 映射第三方角色到系统角色
     * @param thirdPartyRoles 第三方角色代码，多个用逗号分隔
     * @return 系统角色ID字符串，多个用逗号分隔
     */
    private String mapThirdPartyRolesToSystemRoles(String thirdPartyRoles) {
        if (StringUtils.isEmpty(thirdPartyRoles)) {
            // 默认返回基础角色
            return "4"; // 系统管理员角色
        }
        
        // 第三方英文角色代码到中文名称的映射表
        Map<String, String> roleNameMapping = new HashMap<>();
        roleNameMapping.put("ADMIN", "管理员");
        roleNameMapping.put("AHB", "畜牧局管理员");
        roleNameMapping.put("CLEANER", "清洁员");
        roleNameMapping.put("FINANCE", "财务管理员");
        roleNameMapping.put("STAFF", "饲养员");
        roleNameMapping.put("TECHNICIAN", "技术员");
        roleNameMapping.put("VET", "兽医");
        roleNameMapping.put("WAREHOUSE", "库房管理员");
        
        Set<String> systemRoleIds = new HashSet<>();
        
        // 解析第三方角色（可能是逗号分隔的多个角色）
        String[] roles = thirdPartyRoles.split(",");
        for (String role : roles) {
            String trimmedRole = role.trim().toUpperCase();
            String chineseRoleName = roleNameMapping.get(trimmedRole);
            
            if (chineseRoleName != null) {
                // 根据中文角色名称查询或创建角色
                Integer roleId = findOrCreateSystemRole(chineseRoleName);
                if (roleId != null) {
                    systemRoleIds.add(roleId.toString());
                    log.info("角色映射成功：{} -> {} -> roleId: {}", trimmedRole, chineseRoleName, roleId);
                }
            } else {
                log.warn("未知的第三方角色代码：{}", trimmedRole);
                // 未知角色默认给予基础权限
                systemRoleIds.add("3");
            }
        }
        
        // 如果没有映射到任何角色，给予默认角色
        if (systemRoleIds.isEmpty()) {
            systemRoleIds.add("3");
        }
        
        String result = String.join(",", systemRoleIds);
        log.info("角色映射结果 - 第三方角色: {}, 系统角色ID: {}", thirdPartyRoles, result);
        return result;
    }
    
    /**
     * 根据角色名称查找或创建系统角色
     * @param roleName 角色名称
     * @return 角色ID
     */
    private Integer findOrCreateSystemRole(String roleName) {
        try {
            // 先查询是否存在该角色
            LambdaQueryWrapper<com.zbkj.common.model.admin.SystemRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(com.zbkj.common.model.admin.SystemRole::getRoleName, roleName);
            wrapper.eq(com.zbkj.common.model.admin.SystemRole::getMerId, 0); // 平台角色
            wrapper.eq(com.zbkj.common.model.admin.SystemRole::getStatus, true); // 有效状态
            
            com.zbkj.common.model.admin.SystemRole existingRole = systemRoleService.getOne(wrapper);
            
            if (existingRole != null) {
                log.info("找到现有角色：{}, ID: {}", roleName, existingRole.getId());
                return existingRole.getId();
            }
            
            // 角色不存在，创建新角色
            return createSystemRole(roleName);
            
        } catch (Exception e) {
            log.error("查找或创建系统角色失败，角色名称: {}", roleName, e);
            return null;
        }
    }
    
    /**
     * 创建系统角色
     * @param roleName 角色名称
     * @return 角色ID
     */
    private Integer createSystemRole(String roleName) {
        try {
            com.zbkj.common.model.admin.SystemRole newRole = new com.zbkj.common.model.admin.SystemRole();
            newRole.setRoleName(roleName);
            newRole.setMerId(0); // 平台角色
            newRole.setType(3); // 系统管理员类型
            newRole.setStatus(true);
            newRole.setLevel(1);
            newRole.setCreateTime(new Date());
            newRole.setUpdateTime(new Date());
            
            // 设置默认权限规则（可以根据需要调整）
            newRole.setRules(getDefaultRulesForRole(roleName));
            
            boolean saveResult = systemRoleService.save(newRole);
            if (saveResult) {
                log.info("创建新角色成功：{}, ID: {}", roleName, newRole.getId());
                return newRole.getId();
            } else {
                log.error("创建新角色失败：{}", roleName);
                return null;
            }
        } catch (Exception e) {
            log.error("创建系统角色异常，角色名称: {}", roleName, e);
            return null;
        }
    }
    
    /**
     * 根据角色名称获取默认权限规则
     * @param roleName 角色名称
     * @return 权限规则字符串
     */
    private String getDefaultRulesForRole(String roleName) {
        // 根据角色类型设置不同的默认权限
        switch (roleName) {
            case "管理员":
            case "畜牧局管理员":
                // 管理员权限较高，给予大部分权限
                return "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15";
            case "供应商角色":
                // 供应商权限，主要是商户管理相关权限
                return "1,2,3,50,51,52,53,54";
            case "财务管理员":
                // 财务相关权限
                return "1,2,3,20,21,22,23,24";
            case "技术员":
            case "兽医":
                // 技术相关权限
                return "1,2,3,30,31,32,33";
            case "饲养员":
            case "清洁员":
            case "库房管理员":
                // 基础操作权限
                return "1,2,3,40,41,42";
            default:
                // 默认基础权限
                return "1,2,3";
        }
    }

    /**
     * 创建商户相关信息（仅供应商）
     * @param user 用户信息
     * @param thirdLogin 第三方登录信息
     */
    private void createMerchantRelatedInfo(User user, ThirdLogin thirdLogin) {
        try {
            // 1. 为供应商创建"供应商角色"
            Integer supplierRoleId = findOrCreateSystemRole("供应商角色");
            if (supplierRoleId != null) {
                // 更新用户的管理员账号，添加供应商角色
                updateAdminRoleWithSupplierRole(user.getPhone(), supplierRoleId);
            }
            
            // 2. 创建商户申请记录
            Integer merchantApplyId = createMerchantApply(user, thirdLogin);
            
            // 3. 自动审核通过商户申请
            if (merchantApplyId != null) {
                autoAuditMerchantApply(merchantApplyId);
            }
            
            log.info("创建商户相关信息成功，用户ID: {}, 商户申请ID: {}, 供应商角色ID: {}", user.getId(), merchantApplyId, supplierRoleId);
        } catch (Exception e) {
            log.error("创建商户相关信息失败，用户ID: {}", user.getId(), e);
            // 不影响登录流程，只记录错误
        }
    }
    
    /**
     * 为管理员账号添加供应商角色
     * @param phone 手机号
     * @param supplierRoleId 供应商角色ID
     */
    private void updateAdminRoleWithSupplierRole(String phone, Integer supplierRoleId) {
        try {
            // 查找管理员账号
            LambdaQueryWrapper<SystemAdmin> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SystemAdmin::getAccount, phone);
            wrapper.eq(SystemAdmin::getIsDel, false);
            SystemAdmin admin = systemAdminService.getOne(wrapper);
            
            if (admin != null) {
                String currentRoles = admin.getRoles();
                Set<String> roleSet = new HashSet<>();
                
                // 添加现有角色
                if (StringUtils.isNotEmpty(currentRoles)) {
                    String[] existingRoles = currentRoles.split(",");
                    for (String role : existingRoles) {
                        roleSet.add(role.trim());
                    }
                }
                
                // 添加供应商角色
                roleSet.add(supplierRoleId.toString());
                
                // 更新角色
                String newRoles = String.join(",", roleSet);
                admin.setRoles(newRoles);
                admin.setUpdateTime(new Date());
                
                boolean updateResult = systemAdminService.updateById(admin);
                if (updateResult) {
                    log.info("成功为管理员添加供应商角色，账号: {}, 原角色: {}, 新角色: {}", 
                            phone, currentRoles, newRoles);
                } else {
                    log.error("为管理员添加供应商角色失败，账号: {}", phone);
                }
            } else {
                log.warn("未找到管理员账号，无法添加供应商角色，账号: {}", phone);
            }
        } catch (Exception e) {
            log.error("为管理员添加供应商角色异常，账号: {}", phone, e);
        }
    }

    /**
     * 创建商户申请记录
     * @param user 用户信息
     * @param thirdLogin 第三方登录信息
     * @return 商户申请ID
     */
    private Integer createMerchantApply(User user, ThirdLogin thirdLogin) {
        // 检查是否已经有商户申请记录
        com.zbkj.common.model.merchant.MerchantApply existingApply = merchantApplyService.getOne(
            new LambdaQueryWrapper<com.zbkj.common.model.merchant.MerchantApply>()
                .eq(com.zbkj.common.model.merchant.MerchantApply::getUid, user.getId())
                .orderByDesc(com.zbkj.common.model.merchant.MerchantApply::getId)
                .last("limit 1")
        );
        
        if (existingApply != null) {
            log.info("用户已有商户申请记录，用户ID: {}, 申请ID: {}", user.getId(), existingApply.getId());
            return existingApply.getId();
        }
        
        // 创建新的商户申请记录
        com.zbkj.common.model.merchant.MerchantApply merchantApply = new com.zbkj.common.model.merchant.MerchantApply();
        
        // 基本信息
        merchantApply.setUid(user.getId());
        merchantApply.setName(StringUtils.isNotEmpty(thirdLogin.getOrgName()) ? thirdLogin.getOrgName() : user.getRealName() + "商户");
        merchantApply.setRealName(user.getRealName());
        merchantApply.setPhone(user.getPhone());
        merchantApply.setEmail("");
        
        // 默认商户分类和类型（需要根据实际业务配置）
        merchantApply.setCategoryId(1); // 默认分类ID，需要根据实际情况调整
        merchantApply.setTypeId(1); // 默认类型ID，需要根据实际情况调整
        merchantApply.setHandlingFee(5); // 默认手续费5%
        
        // 设置关键字和地址
        merchantApply.setKeywords(thirdLogin.getOrgName());
        merchantApply.setAddress(StringUtils.isNotEmpty(user.getAddress()) ? user.getAddress() : "");
        
        // 默认配置
        merchantApply.setIsSelf(false); // 非自营
        merchantApply.setIsRecommend(false); // 不推荐
        merchantApply.setAuditStatus(MerchantConstants.AUDIT_STATUS_WAIT); // 待审核
        
        // 资质图片（可以后续上传）
        merchantApply.setQualificationPicture("");
        
        // 保存申请记录
        boolean saveResult = merchantApplyService.save(merchantApply);
        if (saveResult) {
            log.info("创建商户申请记录成功，用户ID: {}, 申请ID: {}", user.getId(), merchantApply.getId());
            return merchantApply.getId();
        } else {
            log.error("创建商户申请记录失败，用户ID: {}", user.getId());
            return null;
        }
    }
    
    /**
     * 自动审核通过商户申请
     * @param merchantApplyId 商户申请ID
     */
    private void autoAuditMerchantApply(Integer merchantApplyId) {
        try {
            // 获取商户申请记录
            com.zbkj.common.model.merchant.MerchantApply merchantApply = merchantApplyService.getById(merchantApplyId);
            if (merchantApply == null) {
                log.error("商户申请记录不存在，申请ID: {}", merchantApplyId);
                return;
            }
            
            // 如果已经审核过，直接返回
            if (!merchantApply.getAuditStatus().equals(MerchantConstants.AUDIT_STATUS_WAIT)) {
                log.info("商户申请已审核，申请ID: {}, 状态: {}", merchantApplyId, merchantApply.getAuditStatus());
                return;
            }
            
            // 更新申请状态为审核通过
            merchantApply.setAuditStatus(MerchantConstants.AUDIT_STATUS_SUCCESS);
            merchantApply.setAuditorId(1); // 使用系统管理员ID
            
            // 构造商户添加请求
            com.zbkj.common.request.merchant.MerchantAddRequest merchantAddRequest = 
                new com.zbkj.common.request.merchant.MerchantAddRequest();
            BeanUtils.copyProperties(merchantApply, merchantAddRequest);
            
            // 使用事务执行审核和创建商户
            Boolean success = transactionTemplate.execute(status -> {
                try {
                    // 更新申请状态
                    boolean updateResult = merchantApplyService.updateById(merchantApply);
                    if (!updateResult) {
                        log.error("更新商户申请状态失败，申请ID: {}", merchantApplyId);
                        status.setRollbackOnly();
                        return false;
                    }
                    
                    // 创建商户
                    Boolean createResult = merchantService.auditSuccess(merchantAddRequest, 1);
                    if (!createResult) {
                        log.error("创建商户失败，申请ID: {}", merchantApplyId);
                        status.setRollbackOnly();
                        return false;
                    }
                    
                    return true;
                } catch (Exception e) {
                    log.error("自动审核商户申请事务执行失败，申请ID: {}", merchantApplyId, e);
                    status.setRollbackOnly();
                    return false;
                }
            });
            
            if (success != null && success) {
                log.info("商户申请自动审核通过成功，申请ID: {}", merchantApplyId);
            } else {
                log.error("商户申请自动审核通过失败，申请ID: {}", merchantApplyId);
            }
        } catch (Exception e) {
            log.error("商户申请自动审核过程中发生异常，申请ID: {}", merchantApplyId, e);
        }
    }

}
