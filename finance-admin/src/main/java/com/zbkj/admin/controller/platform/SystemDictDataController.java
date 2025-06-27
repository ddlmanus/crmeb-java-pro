package com.zbkj.admin.controller.platform;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.system.SystemDictData;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.system.SystemDictDataRequest;
import com.zbkj.common.request.system.SystemDictSearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.service.service.system.SystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/platform/system/dict/data")
@Api(tags = "平台端 - 系统管理 - 字典数据")
public class SystemDictDataController {

    @Autowired
    private SystemDictDataService systemDictDataService;

    /**
     * 分页列表
     */
   // @PreAuthorize("hasAuthority('platform:system:dict:data:list')")
    @ApiOperation(value = "字典数据分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<PageInfo<SystemDictData>> getList(@Validated PageParamRequest pageParamRequest,
                                                          SystemDictSearchRequest searchRequest) {
        PageInfo<SystemDictData> pageInfo = systemDictDataService.getAdminPage(pageParamRequest, searchRequest);
        return CommonResult.success(pageInfo);
    }

    /**
     * 新增字典数据
     */
  //  @PreAuthorize("hasAuthority('platform:system:dict:data:add')")
    @ApiOperation(value = "新增字典数据")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public CommonResult<String> add(@RequestBody @Validated SystemDictDataRequest request) {
        if (systemDictDataService.add(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 删除字典数据
     */
  //  @PreAuthorize("hasAuthority('platform:system:dict:data:delete')")
    @ApiOperation(value = "删除字典数据")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CommonResult<String> delete(@RequestParam Long id) {
        if (systemDictDataService.delete(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 修改字典数据
     */
  //  @PreAuthorize("hasAuthority('platform:system:dict:data:edit')")
    @ApiOperation(value = "修改字典数据")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated SystemDictDataRequest request) {
        if (systemDictDataService.edit(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 查询字典数据详情
     */
 //   @PreAuthorize("hasAuthority('platform:system:dict:data:info')")
    @ApiOperation(value = "字典数据详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<SystemDictData> info(@RequestParam Long id) {
        SystemDictData dictData = systemDictDataService.getById(id);
        if (dictData == null || dictData.getDeleteFlag() == 1) {
            return CommonResult.failed(CommonResultCode.VALIDATE_FAILED, "字典数据不存在");
        }
        return CommonResult.success(dictData);
    }

    /**
     * 根据字典类型获取字典数据
     */
  //  @PreAuthorize("hasAuthority('platform:system:dict:data:list')")
    @ApiOperation(value = "根据字典类型获取字典数据")
    @RequestMapping(value = "/type/{dictType}", method = RequestMethod.GET)
    public CommonResult<List<SystemDictData>> getByDictType(@PathVariable String dictType) {
        List<SystemDictData> list = systemDictDataService.getByDictType(dictType);
        return CommonResult.success(list);
    }

    /**
     * 批量删除字典数据
     */
 //   @PreAuthorize("hasAuthority('platform:system:dict:data:delete')")
    @ApiOperation(value = "批量删除字典数据")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    public CommonResult<String> deleteBatch(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return CommonResult.failed(CommonResultCode.VALIDATE_FAILED, "请选择要删除的数据");
        }
        if (systemDictDataService.deleteBatch(ids)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 检查字典值是否存在
     */
   // @PreAuthorize("hasAuthority('platform:system:dict:data:list')")
    @ApiOperation(value = "检查字典值是否存在")
    @RequestMapping(value = "/checkDictValue", method = RequestMethod.GET)
    public CommonResult<Boolean> checkDictValue(@RequestParam String dictType,
                                               @RequestParam String dictValue,
                                               @RequestParam(required = false) Long excludeId) {
        Boolean exists = systemDictDataService.checkDictValue(dictType, dictValue, excludeId);
        return CommonResult.success(exists);
    }

    /**
     * 获取字典标签
     */
   // @PreAuthorize("hasAuthority('platform:system:dict:data:list')")
    @ApiOperation(value = "获取字典标签")
    @RequestMapping(value = "/label", method = RequestMethod.GET)
    public CommonResult<String> getDictLabel(@RequestParam String dictType,
                                            @RequestParam String dictValue) {
        String label = systemDictDataService.getDictLabel(dictType, dictValue);
        return CommonResult.success(label);
    }

    /**
     * 获取字典默认值
     */
  //  @PreAuthorize("hasAuthority('platform:system:dict:data:list')")
    @ApiOperation(value = "获取字典默认值")
    @RequestMapping(value = "/default/{dictType}", method = RequestMethod.GET)
    public CommonResult<SystemDictData> getDefaultByDictType(@PathVariable String dictType) {
        SystemDictData defaultData = systemDictDataService.getDefaultByDictType(dictType);
        return CommonResult.success(defaultData);
    }
} 