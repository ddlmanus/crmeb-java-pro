package com.zbkj.common.vo.finance;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 还款记录导出VO
 */
@Data
public class RepaymentRecordExportVO {
    
    /**
     * 记录ID
     */
    @ExcelProperty("记录ID")
    private String id;
    
    /**
     * 用户名称
     */
    @ExcelProperty("用户名称")
    private String userName;
    
    /**
     * 授信订单号
     */
    @ExcelProperty("授信订单号")
    private String creditOrderNo;
    
    /**
     * 原始订单号
     */
    @ExcelProperty("原始订单号")
    private String originalOrderNo;
    
    /**
     * 还款金额
     */
    @ExcelProperty("还款金额")
    private BigDecimal repaymentAmount;
    
    /**
     * 还款方式
     */
    @ExcelProperty("还款方式")
    private String repaymentMethod;
    
    /**
     * 还款状态
     */
    @ExcelProperty("还款状态")
    private String repaymentStatus;
    
    /**
     * 还款时间
     */
    @ExcelProperty("还款时间")
    private String repaymentTime;
    
    /**
     * 还款凭证
     */
    @ExcelProperty("还款凭证")
    private String repaymentProof;
    
    /**
     * 审核人
     */
    @ExcelProperty("审核人")
    private String auditorName;
    
    /**
     * 审核时间
     */
    @ExcelProperty("审核时间")
    private String auditTime;
    
    /**
     * 审核备注
     */
    @ExcelProperty("审核备注")
    private String auditRemark;
    
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