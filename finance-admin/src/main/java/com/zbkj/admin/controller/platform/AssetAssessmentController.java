package com.zbkj.admin.controller.platform;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.AssetAssessment;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.AssetAssessmentDetailVO;
import com.zbkj.common.vo.finance.AssetAssessmentPageVO;
import com.zbkj.common.vo.finance.AssetAssessmentSearchVO;
import com.zbkj.service.service.finance.AssetAssessmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 资产评估管理控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/asset-assessment")
@Api(tags = "平台端 - 资产评估管理")
public class AssetAssessmentController {

    @Autowired
    private AssetAssessmentService assetAssessmentService;

    /**
     * 分页查询资产评估记录 (POST方法)
     */
    @PreAuthorize("hasAuthority('platform:asset-assessment:list')")
    @ApiOperation(value = "分页查询资产评估记录")
    @PostMapping("/list")
    public CommonResult<CommonPage<AssetAssessmentPageVO>> getList(@RequestBody @Validated AssetAssessmentSearchVO searchVO) {
        PageInfo<AssetAssessmentPageVO> pageInfo = assetAssessmentService.getPageForAdmin(searchVO);
        return CommonResult.success(CommonPage.restPage(pageInfo));
    }

    /**
     * 获取资产评估详情
     */
    @PreAuthorize("hasAuthority('platform:asset-assessment:info')")
    @ApiOperation(value = "获取资产评估详情")
    @GetMapping("/info/{id}")
    public CommonResult<AssetAssessmentDetailVO> info(@PathVariable String id) {
        AssetAssessmentDetailVO detail = assetAssessmentService.getAssessmentDetail(id);
        return CommonResult.success(detail);
    }

    /**
     * 根据授信申请ID获取资产评估信息
     */
    @PreAuthorize("hasAuthority('platform:asset-assessment:info')")
    @ApiOperation(value = "根据授信申请ID获取资产评估信息")
    @GetMapping("/by-credit-application/{applicationId}")
    public CommonResult<AssetAssessmentDetailVO> getByCreditApplication(@PathVariable String applicationId) {
        AssetAssessmentDetailVO detail = assetAssessmentService.getAssessmentByCreditApplication(applicationId);
        return CommonResult.success(detail);
    }

    /**
     * 根据用户ID获取最新可用的资产评估
     */
    @PreAuthorize("hasAuthority('platform:asset-assessment:info')")
    @ApiOperation(value = "根据用户ID获取最新可用的资产评估")
    @GetMapping("/latest/{userId}")
    public CommonResult<AssetAssessmentDetailVO> getLatestByUser(@PathVariable Integer userId) {
        String assessmentId = assetAssessmentService.getAvailableAssessmentByUser(userId);
        if (assessmentId != null) {
            AssetAssessmentDetailVO detail = assetAssessmentService.getAssessmentDetail(assessmentId);
            return CommonResult.success(detail);
        }
        return CommonResult.failed();
    }

    /**
     * 统计资产评估数据
     */
    @PreAuthorize("hasAuthority('platform:asset-assessment:statistics')")
    @ApiOperation(value = "统计资产评估数据")
    @GetMapping("/statistics")
    public CommonResult<Object> getStatistics(
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String farmName,
            @RequestParam(required = false) Integer assessmentStatus,
            @RequestParam(required = false) Integer isUsed,
            @RequestParam(required = false) String dateLimit) {
        
        AssetAssessmentSearchVO searchVO = new AssetAssessmentSearchVO();
        searchVO.setKeywords(keywords);
        searchVO.setUserName(userName);
        searchVO.setFarmName(farmName);
        searchVO.setAssessmentStatus(assessmentStatus);
        searchVO.setIsUsed(isUsed);
        
        // 处理时间范围参数
        if (dateLimit != null && dateLimit.contains(" - ")) {
            String[] dates = dateLimit.split(" - ");
            if (dates.length == 2) {
                searchVO.setStartTime(dates[0].trim());
                searchVO.setEndTime(dates[1].trim());
            }
        }
        
        Object statistics = assetAssessmentService.getAssessmentStatistics();
        return CommonResult.success(statistics);
    }
} 