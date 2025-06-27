package com.zbkj.admin.controller.platform;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.system.SystemDictType;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.system.SystemDictSearchRequest;
import com.zbkj.common.request.system.SystemDictTypeRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.service.service.system.SystemDictTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典类型管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/platform/system/dict/type")
@Api(tags = "平台端 - 系统管理 - 字典类型")
public class SystemDictTypeController {

    @Autowired
    private SystemDictTypeService systemDictTypeService;

    /**
     * 分页列表
     */
   // @PreAuthorize("hasAuthority('platform:system:dict:type:list')")
    @ApiOperation(value = "字典类型分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<PageInfo<SystemDictType>> getList(@Validated PageParamRequest pageParamRequest, 
                                                          SystemDictSearchRequest searchRequest) {
        PageInfo<SystemDictType> pageInfo = systemDictTypeService.getAdminPage(pageParamRequest, searchRequest);
        return CommonResult.success(pageInfo);
    }

    /**
     * 新增字典类型
     */
  //  @PreAuthorize("hasAuthority('platform:system:dict:type:add')")
    @ApiOperation(value = "新增字典类型")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated SystemDictTypeRequest request) {
        if (systemDictTypeService.add(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 删除字典类型
     */
   // @PreAuthorize("hasAuthority('platform:system:dict:type:delete')")
    @ApiOperation(value = "删除字典类型")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CommonResult<String> delete(@RequestParam Long id) {
        if (systemDictTypeService.delete(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 修改字典类型
     */
  //  @PreAuthorize("hasAuthority('platform:system:dict:type:edit')")
    @ApiOperation(value = "修改字典类型")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated SystemDictTypeRequest request) {
        if (systemDictTypeService.edit(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 查询字典类型详情
     */
 //   @PreAuthorize("hasAuthority('platform:system:dict:type:info')")
    @ApiOperation(value = "字典类型详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemDictType> info(@RequestParam Long id) {
        SystemDictType dictType = systemDictTypeService.getById(id);
        if (dictType == null || dictType.getDeleteFlag() == 1) {
            return CommonResult.failed(CommonResultCode.VALIDATE_FAILED, "字典类型不存在");
        }
        return CommonResult.success(dictType);
    }

    /**
     * 获取所有启用的字典类型
     */
  //  @PreAuthorize("hasAuthority('platform:system:dict:type:list')")
    @ApiOperation(value = "获取所有启用的字典类型")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public CommonResult<List<SystemDictType>> getAllEnabled() {
        List<SystemDictType> list = systemDictTypeService.getAllEnabled();
        return CommonResult.success(list);
    }

    /**
     * 批量删除字典类型
     */
  //  @PreAuthorize("hasAuthority('platform:system:dict:type:delete')")
    @ApiOperation(value = "批量删除字典类型")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    public CommonResult<String> deleteBatch(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return CommonResult.failed(CommonResultCode.VALIDATE_FAILED, "请选择要删除的数据");
        }
        if (systemDictTypeService.deleteBatch(ids)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 检查字典类型是否存在
     */
  //  @PreAuthorize("hasAuthority('platform:system:dict:type:list')")
    @ApiOperation(value = "检查字典类型是否存在")
    @RequestMapping(value = "/checkDictType", method = RequestMethod.GET)
    public CommonResult<Boolean> checkDictType(@RequestParam String dictType, 
                                               @RequestParam(required = false) Long excludeId) {
        Boolean exists = systemDictTypeService.checkDictType(dictType, excludeId);
        return CommonResult.success(exists);
    }
} 