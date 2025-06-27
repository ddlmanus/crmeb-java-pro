package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 额度申请VO
 */
@Data
@ApiModel(value = "CreditAmountApplicationVO", description = "额度申请请求参数")
public class CreditAmountApplicationVO {
    
    /**
     * 资产评估ID（必须基于已有的资产评估）
     */
    @ApiModelProperty(value = "资产评估ID", required = true)
    @NotNull(message = "资产评估ID不能为空")
    private String assessmentId;
    
    /**
     * 申请金额
     */
    @ApiModelProperty(value = "申请金额", required = true)
    @NotNull(message = "申请金额不能为空")
    @DecimalMin(value = "0.01", message = "申请金额必须大于0")
    private BigDecimal applyAmount;

    /**
     * 申请凭证
     */
    @ApiModelProperty(value = "申请凭证", required = true)
    @NotNull(message = "申请凭证不能为空")
    private String applyImages;
} 