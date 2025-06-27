package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 养殖场评估价值配置VO
 */
@Data
@ApiModel(value = "FarmValuationConfigVO", description = "养殖场评估价值配置")
public class FarmValuationConfigVO {

    @ApiModelProperty(value = "配置ID")
    private Integer id;

    @ApiModelProperty(value = "养殖场ID")
    private Integer farmId;

    @ApiModelProperty(value = "养殖场名称", required = true)
    @NotBlank(message = "养殖场名称不能为空")
    private String farmName;

    @ApiModelProperty(value = "养殖场编码", required = true)
    @NotBlank(message = "养殖场编码不能为空")
    private String farmCode;

    @ApiModelProperty(value = "养殖品种名称", required = true)
    @NotBlank(message = "养殖品种名称不能为空")
    private String breedingName;

    @ApiModelProperty(value = "品种类型", required = true)
    @NotBlank(message = "品种类型不能为空")
    private String breedingType;

    @ApiModelProperty(value = "品种编码")
    private String breedingCode;

    @ApiModelProperty(value = "单位名称", required = true)
    @NotBlank(message = "单位名称不能为空")
    private String unitName;

    @ApiModelProperty(value = "单价（元）", required = true)
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.01", message = "单价必须大于0")
    private BigDecimal unitPrice;



    @ApiModelProperty(value = "状态 0-禁用 1-启用")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;
} 