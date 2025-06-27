package com.zbkj.common.vo.finance;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 分红统计VO
 */
@Data
public class DividendStatisticsVO {
    
    /**
     * 总分红次数
     */
    private Long totalDividendCount = 0L;
    
    /**
     * 总分红金额
     */
    private BigDecimal totalDividendAmount = BigDecimal.ZERO;
    
    /**
     * 草稿状态数量
     */
    private Long draftCount = 0L;
    
    /**
     * 已发布数量
     */
    private Long publishedCount = 0L;
    
    /**
     * 已完成数量
     */
    private Long completedCount = 0L;
    
    /**
     * 草稿状态金额
     */
    private BigDecimal draftAmount = BigDecimal.ZERO;
    
    /**
     * 已发布金额
     */
    private BigDecimal publishedAmount = BigDecimal.ZERO;
    
    /**
     * 已完成金额
     */
    private BigDecimal completedAmount = BigDecimal.ZERO;
    
    /**
     * 平均分红金额
     */
    private BigDecimal averageDividendAmount = BigDecimal.ZERO;
    
    /**
     * 参与分红人数（去重）
     */
    private Long participantCount = 0L;
    
    /**
     * 完成率
     */
    private BigDecimal completionRate = BigDecimal.ZERO;
} 