package com.zbkj.common.vo.finance;

import lombok.Data;

/**
 * 授信账单分页查询VO
 */
@Data
public class CreditBillPageVO {
    
    /**
     * 页码
     */
    private Integer pageNumber = 1;
    
    /**
     * 每页数量
     */
    private Integer pageSize = 10;
    
    /**
     * 页码（兼容前端）
     */
    private Integer page;
    
    /**
     * 每页数量（兼容前端）
     */
    private Integer limit;
    
    /**
     * 关键词（用户名称或订单号）
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
     * 原始订单号
     */
    private String originalOrderNo;
    
    /**
     * 授信支付订单号
     */
    private String creditOrderNo;
    
    /**
     * 还款状态：0-未还款，1-部分还款，2-已还清
     */
    private Integer repaymentStatus;
    
    /**
     * 订单状态：0-待还款，1-逾期，2-已还清，3-已取消
     */
    private Integer status;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
    
    /**
     * 最小金额
     */
    private String minAmount;
    
    /**
     * 最大金额
     */
    private String maxAmount;
} 