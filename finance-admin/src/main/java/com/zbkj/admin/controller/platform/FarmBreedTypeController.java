package com.zbkj.admin.controller.platform;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zbkj.common.model.finance.FarmBreedType;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.FarmBreedTypeRequestVO;
import com.zbkj.common.vo.finance.FarmBreedTypeSearchVO;
import com.zbkj.service.service.finance.FarmBreedTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 养殖品种类型管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/finance/farmBreedType")
@Api(tags = "养殖品种类型管理")
public class FarmBreedTypeController {

    @Autowired
    private FarmBreedTypeService farmBreedTypeService;

    /**
     * 分页查询养殖品种类型
     */
    @PostMapping("/page")
    @ApiOperation(value = "分页查询养殖品种类型")
    @PreAuthorize("hasAuthority('admin:finance:farmBreedType:page')")
    public CommonResult<IPage<FarmBreedType>> page(@RequestBody FarmBreedTypeSearchVO searchVO, 
                                                    @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(farmBreedTypeService.pageList(pageParamRequest, searchVO));
    }

    /**
     * 新增养殖品种类型
     */
    @PostMapping("/add")
    @ApiOperation(value = "新增养殖品种类型")
    @PreAuthorize("hasAuthority('admin:finance:farmBreedType:add')")
    public CommonResult<Boolean> add(@RequestBody @Validated FarmBreedTypeRequestVO requestVO) {
        return CommonResult.success(farmBreedTypeService.add(requestVO));
    }

    /**
     * 编辑养殖品种类型
     */
    @PostMapping("/edit")
    @ApiOperation(value = "编辑养殖品种类型")
    @PreAuthorize("hasAuthority('admin:finance:farmBreedType:edit')")
    public CommonResult<Boolean> edit(@RequestBody @Validated FarmBreedTypeRequestVO requestVO) {
        return CommonResult.success(farmBreedTypeService.edit(requestVO));
    }

    /**
     * 删除养殖品种类型
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除养殖品种类型")
    @PreAuthorize("hasAuthority('admin:finance:farmBreedType:delete')")
    public CommonResult<Boolean> delete(@PathVariable("id") String id) {
        return CommonResult.success(farmBreedTypeService.delete(id));
    }

    /**
     * 获取养殖品种类型详情
     */
    @GetMapping("/detail/{id}")
    @ApiOperation(value = "获取养殖品种类型详情")
    @PreAuthorize("hasAuthority('admin:finance:farmBreedType:detail')")
    public CommonResult<FarmBreedType> detail(@PathVariable("id") String id) {
        return CommonResult.success(farmBreedTypeService.getDetailById(id));
    }

    /**
     * 获取所有养殖品种类型列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取所有养殖品种类型列表")
    @PreAuthorize("hasAuthority('admin:finance:farmBreedType:list')")
    public CommonResult<List<FarmBreedType>> list() {
        return CommonResult.success(farmBreedTypeService.getAllList());
    }

    /**
     * 根据养殖场编码查询存栏数据
     */
    @GetMapping("/getByFarmCode/{farmCode}")
    @ApiOperation(value = "根据养殖场编码查询存栏数据")
    @PreAuthorize("hasAuthority('admin:finance:farmBreedType:detail')")
    public CommonResult<List<FarmBreedType>> getByFarmCode(@PathVariable("farmCode") String farmCode) {
        return CommonResult.success(farmBreedTypeService.getByFarmCode(farmCode));
    }
} 