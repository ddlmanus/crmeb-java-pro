package com.zbkj.admin.controller.platform;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.finance.CreditApplicationAuditHistory;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.*;
import com.zbkj.service.service.finance.CreditApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * 平台端授信申请管理控制器
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
@RequestMapping("api/admin/platform/credit/application")
@Api(tags = "平台端授信申请管理控制器")
@Validated
public class CreditApplicationController {

    @Autowired
    private CreditApplicationService creditApplicationService;

    /**
     * 分页查询授信申请列表
     */
  //  @PreAuthorize("hasAuthority('platform:credit:application:list')")
    @ApiOperation(value = "分页查询授信申请列表", notes = "支持多条件查询，包括审核状态、用户类型、姓名、手机号等")
    @PostMapping("/page")
    public CommonResult<CommonPage<CreditApplicationPage>> pageInfo(
            @ApiParam(value = "查询参数") @RequestBody @Valid CreditApplicationPageInfo pageInfo) {
        try {
            log.info("分页查询授信申请列表，查询参数：{}", pageInfo);
            PageInfo<CreditApplicationPage> result = creditApplicationService.pageAdminInfo(pageInfo);
            return CommonResult.success(CommonPage.restPage( result));
        } catch (Exception e) {
            log.error("分页查询授信申请列表失败", e);
            return CommonResult.failed("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取授信申请详情
     */
  //  @PreAuthorize("hasAuthority('platform:credit:application:info')")
    @ApiOperation(value = "获取授信申请详情", notes = "根据申请ID获取详细信息")
    @GetMapping("/info/{id}")
    public CommonResult<CreditApplicationPage> getInfo(
            @ApiParam(value = "申请ID", required = true) 
            @PathVariable("id") @NotBlank(message = "申请ID不能为空") String id) {
        try {
            log.info("获取授信申请详情，申请ID：{}", id);
            CreditApplicationPage result = creditApplicationService.get(id);
            if (result == null) {
                log.warn("未找到申请记录，申请ID：{}", id);
                return CommonResult.failed("未找到对应的申请记录");
            }
            log.info("获取授信申请详情成功，申请ID：{}", id);
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("获取授信申请详情失败，申请ID：{}", id, e);
            return CommonResult.failed("获取详情失败：" + e.getMessage());
        }
    }

    /**
     * 审核授信申请
     */
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "审核授信申请")
 //   @PreAuthorize("hasAuthority('platform:credit:application:audit')")
    @ApiOperation(value = "审核授信申请", notes = "对授信申请进行审核，支持通过和拒绝")
    @PostMapping("/audit")
    public CommonResult<Boolean> audit(
            @ApiParam(value = "审核请求参数", required = true)
            @RequestBody @Validated CreditApplicationAuditRequest request) {
        try {
            log.info("开始审核授信申请，审核参数：{}", request);
            Boolean result = creditApplicationService.auditCreditApplication(request);
            if (result) {
                log.info("授信申请审核成功，申请ID：{}，审核结果：{}", request.getId(), request.getAuditStatus());
                return CommonResult.success(true);
            } else {
                log.warn("授信申请审核失败，申请ID：{}", request.getId());
                return CommonResult.failed("审核失败");
            }
        } catch (Exception e) {
            log.error("审核授信申请异常，申请ID：{}", request.getId(), e);
            return CommonResult.failed("审核失败：" + e.getMessage());
        }
    }

    /**
     * 获取授信申请统计信息
     */
   // @PreAuthorize("hasAuthority('platform:credit:application:statistics')")
    @ApiOperation(value = "获取授信申请统计信息", notes = "获取各种状态的申请数量和金额统计")
    @GetMapping("/statistics")
    public CommonResult<CreditApplicationStatistics> getStatistics() {
        try {
            log.info("获取授信申请统计信息");
            CreditApplicationStatistics statistics = creditApplicationService.getStatistics();
            log.info("获取统计信息成功：总申请数={}, 待审核={}, 已通过={}, 已拒绝={}", 
                statistics.getTotalCount(), statistics.getPendingCount(), 
                statistics.getApprovedCount(), statistics.getRejectedCount());
            return CommonResult.success(statistics);
        } catch (Exception e) {
            log.error("获取授信申请统计信息失败", e);
            return CommonResult.failed("获取统计信息失败：" + e.getMessage());
        }
    }

    /**
     * 导出授信申请列表
     */
  //  @PreAuthorize("hasAuthority('platform:credit:application:export')")
    @ApiOperation(value = "导出授信申请列表", notes = "根据查询条件导出授信申请数据")
    @GetMapping("/export")
    public CommonResult<String> export(
            @ApiParam(value = "导出查询参数") @Valid CreditApplicationPageInfo pageInfo) {
        try {
            log.info("导出授信申请列表，查询参数：{}", pageInfo);
            // TODO: 实现导出功能
            String downloadUrl = "导出功能暂未实现";
            log.info("导出成功，下载地址：{}", downloadUrl);
            return CommonResult.success(downloadUrl);
        } catch (Exception e) {
            log.error("导出授信申请列表失败", e);
            return CommonResult.failed("导出失败：" + e.getMessage());
        }
    }

    /**
     * 批量审核授信申请
     */
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "批量审核授信申请")
  //  @PreAuthorize("hasAuthority('platform:credit:application:batch:audit')")
    @ApiOperation(value = "批量审核授信申请", notes = "批量对多个授信申请进行审核")
    @PostMapping("/batch/audit")
    public CommonResult<Boolean> batchAudit(
            @ApiParam(value = "批量审核请求参数", required = true)
            @RequestBody @Validated List<CreditApplicationAuditRequest> requests) {
        try {
            log.info("开始批量审核授信申请，数量：{}", requests.size());
            // TODO: 实现批量审核逻辑
            Boolean result = true;
            for (CreditApplicationAuditRequest request : requests) {
                Boolean singleResult = creditApplicationService.auditCreditApplication(request);
                if (!singleResult) {
                    result = false;
                    break;
                }
            }
            if (result) {
                log.info("批量审核授信申请成功，数量：{}", requests.size());
                return CommonResult.success(true);
            } else {
                log.warn("批量审核授信申请失败");
                return CommonResult.failed("批量审核失败");
            }
        } catch (Exception e) {
            log.error("批量审核授信申请异常", e);
            return CommonResult.failed("批量审核失败：" + e.getMessage());
        }
    }

    /**
     * 获取授信申请审核历史
     */
  //  @PreAuthorize("hasAuthority('platform:credit:application:audit:history')")
    @ApiOperation(value = "获取授信申请审核历史", notes = "获取指定申请的审核历史记录")
    @GetMapping("/audit/history/{id}")
    public CommonResult<List<CreditApplicationAuditHistory>> getAuditHistory(
            @ApiParam(value = "申请ID", required = true)
            @PathVariable("id") @NotBlank(message = "申请ID不能为空") String id) {
        try {
            log.info("获取授信申请审核历史，申请ID：{}", id);
            // TODO: 实现审核历史查询逻辑
            List<CreditApplicationAuditHistory> history = new ArrayList<>();
            log.info("获取审核历史成功，申请ID：{}，历史记录数：{}", id, history.size());
            return CommonResult.success(history);
        } catch (Exception e) {
            log.error("获取授信申请审核历史失败，申请ID：{}", id, e);
            return CommonResult.failed("获取审核历史失败：" + e.getMessage());
        }
    }
}