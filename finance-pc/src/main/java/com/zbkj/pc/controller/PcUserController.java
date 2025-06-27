package com.zbkj.pc.controller;

import com.zbkj.common.request.LoginMobileRequest;
import com.zbkj.common.request.LoginPasswordRequest;
import com.zbkj.common.request.UserUpdateRequest;
import com.zbkj.common.response.LoginResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.model.user.User;
import com.zbkj.pc.service.PcLoginService;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.SystemConfigService;
import com.zbkj.service.service.OrderService;
import com.zbkj.service.service.CouponUserService;
import com.zbkj.service.service.ProductRelationService;
import com.zbkj.service.service.UserAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * PC端用户控制器
 * @author: crmeb
 * @date: 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/pc/user")
@Api(tags = "PC端用户接口")
public class PcUserController {

    @Autowired
    private PcLoginService loginService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CouponUserService couponUserService;

    @Autowired
    private ProductRelationService productRelationService;

    @Autowired
    private UserAddressService userAddressService;

    /**
     * 用户登录 - 支持密码和验证码登录
     * @param request 登录请求参数  
     * @return CommonResult
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public CommonResult<LoginResponse> login(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String password = request.get("password");
        String code = request.get("code");
        
        LoginResponse result;
        
        if (code != null && !code.isEmpty()) {
            // 验证码登录
            LoginMobileRequest mobileRequest = new LoginMobileRequest();
            mobileRequest.setPhone(phone);
            mobileRequest.setCaptcha(code);
            result = loginService.phoneCaptchaLogin(mobileRequest);
        } else {
            // 密码登录
            LoginPasswordRequest passwordRequest = new LoginPasswordRequest();
            passwordRequest.setPhone(phone);
            passwordRequest.setPassword(password);
            result = loginService.phonePasswordLogin(passwordRequest);
        }
        
        return CommonResult.success(result);
    }
    
    /**
     * 用户退出登录
     * @return CommonResult
     */
    @ApiOperation("用户退出登录")
    @PostMapping("/logout")
    public CommonResult<String> logout(HttpServletRequest request) {
        loginService.loginOut(request);
        return CommonResult.success("退出登录成功");
    }

    /**
     * 获取用户信息
     * @return CommonResult
     */
    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public CommonResult<Map<String, Object>> info() {
        Map<String, Object> map = new HashMap<>();

        // 获取用户基本信息
        map.put("userInfo", userService.getUserInfo());

        // 获取订单统计信息
        try {
            map.put("orderStats", getOrderStatistics());
        } catch (Exception e) {
            map.put("orderStats", getDefaultOrderStats());
        }

        // 获取用户资产信息
        try {
            map.put("userAssets", getUserAssets());
        } catch (Exception e) {
            map.put("userAssets", getDefaultUserAssets());
        }

        // 获取地址数量
        try {
            map.put("addressCount", getAddressCount());
        } catch (Exception e) {
            map.put("addressCount", 0);
        }

        return CommonResult.success(map);
    }

    /**
     * 获取订单统计信息
     */
    private Map<String, Object> getOrderStatistics() {
        Map<String, Object> orderStats = new HashMap<>();
        Integer userId = userService.getUserIdException();
        
        // 获取各状态订单数量
        int waitPay = orderService.getCountByStatusAndUid(0, userId); // 待支付
        int waitDeliver = orderService.getCountByStatusAndUid(1, userId); // 待发货
        int partDeliver = orderService.getCountByStatusAndUid(2, userId); // 部分发货
        int waitVerify = orderService.getCountByStatusAndUid(3, userId); // 待核销
        int waitReceive = orderService.getCountByStatusAndUid(4, userId); // 待收货
        int received = orderService.getCountByStatusAndUid(5, userId); // 已收货
        int completed = orderService.getCountByStatusAndUid(6, userId); // 已完成
        int cancelled = orderService.getCountByStatusAndUid(9, userId); // 已取消
        
        // 计算总数（各状态之和）
        int totalOrders = waitPay + waitDeliver + partDeliver + waitVerify + waitReceive + received + completed + cancelled;
        
        orderStats.put("total", totalOrders);
        orderStats.put("waitPay", waitPay);
        orderStats.put("waitDeliver", waitDeliver);
        orderStats.put("partDeliver", partDeliver);
        orderStats.put("waitVerify", waitVerify);
        orderStats.put("waitReceive", waitReceive);
        orderStats.put("received", received);
        orderStats.put("completed", completed);
        orderStats.put("cancelled", cancelled);
        
        return orderStats;
    }

