package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 养殖品种类型管理搜索VO
 */
@Data
@ApiModel(value = "FarmBreedTypeSearchVO", description = "养殖品种类型管理搜索条件")
public class FarmBreedTypeSearchVO {

    @ApiModelProperty(value = "关键词搜索（养殖场名称、品种名称、品种类型）")
    private String keywords;

    @ApiModelProperty(value = "养殖场编码")
    private String farmCode;

    @ApiModelProperty(value = "养殖场名称")
    private String farmName;

    @ApiModelProperty(value = "养殖品种名称")
    private String breedName;

    @ApiModelProperty(value = "养殖品种类型")
    private String breedType;

    @ApiModelProperty(value = "生长阶段")
    private String growthStage;

    @ApiModelProperty(value = "日期范围")
    private String dateLimit;
} 