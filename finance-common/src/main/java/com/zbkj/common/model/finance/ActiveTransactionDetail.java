package com.zbkj.common.model.finance;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("eb_active_transaction_detail")
public class ActiveTransactionDetail extends BaseEntity {
    /**
     * 交易金额
     */
    @ApiModelProperty(value = "交易金额")
    private BigDecimal transactionAmount;
    /**
     * 交易类型
     */
    @ApiModelProperty(value = "交易类型")
    private Integer transactionType;

    /**
     * 银行交易流水号
     */
    @ApiModelProperty(value = "银行交易流水号")
    private String bankTransactionNo;
    /**
     * 交易时间
     */
    @ApiModelProperty(value = "交易时间")
    private String transactionTime;
    /**
     * 单价
     */
    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;
    /**
     * 交易数量
     */
    @ApiModelProperty(value = "交易数量")
    private Integer transactionQuantity;
    /**
     * 交易活体总重量
     */
    @ApiModelProperty(value = "交易活体总重量")
    private BigDecimal transactionTotalWeight;
    @ApiModelProperty(value = "活体交易记录ID")
    private String activeTransactionId;
}
