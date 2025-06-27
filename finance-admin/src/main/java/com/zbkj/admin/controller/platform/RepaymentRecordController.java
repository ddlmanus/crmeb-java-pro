package com.zbkj.admin.controller.platform;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.RepaymentAuditVO;
import com.zbkj.common.vo.finance.RepaymentPageVO;
import com.zbkj.common.vo.finance.RepaymentRecordVO;
import com.zbkj.common.vo.finance.RepaymentStatisticsVO;
import com.zbkj.service.service.finance.RepaymentRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 还款记录管理后台Controller
 */
@Slf4j
@RestController
@Api(tags = "平台端 - 还款记录管理")
@RequestMapping("/api/admin/finance/repayment")
public class RepaymentRecordController {

    @Autowired
    private RepaymentRecordService repaymentRecordService;

    /**
     * 分页查询还款记录列表
     */
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('finance:repayment:page')")
    @ApiOperation(value = "分页查询还款记录列表")
    public CommonResult<IPage<RepaymentRecordVO>> pageList(@RequestBody RepaymentPageVO pageVO) {
        try {
            IPage<RepaymentRecordVO> result = repaymentRecordService.adminPageQuery(pageVO);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取还款记录详情
     */
    @GetMapping("/detail/{id}")
    @PreAuthorize("hasAuthority('finance:repayment:detail')")
    @ApiOperation(value = "获取还款记录详情")
    public CommonResult<RepaymentRecordVO> getDetail(@ApiParam(value = "还款记录ID") @PathVariable String id) {
        try {
            RepaymentRecordVO result = repaymentRecordService.getRepaymentDetail(id);
            if (result != null) {
                return CommonResult.success(result);
            } else {
                return CommonResult.failed("还款记录不存在");
            }
        } catch (Exception e) {
            return CommonResult.failed("获取详情失败: " + e.getMessage());
        }
    }

    /**
     * 审核还款记录
     */
    @PostMapping("/audit")
    @PreAuthorize("hasAuthority('finance:repayment:audit')")
    @ApiOperation(value = "审核还款记录")
    public CommonResult<Boolean> auditRepayment(@RequestBody RepaymentAuditVO auditVO) {
        try {
            Boolean result = repaymentRecordService.auditRepayment(auditVO);
            if (result) {
                return CommonResult.success(result);
            } else {
                return CommonResult.failed("审核失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("审核失败: " + e.getMessage());
        }
    }

    /**
     * 批量审核还款记录
     */
    @PostMapping("/batch/audit")
    @PreAuthorize("hasAuthority('finance:repayment:batchAudit')")
    @ApiOperation(value = "批量审核还款记录")
    public CommonResult<Boolean> batchAudit(
            @ApiParam(value = "还款记录ID列表") @RequestParam List<String> ids,
            @ApiParam(value = "审核状态：1-通过，2-拒绝") @RequestParam Integer status,
            @ApiParam(value = "审核备注") @RequestParam(required = false) String auditRemark) {
        try {
            Boolean result = repaymentRecordService.batchAudit(ids, status, auditRemark);
            if (result) {
                return CommonResult.success(result);
            } else {
                return CommonResult.failed("批量审核失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("批量审核失败: " + e.getMessage());
        }
    }

    /**
     * 获取还款统计信息
     */
    @PostMapping("/statistics")
    @PreAuthorize("hasAuthority('finance:repayment:statistics')")
    @ApiOperation(value = "获取还款统计信息")
    public CommonResult<RepaymentStatisticsVO> getStatistics(@RequestBody RepaymentPageVO pageVO) {
        try {
            RepaymentStatisticsVO result = repaymentRecordService.getRepaymentStatistics(pageVO);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("获取统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 导出还款记录数据
     */
    @PostMapping("/export")
   // @PreAuthorize("hasAuthority('finance:repayment:export')")
    @ApiOperation(value = "导出还款记录数据")
    public void exportData(@RequestBody RepaymentPageVO pageVO, HttpServletResponse response) {
        try {
            log.info("开始导出还款记录数据，参数：{}", pageVO);
            repaymentRecordService.exportRepaymentData(pageVO, response);
            log.info("还款记录数据导出完成");
        } catch (Exception e) {
            log.error("导出还款记录数据失败", e);
            // 发生异常时，不再向response写入内容，
            // 前端会因为无法正确解析blob而进入catch块，提示用户导出失败
        }
    }

    /**
     * 根据授信订单号查询还款记录
     */
    @GetMapping("/credit/{creditOrderNo}")
    @PreAuthorize("hasAuthority('finance:repayment:detail')")
    @ApiOperation(value = "根据授信订单号查询还款记录")
    public CommonResult<List<RepaymentRecordVO>> getRepaymentsByCreditOrder(
            @ApiParam(value = "授信订单号") @PathVariable String creditOrderNo) {
        try {
            List<RepaymentRecordVO> result = repaymentRecordService.getRepaymentsByCreditOrder(creditOrderNo);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("查询失败: " + e.getMessage());
        }
    }
} 