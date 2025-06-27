package com.zbkj.common.vo.finance;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 分红批次VO
 */
@Data
public class DividendBatchVO {
    
    /**
     * ID
     */
    private String id;
    
    /**
     * 批次号
     */
    private String batchNo;
    
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
     * 分红状态：0-未分配，1-已分配，2-已发放，3-已取消
     */
    private Integer batchStatus;
    
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
     * 发起人ID
     */
    private String initiatorId;
    
    /**
     * 发起人名称
     */
    private String initiatorName;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private Date createTime;
} 