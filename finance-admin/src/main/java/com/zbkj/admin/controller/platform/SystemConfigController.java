package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.system.SystemConfig;
import com.zbkj.common.request.SaveConfigRequest;
import com.zbkj.common.request.SystemConfigAdminRequest;
import com.zbkj.common.request.SystemFormCheckRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.response.PayConfigResponse;
import com.zbkj.common.request.PayConfigRequest;
import com.zbkj.service.service.SystemConfigService;
import com.zbkj.service.service.PayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


/**
 * 配置表 前端控制器
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
@RequestMapping("api/admin/platform/system/config")
@Api(tags = "平台端设置控制器")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private PayService payService;

    /**
     * 查询配置表信息
     * @param formId Integer
     */
    @PreAuthorize("hasAuthority('platform:system:config:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> info(@RequestParam(value = "formId") Integer formId) {
        return CommonResult.success(systemConfigService.info(formId));
    }


    /**
     * 整体保存表单数据
     * @param systemFormCheckRequest SystemFormCheckRequest 新增参数
     */
    @PreAuthorize("hasAuthority('platform:system:config:save:form')")
    @ApiOperation(value = "整体保存表单数据")
    @RequestMapping(value = "/save/form", method = RequestMethod.POST)
    public CommonResult<String> saveFrom(@RequestBody @Validated SystemFormCheckRequest systemFormCheckRequest) {
        if (systemConfigService.saveForm(systemFormCheckRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 检测表单name是否存在
     * @param name name
     */
    @PreAuthorize("hasAuthority('platform:system:config:check')")
    @ApiOperation(value = "检测表单name是否存在")
    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public CommonResult<Boolean> check(@RequestParam String name) {
        return CommonResult.success(systemConfigService.checkName(name));
    }

//    /**
//     * 配置表中仅仅存储对应的配置
//     * @param key 配置表中的配置字段
//     * @param value 对应的值
//     */
//    @PreAuthorize("hasAuthority('platform:system:config:saveuniq')")
//    @ApiOperation(value = "表单配置中仅仅存储")
//    @RequestMapping(value = "/saveuniq", method = RequestMethod.POST)
//    public CommonResult<Boolean> justSaveUniq(@RequestParam String key, @RequestParam String value) {
//        return CommonResult.success(systemConfigService.updateOrSaveValueByName(key, value));
//    }
//
//    /**
//     * 根据key获取表单配置数据
//     * @param key 配置表的的字段
//     */
//    @PreAuthorize("hasAuthority('platform:system:config:getuniq')")
//    @ApiOperation(value = "表单配置根据key获取")
//    @RequestMapping(value = "/getuniq", method = RequestMethod.GET)
//    public CommonResult<Object> justGetUniq(@RequestParam String key) {
//        return CommonResult.success(systemConfigService.getValueByKey(key));
//    }

    /**
     * 更新配置信息
     */
    @PreAuthorize("hasAuthority('platform:system:config:update')")
    @ApiOperation(value = "更新配置信息")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<List<SystemConfig>> getByKey(@RequestBody @Validated List<SystemConfigAdminRequest> requestList) {
        if (systemConfigService.updateByList(requestList)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    @PreAuthorize("hasAuthority('platform:system:config:change:color:get')")
    @ApiOperation(value = "获取主题色")
    @RequestMapping(value = "/get/change/color", method = RequestMethod.GET)
    public CommonResult<SystemConfig> getChangeColor() {
        return CommonResult.success(systemConfigService.getChangeColor());
    }

    @PreAuthorize("hasAuthority('platform:system:config:change:color:save')")
    @ApiOperation(value = "保存主题色")
    @RequestMapping(value = "/save/change/color", method = RequestMethod.POST)
    public CommonResult<String> saveChangeColor(@RequestBody SaveConfigRequest request) {
        if (systemConfigService.saveChangeColor(request)) {
            return CommonResult.success("保存成功");
        }
        return CommonResult.failed("保存失败");
    }

    /**
     * 获取支付配置
     */
    @PreAuthorize("hasAuthority('platform:system:config:pay:get')")
    @ApiOperation(value = "获取支付配置")
    @RequestMapping(value = "/pay/get", method = RequestMethod.GET)
    public CommonResult<PayConfigResponse> getPayConfig() {
        return CommonResult.success(payService.getPayConfig());
    }

    /**
     * 保存支付配置
     */
    @PreAuthorize("hasAuthority('platform:system:config:pay:save')")
    @ApiOperation(value = "保存支付配置")
    @RequestMapping(value = "/pay/save", method = RequestMethod.POST)
    public CommonResult<String> savePayConfig(@RequestBody @Validated PayConfigRequest request) {
        if (systemConfigService.savePayConfig(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



