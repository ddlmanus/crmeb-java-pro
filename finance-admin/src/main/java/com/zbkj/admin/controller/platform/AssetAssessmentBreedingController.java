package com.zbkj.admin.controller.platform;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.AssetAssessmentBreeding;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.AssetAssessmentBreedingSearchVO;
import com.zbkj.common.vo.finance.AssetAssessmentBreedingStatisticsVO;
import com.zbkj.service.service.finance.AssetAssessmentBreedingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资产评估养殖品种管理控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/asset-assessment-breeding")
@Api(tags = "平台端 - 资产评估养殖品种管理")
public class AssetAssessmentBreedingController {

    @Autowired
    private AssetAssessmentBreedingService assetAssessmentBreedingService;

    /**
     * 分页查询资产评估养殖品种列表
     */
  //  @PreAuthorize("hasAuthority('platform:asset-assessment-breeding:list')")
    @ApiOperation(value = "分页查询资产评估养殖品种列表")
    @GetMapping("/list")
    public CommonResult<PageInfo<AssetAssessmentBreeding>> getList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "keywords", required = false) String keywords,
            @RequestParam(value = "farmCode", required = false) String farmCode,
            @RequestParam(value = "farmName", required = false) String farmName,
            @RequestParam(value = "breedName", required = false) String breedName,
            @RequestParam(value = "breedType", required = false) String breedType,
            @RequestParam(value = "assessmentId", required = false) String assessmentId,
            @RequestParam(value = "dateLimit", required = false) String dateLimit) {
        
        log.info("分页查询参数: page={}, limit={}, keywords={}, farmCode={}, farmName={}, breedName={}, breedType={}, assessmentId={}, dateLimit={}", 
                page, limit, keywords, farmCode, farmName, breedName, breedType, assessmentId, dateLimit);
        
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setPage(page);
        pageParamRequest.setLimit(limit);
        
        AssetAssessmentBreedingSearchVO searchVO = new AssetAssessmentBreedingSearchVO();
        searchVO.setKeywords(keywords);
        searchVO.setFarmCode(farmCode);
        searchVO.setFarmName(farmName);
        searchVO.setBreedName(breedName);
        searchVO.setBreedType(breedType);
        searchVO.setAssessmentId(assessmentId);
        searchVO.setDateLimit(dateLimit);
        
        PageInfo<AssetAssessmentBreeding> pageInfo = assetAssessmentBreedingService.getPage(pageParamRequest, searchVO);
        log.info("查询结果: 总数={}, 当前页记录数={}", pageInfo.getTotal(), pageInfo.getList().size());
        
        return CommonResult.success(pageInfo);
    }

    /**
     * 根据ID获取养殖品种详情
     */
   // @PreAuthorize("hasAuthority('platform:asset-assessment-breeding:info')")
    @ApiOperation(value = "根据ID获取养殖品种详情")
    @GetMapping("/info/{id}")
    public CommonResult<AssetAssessmentBreeding> info(@PathVariable String id) {
        AssetAssessmentBreeding breeding = assetAssessmentBreedingService.getById(id);
        return CommonResult.success(breeding);
    }

    /**
     * 根据资产评估ID获取养殖品种列表
     */
  //  @PreAuthorize("hasAuthority('platform:asset-assessment-breeding:list')")
    @ApiOperation(value = "根据资产评估ID获取养殖品种列表")
    @GetMapping("/by-assessment/{assessmentId}")
    public CommonResult<List<AssetAssessmentBreeding>> getByAssessmentId(@PathVariable String assessmentId) {
        List<AssetAssessmentBreeding> breedingList = assetAssessmentBreedingService.getByAssessmentId(assessmentId);
        return CommonResult.success(breedingList);
    }

        /**
     * 根据授信申请ID获取养殖品种列表
     */
   //  @PreAuthorize("hasAuthority('platform:asset-assessment-breeding:list')")
    @ApiOperation(value = "根据授信申请ID获取养殖品种列表")
    @GetMapping("/by-credit-application/{applicationId}")
    public CommonResult<List<AssetAssessmentBreeding>> getByCreditApplicationId(@PathVariable String applicationId) {
        List<AssetAssessmentBreeding> breedingList = assetAssessmentBreedingService.getByCreditApplicationId(applicationId);
        return CommonResult.success(breedingList);
    }

    /**
     * 获取资产评估养殖品种统计信息
     */
   //  @PreAuthorize("hasAuthority('platform:asset-assessment-breeding:statistics')")
    @ApiOperation(value = "获取资产评估养殖品种统计信息")
    @GetMapping("/statistics")
    public CommonResult<AssetAssessmentBreedingStatisticsVO> getStatistics(AssetAssessmentBreedingSearchVO searchVO) {
        AssetAssessmentBreedingStatisticsVO statistics = assetAssessmentBreedingService.getStatistics(searchVO);
        return CommonResult.success(statistics);
    }
} 