package com.zbkj.admin.controller.platform;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.FarmValuationConfig;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.FarmValuationConfigSearchVO;
import com.zbkj.common.vo.finance.FarmValuationConfigVO;
import com.zbkj.service.service.finance.FarmValuationConfigService;
import com.zbkj.common.model.finance.BreedingProduct;
import com.zbkj.common.model.finance.FarmBreedType;
import com.zbkj.common.model.finance.FarmInstitution;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 养殖场评估价值配置管理控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/farm-valuation-config")
@Api(tags = "平台端 - 养殖场评估价值配置管理")
public class FarmValuationConfigController {

    @Autowired
    private FarmValuationConfigService farmValuationConfigService;

    /**
     * 分页查询养殖场评估价值配置
     */
    @PreAuthorize("hasAuthority('platform:farm-valuation-config:list')")
    @ApiOperation(value = "分页查询养殖场评估价值配置")
    @GetMapping("/list")
    public CommonResult<PageInfo<FarmValuationConfig>> getList(@Validated PageParamRequest pageParamRequest, 
                                                               FarmValuationConfigSearchVO searchVO) {
        PageInfo<FarmValuationConfig> pageInfo = farmValuationConfigService.getPage(pageParamRequest, searchVO);
        return CommonResult.success(pageInfo);
    }

    /**
     * 新增养殖场评估价值配置
     */
    @PreAuthorize("hasAuthority('platform:farm-valuation-config:save')")
    @ApiOperation(value = "新增养殖场评估价值配置")
    @PostMapping("/save")
    public CommonResult<String> save(@RequestBody @Validated FarmValuationConfigVO configVO) {
        Boolean result = farmValuationConfigService.add(configVO);
        if (result) {
            return CommonResult.success("添加成功");
        }
        return CommonResult.failed("添加失败");
    }

    /**
     * 修改养殖场评估价值配置
     */
    @PreAuthorize("hasAuthority('platform:farm-valuation-config:update')")
    @ApiOperation(value = "修改养殖场评估价值配置")
    @PostMapping("/update")
    public CommonResult<String> update(@RequestBody @Validated FarmValuationConfigVO configVO) {
        Boolean result = farmValuationConfigService.edit(configVO);
        if (result) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    /**
     * 删除养殖场评估价值配置
     */
    @PreAuthorize("hasAuthority('platform:farm-valuation-config:delete')")
    @ApiOperation(value = "删除养殖场评估价值配置")
    @PostMapping("/delete/{id}")
    public CommonResult<String> delete(@PathVariable Integer id) {
        Boolean result = farmValuationConfigService.delete(id);
        if (result) {
            return CommonResult.success("删除成功");
        }
        return CommonResult.failed("删除失败");
    }

    /**
     * 根据ID获取配置详情
     */
    @PreAuthorize("hasAuthority('platform:farm-valuation-config:info')")
    @ApiOperation(value = "根据ID获取配置详情")
    @GetMapping("/info/{id}")
    public CommonResult<FarmValuationConfig> info(@PathVariable Integer id) {
        FarmValuationConfig config = farmValuationConfigService.getConfigById(id);
        return CommonResult.success(config);
    }

    /**
     * 启用/禁用配置
     */
    @PreAuthorize("hasAuthority('platform:farm-valuation-config:update')")
    @ApiOperation(value = "启用/禁用配置")
    @PostMapping("/updateStatus")
    public CommonResult<String> updateStatus(@RequestParam Integer id, @RequestParam Integer status) {
        Boolean result = farmValuationConfigService.updateStatus(id, status);
        if (result) {
            return CommonResult.success("状态更新成功");
        }
        return CommonResult.failed("状态更新失败");
    }

    /**
     * 获取所有启用的配置列表
     */
    @ApiOperation(value = "获取所有启用的配置列表")
    @GetMapping("/enabled")
    public CommonResult<List<FarmValuationConfig>> getEnabledConfigs() {
        List<FarmValuationConfig> configs = farmValuationConfigService.getEnabledConfigs();
        return CommonResult.success(configs);
    }
    
    /**
     * 获取所有养殖品种列表
     */
    @ApiOperation(value = "获取所有养殖品种列表")
    @GetMapping("/breeding-products")
    public CommonResult<List<BreedingProduct>> getBreedingProducts() {
        List<BreedingProduct> products = farmValuationConfigService.getAllBreedingProducts();
        return CommonResult.success(products);
    }
    
    /**
     * 根据品种名称获取品种类型列表
     */
    @ApiOperation(value = "根据品种名称获取品种类型列表")
    @GetMapping("/breed-types")
    public CommonResult<List<FarmBreedType>> getBreedTypesByName(@RequestParam String breedName) {
        List<FarmBreedType> breedTypes = farmValuationConfigService.getBreedTypesByName(breedName);
        return CommonResult.success(breedTypes);
    }
    
    /**
     * 获取所有养殖场列表
     */
    @ApiOperation(value = "获取所有养殖场列表")
    @GetMapping("/farms")
    public CommonResult<List<FarmInstitution>> getFarms() {
        List<FarmInstitution> farms = farmValuationConfigService.getAllFarms();
        return CommonResult.success(farms);
    }
    
    /**
     * 根据养殖场编码获取养殖品种列表
     */
    @ApiOperation(value = "根据养殖场编码获取养殖品种列表")
    @GetMapping("/breeding-products-by-farm")
    public CommonResult<List<BreedingProduct>> getBreedingProductsByFarm(@RequestParam String farmCode) {
        List<BreedingProduct> products = farmValuationConfigService.getBreedingProductsByFarm(farmCode);
        return CommonResult.success(products);
    }
} 