package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 资产评估养殖品种搜索VO
 */
@Data
@ApiModel(value = "AssetAssessmentBreedingSearchVO", description = "资产评估养殖品种搜索参数")
public class AssetAssessmentBreedingSearchVO {

    @ApiModelProperty(value = "关键词（品种名称、养殖场名称）")
    private String keywords;

    @ApiModelProperty(value = "养殖场编码")
    private String farmCode;

    @ApiModelProperty(value = "养殖场名称")
    private String farmName;

    @ApiModelProperty(value = "品种名称")
    private String breedName;

    @ApiModelProperty(value = "品种类型")
    private String breedType;

    @ApiModelProperty(value = "资产评估ID")
    private String assessmentId;

    @ApiModelProperty(value = "创建时间范围")
    private String dateLimit;
} 