    /**
     * 获取用户资产信息
     */
    private Map<String, Object> getUserAssets() {
        Map<String, Object> userAssets = new HashMap<>();
        Integer userId = userService.getUserIdException();
        
        // 获取用户信息（包含余额）
        User user = userService.getById(userId);
        if (user != null) {
            // 账户余额
            userAssets.put("balance", user.getNowMoney() != null ? user.getNowMoney().toString() : "0.00");
            
            // 积分
            userAssets.put("points", user.getIntegral() != null ? user.getIntegral() : 0);
            
            // 经验值
            userAssets.put("experience", user.getExperience() != null ? user.getExperience() : 0);
        } else {
            userAssets.put("balance", "0.00");
            userAssets.put("points", 0);
            userAssets.put("experience", 0);
        }
        
        // 获取优惠券数量
        int couponCount = couponUserService.getUseCount(userId);
        userAssets.put("coupons", couponCount);
        
        // 获取收藏数量
        int collectCount = productRelationService.getCollectCountByUid(userId);
        userAssets.put("collections", collectCount);
        
        return userAssets;
    }

    /**
     * 获取地址数量
     */
    private int getAddressCount() {
        Integer userId = userService.getUserIdException();
        // 使用LambdaQueryWrapper来统计地址数量
        try {
            return userAddressService.getCountByUid(userId);
        } catch (Exception e) {
            // 如果方法不存在，返回0
            return 0;
        }
    }

    /**
     * 默认订单统计（出错时使用）
     */
    private Map<String, Object> getDefaultOrderStats() {
        Map<String, Object> orderStats = new HashMap<>();
        orderStats.put("total", 0);
        orderStats.put("waitPay", 0);
        orderStats.put("waitDeliver", 0);
        orderStats.put("partDeliver", 0);
        orderStats.put("waitVerify", 0);
        orderStats.put("waitReceive", 0);
        orderStats.put("received", 0);
        orderStats.put("completed", 0);
        orderStats.put("cancelled", 0);
        return orderStats;
    }

    /**
     * 默认用户资产（出错时使用）
     */
    private Map<String, Object> getDefaultUserAssets() {
        Map<String, Object> userAssets = new HashMap<>();
        userAssets.put("balance", "0.00");
        userAssets.put("coupons", 0);
        userAssets.put("points", 0);
        userAssets.put("experience", 0);
        userAssets.put("collections", 0);
        return userAssets;
    }

    /**
     * 修改用户信息
     * @param request 用户信息请求
     * @return CommonResult
     */
    @ApiOperation("修改用户信息")
    @PostMapping("/update")
    public CommonResult<String> update(@RequestBody UserUpdateRequest request) {
        if (userService.updateUser(request)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    @ApiOperation(value = "获取关于我们信息")
    @RequestMapping(value = "/aboutus/info", method = RequestMethod.GET)
    public CommonResult<String> getAboutUsInfo() {
        return CommonResult.success(systemConfigService.getAgreementByKey(SysConfigConstants.ABOUTUS_AGREEMENT));
    }

    /**
     * 用户登录 - 纯token版本（返回纯UUID token）
     * @param request 登录请求参数  
     * @return CommonResult
     */
    @ApiOperation("用户登录-纯token版本")
    @PostMapping("/login-pure")
    public CommonResult<Map<String, Object>> loginPure(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String password = request.get("password");
        String code = request.get("code");
        
        LoginResponse result;
        
        if (code != null && !code.isEmpty()) {
            // 验证码登录
            LoginMobileRequest mobileRequest = new LoginMobileRequest();
            mobileRequest.setPhone(phone);
            mobileRequest.setCaptcha(code);
            result = loginService.phoneCaptchaLogin(mobileRequest);
        } else {
            // 密码登录
            LoginPasswordRequest passwordRequest = new LoginPasswordRequest();
            passwordRequest.setPhone(phone);
            passwordRequest.setPassword(password);
            result = loginService.phonePasswordLogin(passwordRequest);
        }
        
        // 构建返回数据，提取纯token（移除Redis前缀）
        Map<String, Object> responseData = new HashMap<>();
        
        // 从token中提取纯UUID部分
        String pureToken = result.getToken();
        if (pureToken.startsWith("TOKEN_USER_NORMAL_REDIS")) {
            pureToken = pureToken.replace("TOKEN_USER_NORMAL_REDIS", "");
        } else if (pureToken.startsWith("TOKEN_USER_SHOP_MANAGER_REDIS")) {
            pureToken = pureToken.replace("TOKEN_USER_SHOP_MANAGER_REDIS", "");
        }
        
        responseData.put("token", pureToken);
        
        // 构建用户信息
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", result.getId());
        userInfo.put("nikeName", result.getNikeName());
        userInfo.put("phone", result.getPhone());
        userInfo.put("avatar", result.getAvatar());
        userInfo.put("isNew", result.getIsNew());
        
        responseData.put("userInfo", userInfo);
        responseData.put("isNew", result.getIsNew());
        
        return CommonResult.success(responseData);
    }
} 