package com.zbkj.admin.controller.platform;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zbkj.common.model.finance.CreditPaymentOrder;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.CreditBillPageVO;
import com.zbkj.common.vo.finance.CreditBillStatisticsVO;
import com.zbkj.common.vo.finance.CreditBillVO;
import com.zbkj.service.service.finance.CreditPaymentOrderService;
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
 * 授信账单管理后台Controller
 */
@Slf4j
@RestController
@Api(tags = "平台端 - 授信账单管理")
@RequestMapping("/api/admin/finance/credit/bill")
public class CreditBillController {

    @Autowired
    private CreditPaymentOrderService creditPaymentOrderService;

    /**
     * 分页查询授信账单列表
     */
    @PostMapping("/page")
   // @PreAuthorize("hasAuthority('finance:creditBill:page')")
    @ApiOperation(value = "分页查询授信账单列表")
    public CommonResult<IPage<CreditBillVO>> pageList(@RequestBody CreditBillPageVO pageVO) {
        try {
            IPage<CreditBillVO> result = creditPaymentOrderService.adminPageQuery(pageVO);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取授信账单详情
     */
    @GetMapping("/detail/{id}")
  //  @PreAuthorize("hasAuthority('finance:creditBill:detail')")
    @ApiOperation(value = "获取授信账单详情")
    public CommonResult<CreditBillVO> getDetail(@ApiParam(value = "账单ID") @PathVariable String id) {
        try {
            CreditBillVO result = creditPaymentOrderService.getBillDetail(id);
            if (result != null) {
                return CommonResult.success(result);
            } else {
                return CommonResult.failed("账单信息不存在");
            }
        } catch (Exception e) {
            return CommonResult.failed("获取详情失败: " + e.getMessage());
        }
    }

    /**
     * 标记账单状态
     */
    @PostMapping("/mark/{id}")
   // @PreAuthorize("hasAuthority('finance:creditBill:mark')")
    @ApiOperation(value = "标记账单状态")
    public CommonResult<Boolean> markBillStatus(
            @ApiParam(value = "账单ID") @PathVariable String id,
            @ApiParam(value = "状态：0-待还款，1-逾期，2-已还清，3-已取消") @RequestParam Integer status,
            @ApiParam(value = "备注") @RequestParam(required = false) String remark) {
        try {
            Boolean result = creditPaymentOrderService.markBillStatus(id, status, remark);
            if (result) {
                return CommonResult.success(result);
            } else {
                return CommonResult.failed("标记失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("标记失败: " + e.getMessage());
        }
    }

    /**
     * 手动还款（管理员代操作）
     */
    @PostMapping("/repayment/{id}")
  //  @PreAuthorize("hasAuthority('finance:creditBill:repayment')")
    @ApiOperation(value = "手动还款")
    public CommonResult<Boolean> manualRepayment(
            @ApiParam(value = "账单ID") @PathVariable String id,
            @ApiParam(value = "还款金额") @RequestParam String repaymentAmount,
            @ApiParam(value = "还款备注") @RequestParam(required = false) String remark) {
        try {
            Boolean result = creditPaymentOrderService.manualRepayment(id, repaymentAmount, remark);
            if (result) {
                return CommonResult.success(result);
            } else {
                return CommonResult.failed("还款失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("还款失败: " + e.getMessage());
        }
    }

    /**
     * 获取逾期账单列表
     */
    @PostMapping("/overdue")
   // @PreAuthorize("hasAuthority('finance:creditBill:overdue')")
    @ApiOperation(value = "获取逾期账单列表")
    public CommonResult<IPage<CreditBillVO>> getOverdueBills(@RequestBody CreditBillPageVO pageVO) {
        try {
            IPage<CreditBillVO> result = creditPaymentOrderService.getOverdueBills(pageVO);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的授信账单列表
     */
    @PostMapping("/user/{userId}")
    //@PreAuthorize("hasAuthority('finance:creditBill:userBills')")
    @ApiOperation(value = "获取用户的授信账单列表")
    public CommonResult<IPage<CreditBillVO>> getUserBills(
            @ApiParam(value = "用户ID") @PathVariable Integer userId,
            @RequestBody CreditBillPageVO pageVO) {
        try {
            pageVO.setUserId(userId);
            IPage<CreditBillVO> result = creditPaymentOrderService.adminPageQuery(pageVO);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取授信账单统计信息
     */
    @PostMapping("/statistics")
   // @PreAuthorize("hasAuthority('finance:creditBill:statistics')")
    @ApiOperation(value = "获取授信账单统计信息")
    public CommonResult<CreditBillStatisticsVO> getStatistics(@RequestBody CreditBillPageVO pageVO) {
        try {
            CreditBillStatisticsVO result = creditPaymentOrderService.getBillStatistics(pageVO);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("获取统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 导出授信账单数据
     */
    @PostMapping("/export")
  //  @PreAuthorize("hasAuthority('finance:creditBill:export')")
    @ApiOperation(value = "导出授信账单数据")
    public void exportData(@RequestBody CreditBillPageVO pageVO, HttpServletResponse response) {
        try {
            creditPaymentOrderService.exportBillData(pageVO, response);
        } catch (Exception e) {
            // 记录日志，但不向外抛出异常，也不向response写入内容
            // 避免返回损坏的文件
            log.error("导出授信账单数据失败", e);
        }
    }

    /**
     * 批量操作
     */
    @PostMapping("/batch")
  //  @PreAuthorize("hasAuthority('finance:creditBill:mark')")
    @ApiOperation(value = "批量操作")
    public CommonResult<Boolean> batchOperation(
            @ApiParam(value = "账单ID列表") @RequestParam List<String> ids,
            @ApiParam(value = "操作类型：mark_overdue-标记逾期，cancel-取消账单") @RequestParam String action,
            @ApiParam(value = "备注") @RequestParam(required = false) String remark) {
        try {
            Boolean result = creditPaymentOrderService.batchOperation(ids, action, remark);
            if (result) {
                return CommonResult.success(result);
            } else {
                return CommonResult.failed("批量操作失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("批量操作失败: " + e.getMessage());
        }
    }

    /**
     * 生成账单报表
     */
    @PostMapping("/report")
 //   @PreAuthorize("hasAuthority('finance:creditBill:report')")
    @ApiOperation(value = "生成账单报表")
    public CommonResult<String> generateReport(@RequestBody CreditBillPageVO pageVO) {
        try {
            String result = creditPaymentOrderService.generateBillReport(pageVO);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("生成报表失败: " + e.getMessage());
        }
    }
} 