package com.zbkj.admin.controller.platform;

import com.zbkj.admin.task.livestock.BreedingProductSyncTask;
import com.zbkj.common.model.finance.BreedingProduct;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.BreedingProductRequestVO;
import com.zbkj.common.vo.finance.BreedingProductSearchVO;
import com.zbkj.service.service.finance.BreedingProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 养殖品种管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/platform/breeding-product")
@Api(tags = "平台管理 - 养殖品种管理")
public class BreedingProductController {

    @Autowired
    private BreedingProductService breedingProductService;

    @Autowired(required = false)
    private BreedingProductSyncTask breedingProductSyncTask;

    /**
     * 分页查询养殖品种列表
     */
    @PreAuthorize("hasAuthority('platform:breeding:product:list')")
    @ApiOperation(value = "分页查询养殖品种列表", notes = "分页查询养殖品种列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<BreedingProduct>> pageList(@Validated BreedingProductSearchVO searchVO, @Validated PageParamRequest pageParamRequest) {
        try {
            log.info("分页查询养殖品种列表，页码：{}，页大小：{}", pageParamRequest.getPage(), pageParamRequest.getLimit());
            
            CommonPage<BreedingProduct> pageResult = CommonPage.restPage(breedingProductService.pageList(pageParamRequest, searchVO));
            
            log.info("分页查询养殖品种列表成功，总数：{}", pageResult.getTotal());
            return CommonResult.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询养殖品种列表失败", e);
            return CommonResult.failed("查询失败：" + e.getMessage());
        }
    }

    /**
     * 添加养殖品种
     */
    @PreAuthorize("hasAuthority('platform:breeding:product:add')")
    @ApiOperation(value = "添加养殖品种", notes = "添加新的养殖品种")
    @PostMapping("/add")
    public CommonResult<Boolean> add(
            @ApiParam(value = "添加参数", required = true) @Validated @RequestBody BreedingProductRequestVO requestVO) {
        try {
            log.info("添加养殖品种，品种名称：{}，品种编号：{}", requestVO.getName(), requestVO.getCode());
            Boolean result = breedingProductService.add(requestVO);
            log.info("添加养殖品种成功");
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("添加养殖品种失败", e);
            return CommonResult.failed("添加失败：" + e.getMessage());
        }
    }

    /**
     * 编辑养殖品种
     */
    @PreAuthorize("hasAuthority('platform:breeding:product:edit')")
    @ApiOperation(value = "编辑养殖品种", notes = "编辑养殖品种信息")
    @PostMapping("/edit")
    public CommonResult<Boolean> edit(
            @ApiParam(value = "编辑参数", required = true) @Validated @RequestBody BreedingProductRequestVO requestVO) {
        try {
            log.info("编辑养殖品种，ID：{}，品种名称：{}", requestVO.getId(), requestVO.getName());
            Boolean result = breedingProductService.edit(requestVO);
            log.info("编辑养殖品种成功");
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("编辑养殖品种失败", e);
            return CommonResult.failed("编辑失败：" + e.getMessage());
        }
    }

    /**
     * 删除养殖品种
     */
    @PreAuthorize("hasAuthority('platform:breeding:product:delete')")
    @ApiOperation(value = "删除养殖品种", notes = "删除指定的养殖品种")
    @PostMapping("/delete/{id}")
    public CommonResult<Boolean> delete(
            @ApiParam(value = "品种ID", required = true) @PathVariable("id") String id) {
        try {
            log.info("删除养殖品种，ID：{}", id);
            Boolean result = breedingProductService.delete(id);
            log.info("删除养殖品种成功");
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("删除养殖品种失败", e);
            return CommonResult.failed("删除失败：" + e.getMessage());
        }
    }

