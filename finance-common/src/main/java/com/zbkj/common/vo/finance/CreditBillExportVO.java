package com.zbkj.common.vo.finance;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 授信账单导出VO
 */
@Data
public class CreditBillExportVO {
    
    /**
     * 账单ID
     */
    @ExcelProperty("账单ID")
    private String id;
    
    /**
     * 用户名称
     */
    @ExcelProperty("用户名称")
    private String userName;
    
    /**
     * 原始订单号
     */
    @ExcelProperty("原始订单号")
    private String originalOrderNo;
    
    /**
     * 授信订单号
     */
    @ExcelProperty("授信订单号")
    private String creditOrderNo;
    
    /**
     * 订单金额
     */
    @ExcelProperty("订单金额")
    private BigDecimal paymentAmount;
    
    /**
     * 利息金额
     */
    @ExcelProperty("利息金额")
    private BigDecimal interestAmount;
    
    /**
     * 总还款金额
     */
    @ExcelProperty("总还款金额")
    private BigDecimal totalRepaymentAmount;
    
    /**
     * 已还金额
     */
    @ExcelProperty("已还金额")
    private BigDecimal paidAmount;
    
    /**
     * 剩余金额
     */
    @ExcelProperty("剩余金额")
    private BigDecimal remainingAmount;
    
    /**
     * 还款状态
     */
    @ExcelProperty("还款状态")
    private String repaymentStatus;
    
    /**
     * 账单状态
     */
    @ExcelProperty("账单状态")
    private String status;
    
    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    private String createTime;
    
    /**
     * 备注
     */
    @ExcelProperty("备注")
    private String remark;
} 