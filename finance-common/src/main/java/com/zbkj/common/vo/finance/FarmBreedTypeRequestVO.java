package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 养殖品种类型管理请求VO
 */
@Data
@ApiModel(value = "FarmBreedTypeRequestVO", description = "养殖品种类型管理请求")
public class FarmBreedTypeRequestVO {

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "养殖场名称", required = true)
    @NotBlank(message = "养殖场名称不能为空")
    private String farmName;

    @ApiModelProperty(value = "养殖场ID")
    private String farmId;

    @ApiModelProperty(value = "养殖场code", required = true)
    @NotBlank(message = "养殖场code不能为空")
    private String farmCode;

    @ApiModelProperty(value = "养殖品种名称", required = true)
    @NotBlank(message = "养殖品种名称不能为空")
    private String breedName;

    @ApiModelProperty(value = "养殖品种ID")
    private String breedId;

    @ApiModelProperty(value = "养殖品种类型", required = true)
    @NotBlank(message = "养殖品种类型不能为空")
    private String breedType;

    @ApiModelProperty(value = "存栏量", required = true)
    @NotNull(message = "存栏量不能为空")
    private Integer stockQuantity;

    @ApiModelProperty(value = "生长阶段")
    private String growthStage;
} 