    /**
     * 检查品种编号是否存在
     */
    @ApiOperation(value = "检查品种编号", notes = "检查品种编号是否已存在")
    @GetMapping("/check-code")
    public CommonResult<Boolean> checkCode(
            @ApiParam(value = "品种编号", required = true) @RequestParam("code") String code,
            @ApiParam(value = "排除的ID") @RequestParam(value = "excludeId", required = false) String excludeId) {
        try {
            Boolean exists = breedingProductService.checkCode(code, excludeId);
            return CommonResult.success(exists);
        } catch (Exception e) {
            log.error("检查品种编号失败", e);
            return CommonResult.failed("检查失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有养殖品种列表
     */
    @ApiOperation(value = "获取所有养殖品种", notes = "获取所有可选的养殖品种列表")
    @GetMapping("/all")
    public CommonResult<List<BreedingProduct>> getAllList() {
        try {
            log.info("获取所有养殖品种列表");
            List<BreedingProduct> list = breedingProductService.getAllList();
            log.info("获取所有养殖品种列表成功，共{}个", list.size());
            return CommonResult.success(list);
        } catch (Exception e) {
            log.error("获取所有养殖品种列表失败", e);
            return CommonResult.failed("获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取养殖品种选项列表（用于下拉选择）
     */
    @ApiOperation(value = "获取养殖品种选项", notes = "获取用于下拉选择的养殖品种选项")
    @GetMapping("/options")
    public CommonResult<List<BreedingProduct>> getOptions() {
        try {
            log.info("获取养殖品种选项列表");
            List<BreedingProduct> options = breedingProductService.getAllList();
            log.info("获取养殖品种选项列表成功，共{}个", options.size());
            return CommonResult.success(options);
        } catch (Exception e) {
            log.error("获取养殖品种选项列表失败", e);
            return CommonResult.failed("获取失败：" + e.getMessage());
        }
    }

    /**
     * 手动同步所有养殖场的品种数据
     */
    @PreAuthorize("hasAuthority('platform:breeding:product:sync')")
    @ApiOperation(value = "手动同步所有养殖品种", notes = "手动触发同步所有养殖场的品种数据")
    @PostMapping("/sync/all")
    public CommonResult<String> syncAllBreedingProducts() {
        try {
            log.info("手动触发同步所有养殖品种数据");
            
            if (breedingProductSyncTask == null) {
                return CommonResult.failed("同步任务组件未启用");
            }
            
            // 异步执行同步任务，避免接口超时
            asyncSyncAll();
            
            return CommonResult.success("同步任务已启动，请查看日志了解同步进度");
        } catch (Exception e) {
            log.error("手动同步所有养殖品种失败", e);
            return CommonResult.failed("同步失败：" + e.getMessage());
        }
    }

    /**
     * 异步执行同步所有品种任务
     */
    @Async
    public void asyncSyncAll() {
        try {
            breedingProductSyncTask.manualSyncAll();
        } catch (Exception e) {
            log.error("异步执行同步所有品种任务失败", e);
        }
    }

    /**
     * 手动同步指定养殖场的品种数据
     */
    @PreAuthorize("hasAuthority('platform:breeding:product:sync')")
    @ApiOperation(value = "手动同步指定养殖场品种", notes = "手动触发同步指定养殖场的品种数据")
    @PostMapping("/sync/{farmCode}")
    public CommonResult<String> syncBreedingProductsByFarmCode(
            @ApiParam(value = "养殖场编码", required = true) @PathVariable("farmCode") String farmCode) {
        try {
            log.info("手动触发同步养殖场{}的品种数据", farmCode);
            
            if (breedingProductSyncTask == null) {
                return CommonResult.failed("同步任务组件未启用");
            }
            
            // 直接调用同步方法，因为是单个养殖场，应该很快完成
            breedingProductSyncTask.manualSyncByFarmCode(farmCode);
            
            return CommonResult.success("同步完成，请查看日志了解详细结果");
        } catch (Exception e) {
            log.error("手动同步养殖场{}的品种数据失败", farmCode, e);
            return CommonResult.failed("同步失败：" + e.getMessage());
        }
    }
} 