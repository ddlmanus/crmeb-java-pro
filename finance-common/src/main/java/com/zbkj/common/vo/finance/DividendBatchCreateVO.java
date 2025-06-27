package com.zbkj.common.vo.finance;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 创建分红批次VO
 */
@Data
public class DividendBatchCreateVO {
    
    /**
     * 分红名称
     */
    private String batchName;
    
    /**
     * 分红总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 分红类型：0-年度分红，1-季度分红，2-月度分红，3-特别分红
     */
    private Integer dividendType;
    
    /**
     * 分红时间
     */
    private Date dividendTime;
    
    /**
     * 分红周期开始
     */
    private Date periodStart;
    
    /**
     * 分红周期结束
     */
    private Date periodEnd;
    
    /**
     * 备注
     */
    private String remark;
} 