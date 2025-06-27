package com.zbkj.common.vo.finance;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 授信账单统计VO
 */
@Data
public class CreditBillStatisticsVO {
    
    /**
     * 总账单数
     */
    private Long totalBillCount = 0L;
    
    /**
     * 总支付金额
     */
    private BigDecimal totalPaymentAmount = BigDecimal.ZERO;
    
    /**
     * 总应还金额
     */
    private BigDecimal totalRepaymentAmount = BigDecimal.ZERO;
    
    /**
     * 总已还金额
     */
    private BigDecimal totalPaidAmount = BigDecimal.ZERO;
    
    /**
     * 总剩余应还金额
     */
    private BigDecimal totalRemainingAmount = BigDecimal.ZERO;
    
    /**
     * 待还款账单数
     */
    private Long pendingBillCount = 0L;
    
    /**
     * 逾期账单数
     */
    private Long overdueBillCount = 0L;
    
    /**
     * 已还清账单数
     */
    private Long paidBillCount = 0L;
    
    /**
     * 部分还款账单数
     */
    private Long partialBillCount = 0L;
    
    /**
     * 已取消账单数
     */
    private Long cancelledBillCount = 0L;
    
    /**
     * 待还款金额
     */
    private BigDecimal pendingAmount = BigDecimal.ZERO;
    
    /**
     * 逾期金额
     */
    private BigDecimal overdueAmount = BigDecimal.ZERO;
    
    /**
     * 已还清金额
     */
    private BigDecimal paidAmount = BigDecimal.ZERO;
    
    /**
     * 平均账单金额
     */
    private BigDecimal averageBillAmount = BigDecimal.ZERO;
    
    /**
     * 平均利息金额
     */
    private BigDecimal averageInterestAmount = BigDecimal.ZERO;
    
    /**
     * 还款率
     */
    private BigDecimal repaymentRate = BigDecimal.ZERO;
    
    /**
     * 逾期率
     */
    private BigDecimal overdueRate = BigDecimal.ZERO;
} 