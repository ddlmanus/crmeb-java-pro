package com.zbkj.common.vo.finance;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 还款记录统计VO
 */
@Data
public class RepaymentStatisticsVO {
    
    /**
     * 总还款记录数
     */
    private Long totalCount = 0L;
    
    /**
     * 总还款金额
     */
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    /**
     * 待审核数量
     */
    private Long pendingCount = 0L;
    
    /**
     * 审核通过数量
     */
    private Long approvedCount = 0L;
    
    /**
     * 审核拒绝数量
     */
    private Long rejectedCount = 0L;
    
    /**
     * 待审核金额
     */
    private BigDecimal pendingAmount = BigDecimal.ZERO;
    
    /**
     * 审核通过金额
     */
    private BigDecimal approvedAmount = BigDecimal.ZERO;
    
    /**
     * 审核拒绝金额
     */
    private BigDecimal rejectedAmount = BigDecimal.ZERO;
    
    /**
     * 平均还款金额
     */
    private BigDecimal averageAmount = BigDecimal.ZERO;
    
    /**
     * 审核通过率
     */
    private BigDecimal approvalRate = BigDecimal.ZERO;
    
    /**
     * 今日还款笔数
     */
    private Long todayCount = 0L;
    
    /**
     * 今日还款金额
     */
    private BigDecimal todayAmount = BigDecimal.ZERO;
    
    /**
     * 本月还款笔数
     */
    private Long monthCount = 0L;
    
    /**
     * 本月还款金额
     */
    private BigDecimal monthAmount = BigDecimal.ZERO;
} 