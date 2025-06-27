package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 资产评估养殖品种统计VO
 */
@Data
@ApiModel(value = "AssetAssessmentBreedingStatisticsVO", description = "资产评估养殖品种统计信息")
public class AssetAssessmentBreedingStatisticsVO {

    @ApiModelProperty(value = "总记录数")
    private Long totalCount;

    @ApiModelProperty(value = "总存栏数量")
    private Long totalStock;

    @ApiModelProperty(value = "总评估价值")
    private BigDecimal totalValue;

    @ApiModelProperty(value = "平均单价")
    private BigDecimal averagePrice;

    @ApiModelProperty(value = "养殖场数量")
    private Long farmCount;

    @ApiModelProperty(value = "品种数量")
    private Long breedCount;

    @ApiModelProperty(value = "最高单价")
    private BigDecimal maxPrice;

    @ApiModelProperty(value = "最低单价")
    private BigDecimal minPrice;
} 