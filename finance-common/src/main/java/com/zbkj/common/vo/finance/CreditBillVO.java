package com.zbkj.common.vo.finance;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 授信账单VO
 */
@Data
public class CreditBillVO {
    
    /**
     * ID
     */
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 原始订单号
     */
    private String originalOrderNo;
    
    /**
     * 授信支付订单号
     */
    private String creditOrderNo;
    
    /**
     * 授信申请ID
     */
    private String creditApplicationId;
    
    /**
     * 支付金额
     */
    private BigDecimal paymentAmount;
    
    /**
     * 订单金额（前端显示用，与paymentAmount相同）
     */
    private BigDecimal orderAmount;
    
    /**
     * 利息金额
     */
    private BigDecimal interestAmount;
    
    /**
     * 总应还金额
     */
    private BigDecimal totalRepaymentAmount;
    
    /**
     * 已还金额
     */
    private BigDecimal paidAmount;
    
    /**
     * 已还金额（前端显示用，与paidAmount相同）
     */
    private BigDecimal repaidAmount;
    
    /**
     * 剩余应还金额
     */
    private BigDecimal remainingAmount;
    
    /**
     * 授信利率
     */
    private BigDecimal creditRatio;
    
    /**
     * 还款状态：0-未还款，1-部分还款，2-已还清
     */
    private Integer repaymentStatus;
    
    /**
     * 还款状态文本
     */
    private String repaymentStatusText;
    
    /**
     * 还款期限
     */
    private Date repaymentDeadline;
    
    /**
     * 订单状态：0-待还款，1-逾期，2-已还清，3-已取消
     */
    private Integer status;
    
    /**
     * 订单状态文本
     */
    private String statusText;
    
    /**
     * 是否逾期
     */
    private Boolean isOverdue;
    
    /**
     * 逾期天数
     */
    private Integer overdueDays;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 