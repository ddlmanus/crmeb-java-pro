package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "交易记录", description = "交易记录")
public class TransactionRecordVO {

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户账号")
    private String userAccount;

    @ApiModelProperty(value = "交易类型：1-借款，2-还款，3-消费")
    private Integer transactionType;

    @ApiModelProperty(value = "交易金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "交易前余额")
    private BigDecimal balanceBefore;

    @ApiModelProperty(value = "交易后余额")
    private BigDecimal balanceAfter;

    @ApiModelProperty(value = "交易描述")
    private String description;

    @ApiModelProperty(value = "交易时间")
    private Date transactionTime;

    @ApiModelProperty(value = "交易时间格式化")
    private String transactionTimeClone;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
} 