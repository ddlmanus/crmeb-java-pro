package com.zbkj.common.vo.finance;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 股份分红记录VO
 */
@Data
public class DividendRecordVO {
    
    /**
     * ID
     */
    private String id;
    
    /**
     * 分红批次号
     */
    private String batchNo;
    
    /**
     * 分红批次名称
     */
    private String batchName;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 用户股份ID
     */
    private String shareId;
    
    /**
     * 股份数量
     */
    private BigDecimal shareAmount;
    
    /**
     * 股份占比
     */
    private BigDecimal sharePercentage;
    
    /**
     * 分红金额
     */
    private BigDecimal dividendAmount;
    
    /**
     * 分红类型：0-年度分红，1-季度分红，2-月度分红，3-特别分红
     */
    private Integer dividendType;
    
    /**
     * 分红状态：0-待发放，1-已发放，2-已取消
     */
    private Integer dividendStatus;
    
    /**
     * 分红时间
     */
    private Date dividendTime;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private Date createTime;
} 