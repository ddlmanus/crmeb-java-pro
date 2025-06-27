package com.zbkj.admin.controller.platform;


import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.UserAdminDetailResponse;
import com.zbkj.common.response.UserResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 平台端用户控制器
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
@RestController
@RequestMapping("api/admin/platform/user")
@Api(tags = "平台端用户控制器")
@Validated
public class PlatformUserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('platform:user:page:list')")
    @ApiOperation(value = "平台端用户分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserResponse>> getList(@ModelAttribute @Validated UserSearchRequest request) {
        CommonPage<UserResponse> userCommonPage = CommonPage.restPage(userService.getPlatformPage(request));
        return CommonResult.success(userCommonPage);
    }

    @PreAuthorize("hasAuthority('platform:user:page:list')")
    @ApiOperation(value = "根据机构代码获取用户分页列表")
    @RequestMapping(value = "/list/by-farm", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserResponse>> getListByFarmCode(
            @RequestParam("farmCode") String farmCode,
            @ModelAttribute @Validated PageParamRequest pageParamRequest) {
        
        // 创建搜索条件，包含farmCode
        UserSearchRequest searchRequest = new UserSearchRequest();
        searchRequest.setFarmCode(farmCode);
        searchRequest.setPage(pageParamRequest.getPage());
        searchRequest.setLimit(pageParamRequest.getLimit());
        
        CommonPage<UserResponse> userCommonPage = CommonPage.restPage(userService.getPlatformPage(searchRequest));
        return CommonResult.success(userCommonPage);
    }

    @PreAuthorize("hasAuthority('platform:user:add')")
    @ApiOperation(value = "新增用户")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated UserAddRequest userRequest) {
        if (userService.addUser(userRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:update')")
    @ApiOperation(value = "修改用户信息")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated UserUpdateRequest userRequest) {
        if (userService.updateUser(userRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:detail')")
    @ApiOperation(value = "用户详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<UserAdminDetailResponse> detail(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(userService.getAdminDetail(id));
    }

    @PreAuthorize("hasAuthority('platform:user:tag')")
    @ApiOperation(value = "用户分配标签")
    @RequestMapping(value = "/tag", method = RequestMethod.POST)
    public CommonResult<String> tag(@RequestBody @Validated UserAssignTagRequest request) {
        if (userService.tag(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:operate:integer')")
    @ApiOperation(value = "操作用户积分")
    @RequestMapping(value = "/operate/integer", method = RequestMethod.GET)
    public CommonResult<Object> founds(@Validated UserOperateIntegralRequest request) {
        if (userService.operateUserInteger(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:operate:balance')")
    @ApiOperation(value = "操作用户余额")
    @RequestMapping(value = "/operate/balance", method = RequestMethod.GET)
    public CommonResult<Object> balance(@Validated UserOperateBalanceRequest request) {
        if (userService.operateUserBalance(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:gift:paid:member')")
    @ApiOperation(value = "赠送用户付费会员")
    @RequestMapping(value = "/gift/paid/member", method = RequestMethod.POST)
    public CommonResult<String> giftPaidMember(@RequestBody @Validated GiftPaidMemberRequest request) {
        if (userService.giftPaidMember(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:user:page:list')")
    @ApiOperation(value = "获取用户授信统计信息")
    @RequestMapping(value = "/credit/statistics", method = RequestMethod.GET)
    public CommonResult<com.zbkj.common.response.UserCreditStatisticsResponse> getCreditStatistics() {
        return CommonResult.success(userService.getUserCreditStatistics());
    }
}



