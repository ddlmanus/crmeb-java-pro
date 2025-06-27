package com.zbkj.common.vo.finance;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 授信交易记录VO
 */
@Data
public class CreditTransactionVO {
    
    /**
     * ID
     */
    private String id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 授信申请ID
     */
    private String creditApplicationId;
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 订单号
     */
    private String orderSn;
    
    /**
     * 交易类型：0-授信消费，1-还款，2-授信额度调整，3-退款
     */
    private Integer transactionType;
    
    /**
     * 交易金额
     */
    private BigDecimal transactionAmount;
    
    /**
     * 交易前可用额度
     */
    private BigDecimal beforeAvailableAmount;
    
    /**
     * 交易后可用额度
     */
    private BigDecimal afterAvailableAmount;
    
    /**
     * 利息金额
     */
    private BigDecimal interestAmount;
    
    /**
     * 交易描述
     */
    private String transactionDescription;
    
    /**
     * 交易时间
     */
    private Date transactionTime;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 交易类型文本
     */
    private String transactionTypeText;
} 