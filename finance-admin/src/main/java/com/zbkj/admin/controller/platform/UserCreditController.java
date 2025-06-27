package com.zbkj.admin.controller.platform;

import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.UserCreditInfoVO;
import com.zbkj.service.service.UserCreditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 用户授信管理控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/user-credit")
@Api(tags = "平台端 - 用户授信管理")
public class UserCreditController {

    @Autowired
    private UserCreditService userCreditService;

    /**
     * 获取用户授信信息
     */
    @PreAuthorize("hasAuthority('platform:user-credit:info')")
    @ApiOperation(value = "获取用户授信信息")
    @GetMapping("/info/{userId}")
    public CommonResult<UserCreditInfoVO> getUserCreditInfo(@PathVariable Integer userId) {
        UserCreditInfoVO creditInfo = userCreditService.getUserCreditInfo(userId);
        return CommonResult.success(creditInfo);
    }

    /**
     * 分配授信额度
     */
    @PreAuthorize("hasAuthority('platform:user-credit:allocate')")
    @ApiOperation(value = "分配授信额度")
    @PostMapping("/allocate")
    public CommonResult<String> allocateCredit(
            @RequestParam Integer fromUserId,
            @RequestParam Integer toUserId,
            @RequestParam BigDecimal amount,
            @RequestParam Integer auditUserId,
            @RequestParam(required = false) String remark) {
        
        Boolean result = userCreditService.allocateCredit(fromUserId, toUserId, amount, auditUserId, remark);
        return result ? CommonResult.success("授信额度分配成功") : CommonResult.failed("授信额度分配失败");
    }

    /**
     * 使用授信额度
     */
    @PreAuthorize("hasAuthority('platform:user-credit:use')")
    @ApiOperation(value = "使用授信额度")
    @PostMapping("/use")
    public CommonResult<String> useCredit(
            @RequestParam Integer userId,
            @RequestParam BigDecimal amount,
            @RequestParam String businessType,
            @RequestParam String businessId) {
        
        Boolean result = userCreditService.useCredit(userId, amount, businessType, businessId);
        return result ? CommonResult.success("授信额度使用成功") : CommonResult.failed("授信额度使用失败");
    }

    /**
     * 还款
     */
    @PreAuthorize("hasAuthority('platform:user-credit:repay')")
    @ApiOperation(value = "还款")
    @PostMapping("/repay")
    public CommonResult<String> repayCredit(
            @RequestParam Integer userId,
            @RequestParam BigDecimal amount,
            @RequestParam String businessType,
            @RequestParam String businessId) {
        
        Boolean result = userCreditService.repayCredit(userId, amount, businessType, businessId);
        return result ? CommonResult.success("还款成功") : CommonResult.failed("还款失败");
    }

    /**
     * 冻结用户授信
     */
    @PreAuthorize("hasAuthority('platform:user-credit:freeze')")
    @ApiOperation(value = "冻结用户授信")
    @PostMapping("/freeze")
    public CommonResult<String> freezeCredit(
            @RequestParam Integer userId,
            @RequestParam String reason,
            @RequestParam Integer operatorId) {
        
        Boolean result = userCreditService.freezeCredit(userId, reason, operatorId);
        return result ? CommonResult.success("授信冻结成功") : CommonResult.failed("授信冻结失败");
    }

    /**
     * 解冻用户授信
     */
    @PreAuthorize("hasAuthority('platform:user-credit:unfreeze')")
    @ApiOperation(value = "解冻用户授信")
    @PostMapping("/unfreeze")
    public CommonResult<String> unfreezeCredit(
            @RequestParam Integer userId,
            @RequestParam String reason,
            @RequestParam Integer operatorId) {
        
        Boolean result = userCreditService.unfreezeCredit(userId, reason, operatorId);
        return result ? CommonResult.success("授信解冻成功") : CommonResult.failed("授信解冻失败");
    }

    /**
     * 检查用户授信额度是否足够
     */
    @PreAuthorize("hasAuthority('platform:user-credit:check')")
    @ApiOperation(value = "检查用户授信额度是否足够")
    @GetMapping("/check/{userId}")
    public CommonResult<Boolean> checkCreditSufficient(
            @PathVariable Integer userId,
            @RequestParam BigDecimal amount) {
        
        Boolean sufficient = userCreditService.checkCreditSufficient(userId, amount);
        return CommonResult.success(sufficient);
    }

    /**
     * 获取用户可用授信额度
     */
    @PreAuthorize("hasAuthority('platform:user-credit:available')")
    @ApiOperation(value = "获取用户可用授信额度")
    @GetMapping("/available/{userId}")
    public CommonResult<BigDecimal> getAvailableCredit(@PathVariable Integer userId) {
        BigDecimal availableCredit = userCreditService.getAvailableCredit(userId);
        return CommonResult.success(availableCredit);
    }

    /**
     * 更新用户养殖信息
     */
    @PreAuthorize("hasAuthority('platform:user-credit:update-breeding')")
    @ApiOperation(value = "更新用户养殖信息")
    @PostMapping("/update-breeding/{userId}")
    public CommonResult<String> updateUserBreedingInfo(@PathVariable Integer userId) {
        Boolean result = userCreditService.updateUserBreedingInfo(userId);
        return result ? CommonResult.success("养殖信息更新成功") : CommonResult.failed("养殖信息更新失败");
    }
} 