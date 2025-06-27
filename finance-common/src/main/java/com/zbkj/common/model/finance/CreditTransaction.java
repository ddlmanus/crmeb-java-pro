package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 授信交易记录实体类
 */
@Data
@TableName("eb_credit_transaction")
public class CreditTransaction {
    
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 订单ID
     */
    private String orderSn;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 交易类型：0-授信消费，1-还款，2-授信额度调整
     */
    private Integer transactionType;
    
    /**
     * 交易金额
     */
    private BigDecimal transactionAmount;

    /**
     * 待还款金额
     */
    private BigDecimal repaymentAmount;

    /**
     * 还款总金额
     */
    private BigDecimal totalRepaymentAmount;

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
     * 删除标志
     */
    private Integer deleteFlag;


    /**
     * 组织机构ID
     */
    private String organizationId;
} 