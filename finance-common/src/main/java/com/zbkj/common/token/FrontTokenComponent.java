package com.zbkj.common.token;

import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.RedisConstants;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.RequestUtil;
import com.zbkj.common.vo.LoginFrontUserVo;
import com.zbkj.common.vo.LoginUserVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * token验证处理
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
@Component
public class FrontTokenComponent {

    @Resource
    private RedisUtil redisUtil;

    public static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    public static final Long MILLIS_MINUTE = 60 * 1000L;

    // 令牌有效期（修改为1个月）
//    private static final int expireTime = 30;
//    public static final int expireTime = 5 * 60;
    public static final int expireTime = 60 * 24 * 30; // 1个月

    public static final String TOKEN_USER_NORMAL_REDIS = "TOKEN:USER:NORMAL:";
    public static final String TOKEN_USER_SHOP_MANAGER_REDIS = "TOKEN:USER:SHOPMANAGER:";


    public LoginUserVo getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StrUtil.isNotBlank(token)) {
            return redisUtil.get(token);
        }
        return null;
    }


    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token) {
        if (StrUtil.isNotBlank(token)) {
//            String userKey = getTokenKey(token);
//            redisUtil.delete(userKey);
            redisUtil.delete(token);
        }
    }



    /**
     * 获取请求token
     *
     * @param request HttpServletRequest
     * @return token
     */
    public String getToken(HttpServletRequest request) {
        String token = request.getHeader(Constants.HEADER_AUTHORIZATION_KEY);
        
        // 如果token为空，直接返回
        if (StrUtil.isBlank(token)) {
            return token;
        }
        
        // 处理Bearer前缀（如果存在）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // 移除 "Bearer " 前缀
        }
        
        // 如果是带Redis前缀的token，移除前缀得到纯UUID
        if (token.startsWith(TOKEN_USER_NORMAL_REDIS)) {
            return token; // 保持完整的Redis key
        }
        if (token.startsWith(TOKEN_USER_SHOP_MANAGER_REDIS)) {
            return token; // 保持完整的Redis key
        }
        
        // 如果是纯UUID token，需要添加前缀来构建Redis key
        // 默认使用普通用户前缀（可以根据需要调整）
        if (token.length() == 36 && token.contains("-")) { // UUID格式检查
            return TOKEN_USER_NORMAL_REDIS + token;
        }
        
        return token;
    }

    /**
     * 获取是否店铺管理端身份
     * @param request
     * @return
     */
    public String getTokenForMerchantEmployee(HttpServletRequest request) {
        String token = request.getHeader(Constants.HEADER_AUTHORIZATION_KEY);
        
        // 如果token为空，直接返回
        if (StrUtil.isBlank(token)) {
            return token;
        }
        
        // 处理Bearer前缀（如果存在）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // 移除 "Bearer " 前缀
        }
        
        // 如果已经是完整的Redis key格式，直接返回
        if (token.startsWith(TOKEN_USER_NORMAL_REDIS) || token.startsWith(TOKEN_USER_SHOP_MANAGER_REDIS)) {
            return token;
        }
        
        // 如果是纯UUID，优先尝试店铺管理员格式，然后是普通用户格式
        if (token.length() == 36 && token.contains("-")) { // UUID格式检查
            // 优先检查店铺管理员token
            String shopManagerToken = TOKEN_USER_SHOP_MANAGER_REDIS + token;
            if (redisUtil.exists(shopManagerToken)) {
                return shopManagerToken;
            }
            // 如果店铺管理员token不存在，返回普通用户token
            return TOKEN_USER_NORMAL_REDIS + token;
        }
        
        return token;
    }


    /**
     * 推出登录
     *
     * @param request HttpServletRequest
     */
    public void logout(HttpServletRequest request) {
        String token = getToken(request);
        delLoginUser(token);
    }

    /**
     * 获取当前登录用户id
     */
    public Integer getUserId() {
        HttpServletRequest request = RequestUtil.getRequest();
        String token = getToken(request);
        if (StrUtil.isEmpty(token)) {
            return null;
        }
//        return redisUtil.get(getTokenKey(token));
        return redisUtil.get(token);
    }

    /**
     * 获取当前登录的移动端商户管理
     * @return
     */
    public LoginFrontUserVo getUserForMerchantEmployee() {
        HttpServletRequest request = RequestUtil.getRequest();
        String token = getTokenForMerchantEmployee(request);
        if (StrUtil.isEmpty(token)) {
            return null;
        }
        return redisUtil.get(token);
    }


    /**
     * 激活当前要操作的商户
     * @param merchantId 商户id
     */
    public void activeMerchantEmployee(Integer merchantId){
        HttpServletRequest request = RequestUtil.getRequest();
        String token = getTokenForMerchantEmployee(request);
        if (StrUtil.isEmpty(token)) {
            return ;
        }
        LoginFrontUserVo userForMerchantEmployee = redisUtil.get(token);;
        userForMerchantEmployee.setActiveMerchant(merchantId);
        redisUtil.set(token, userForMerchantEmployee, (long) FrontTokenComponent.expireTime, TimeUnit.MINUTES);
    }

    public Boolean check(String token) {

        try {
            boolean exists = redisUtil.exists(token);
            if (exists) {
                Integer uid = redisUtil.get(token);
                redisUtil.set(token, uid, Constants.TOKEN_EXPRESS_MINUTES, TimeUnit.MINUTES);
            }
            return exists;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean checkForEmployee(String token) {

        try {
            boolean exists = redisUtil.exists(token);
            if (exists) {
                LoginFrontUserVo loginFrontUserVo = redisUtil.get(token);
                redisUtil.set(token, loginFrontUserVo, Constants.TOKEN_EXPRESS_MINUTES, TimeUnit.MINUTES);
            }
            return exists;
        } catch (Exception e) {
            return false;
        }
    }
}
