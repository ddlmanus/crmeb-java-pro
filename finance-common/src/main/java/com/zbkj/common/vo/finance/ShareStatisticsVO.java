package com.zbkj.common.vo.finance;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 股份统计VO
 */
@Data
public class ShareStatisticsVO {
    
    /**
     * 总股东数
     */
    private Long totalShareholderCount = 0L;
    
    /**
     * 总股份数量
     */
    private BigDecimal totalShareAmount = BigDecimal.ZERO;
    
    /**
     * 总投资金额
     */
    private BigDecimal totalInvestmentAmount = BigDecimal.ZERO;
    
    /**
     * 正常股份数量
     */
    private Long normalShareCount = 0L;
    
    /**
     * 冻结股份数量
     */
    private Long frozenShareCount = 0L;
    
    /**
     * 已转让股份数量
     */
    private Long transferredShareCount = 0L;
    
    /**
     * 普通股数量
     */
    private Long ordinaryShareCount = 0L;
    
    /**
     * 优先股数量
     */
    private Long preferredShareCount = 0L;
    
    /**
     * 平均股份占比
     */
    private BigDecimal averageSharePercentage = BigDecimal.ZERO;
    
    /**
     * 平均投资金额
     */
    private BigDecimal averageInvestmentAmount = BigDecimal.ZERO;
    
    /**
     * 平均预期年收益率
     */
    private BigDecimal averageExpectedReturn = BigDecimal.ZERO;
} 