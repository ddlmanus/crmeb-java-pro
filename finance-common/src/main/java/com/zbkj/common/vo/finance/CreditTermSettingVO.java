package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(value = "授信条件设置", description = "授信条件设置")
public class CreditTermSettingVO {

    @ApiModelProperty(value = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Integer id;

    @ApiModelProperty(value = "贷款利率", required = true)
    @NotNull(message = "贷款利率不能为空")
    private BigDecimal premium;

    @ApiModelProperty(value = "还款期限（天）", required = true)
    @NotNull(message = "还款期限不能为空")
    private Integer termDays;
} 