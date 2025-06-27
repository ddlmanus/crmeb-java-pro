package com.zbkj.front.controller.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zbkj.common.model.finance.CreditApplicationAuditHistory;
import com.zbkj.common.model.finance.CreditApplicationInfo;
import com.zbkj.common.model.finance.FarmBreedType;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.*;
import com.zbkj.service.service.finance.CreditApplicationService;
import com.zbkj.service.service.finance.FarmBreedTypeService;
import com.zbkj.service.service.finance.MumaIntegrationService;
import com.zbkj.service.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(tags = "合作社授信额度申请接口")
@RequestMapping("/api/front/finance/creditApplication")
public class CreditApplicationController {

    @Resource
    private CreditApplicationService creditApplicationService;

    /**
     * 资产评估
     */
    @PostMapping("/assetAssessment")
    @ApiOperation(value = "资产评估")
    public CommonResult<String> assetAssessment(@RequestBody @Validated AssetAssessmentRequestVO request) {
        return CommonResult.success(creditApplicationService.createAssetAssessment(request));
    }

    /**
     * 额度申请（基于资产评估）
     */
    @PostMapping("/apply")
    @ApiOperation(value = "额度申请")
    public CommonResult<String> applyCreditAmount(@RequestBody CreditAmountApplicationVO request) {
        return CommonResult.success(creditApplicationService.applyCreditAmount(request));
    }
    /**
     * 获取资产评估详情
     */
    @GetMapping("/assetAssessment/{assessmentId}")
    @ApiOperation(value = "获取资产评估详情")
    public CommonResult<AssetAssessmentDetailVO> getAssetAssessment(@PathVariable("assessmentId") String assessmentId) {
        return CommonResult.success(creditApplicationService.getAssetAssessmentDetail(assessmentId));
    }

    /**
     * 获取当前用户的资产评估记录
     */
    @PostMapping("/assetAssessment/page")
    @ApiOperation(value = "分页查询资产评估记录")
    public CommonResult<IPage<AssetAssessmentPageVO>> pageAssetAssessment(@RequestBody AssetAssessmentPageRequest pageRequest) {
        return CommonResult.success(creditApplicationService.pageAssetAssessment(pageRequest));
    }

    /**
     * 获取当前用户最新的资产评估记录
     */
    @GetMapping("/assetAssessment/latest")
    @ApiOperation(value = "获取当前用户最新的资产评估记录")
    public CommonResult<AssetAssessmentPageVO> getLatestAssetAssessment() {
        return CommonResult.success(creditApplicationService.getLatestAssetAssessment());
    }

    /**
     * 检查用户是否可以申请额度
     */
    @GetMapping("/canApply")
    @ApiOperation(value = "检查是否可以申请额度")
    public CommonResult<CreditApplicationStatusVO> canApply() {
        return CommonResult.success(creditApplicationService.checkUserCanApply());
    }

    /**
     * 分页查询授信申请列表
     */
    @PostMapping("/page")
    @ApiOperation(value = "分页查询授信申请列表")
    public CommonResult<CommonPage<CreditApplicationPage>> page(@RequestBody CreditApplicationPageInfo pageInfo) {
        return CommonResult.success(CommonPage.restPage(creditApplicationService.pageInfo(pageInfo)));
    }

    /**
     * 获取授信申请详情
     */
    @GetMapping("/get")
    @ApiOperation(value = "获取授信申请详情")
    public CommonResult<CreditApplicationPage> get(@RequestParam("id") String id) {
        return CommonResult.success(creditApplicationService.get(id));
    }
    @PostMapping("/getCurrentUser/credit")
    @ApiOperation(value = "获取当前登录人授信申请")
    public CommonResult<CreditApplicationPage> getCurrentUser() {
        return CommonResult.success(creditApplicationService.getByCurrentUser());
    }

    /**
     * 审核社员授信申请
     */
    @PostMapping("/audit")
    @ApiOperation(value = "审核社员授信申请")
    public CommonResult<Boolean> cooperativeAuditEmployeeCredit(@RequestBody CooperativeCreditAuditVO request) {
        return CommonResult.success(creditApplicationService.cooperativeAuditEmployeeCredit(request));
    }

    /**
     * 获取社员审核历史记录
     */
    @PostMapping("/audit/history")
    @ApiOperation(value = "获取社员申请审核历史记录")
    public CommonResult<List<CreditApplicationAuditHistory>> getAuditHistory(@RequestBody AuditStatusRequest auditStatusRequest) {
        return CommonResult.success(creditApplicationService.getAuditHistory(auditStatusRequest));
    }
}
