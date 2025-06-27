package com.zbkj.common.vo.finance;

import lombok.Data;

/**
 * 授信交易记录分页查询VO
 */
@Data
public class CreditTransactionPageVO {
    
    /**
     * 页码
     */
    private Integer  page= 1;
    
    /**
     * 每页数量
     */
    private Integer limit = 10;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 交易类型：0-授信消费，1-还款，2-授信额度调整
     */
    private Integer transactionType;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 交易订单号
     */
    private String orderSn;
} 