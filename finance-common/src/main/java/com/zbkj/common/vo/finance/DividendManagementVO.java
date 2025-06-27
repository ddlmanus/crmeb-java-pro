package com.zbkj.common.vo.finance;

import com.zbkj.common.model.finance.DividendDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 分红管理VO
 */
@Data
public class DividendManagementVO {
    
    /**
     * ID
     */
    private String id;
    
    /**
     * 分红标题
     */
    private String dividendTitle;
    
    /**
     * 分红总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 分红日期
     */
    private Date dividendDate;

    /**
     * 分红明细列表
     */
    private List<DividendDetail> dividendDetails;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 删除标志
     */
    private Integer deleteFlag;
} 