package com.zbkj.common.vo.finance;

import lombok.Data;

/**
 * 还款记录分页查询VO
 */
@Data
public class RepaymentPageVO {
    
    /**
     * 页码
     */
    private Integer pageNumber = 1;
    
    /**
     * 每页数量
     */
    private Integer pageSize = 10;
    
    /**
     * 页码（兼容前端page参数）
     */
    private Integer page;
    
    /**
     * 每页数量（兼容前端limit参数）
     */
    private Integer limit;
    
    /**
     * 关键词（用户名称或还款记录ID）
     */
    private String keywords;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 还款状态：0-待审核，1-已通过，2-已拒绝
     */
    private Integer repaymentStatus;
    
    /**
     * 还款方式：0-银行转账，1-现金，2-其他
     */
    private Integer repaymentMethod;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
} 