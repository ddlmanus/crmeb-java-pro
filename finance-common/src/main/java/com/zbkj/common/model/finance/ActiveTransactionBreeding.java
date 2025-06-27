package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("eb_transaction_breeding")
public class ActiveTransactionBreeding extends BaseEntity {


    @ApiModelProperty(value = "活体交易记录ID")
    private String activeTransactionId;
    /**
     * 交易品种ID
     */
    @ApiModelProperty(value = "交易品种ID")
    private String breedId;
    /**
     * 交易品种名称
     */
    @ApiModelProperty(value = "交易品种名称")
    private String breedName;

    /**
     * 交易品种类型
     */
    @ApiModelProperty(value = "交易品种类型")
    private String breedType;


    /**
     * 体重
     */
    @ApiModelProperty(value = "重量")
    private String weight;
}
