package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 养殖场评估价值配置搜索VO
 */
@Data
@ApiModel(value = "FarmValuationConfigSearchVO", description = "养殖场评估价值配置搜索")
public class FarmValuationConfigSearchVO {

    @ApiModelProperty(value = "养殖品种")
    private String breedingType;

    @ApiModelProperty(value = "品种编码")
    private String breedingCode;

    @ApiModelProperty(value = "状态 0-禁用 1-启用")
    private Integer status;

    @ApiModelProperty(value = "关键词搜索（品种名称、编码）")
    private String keywords;

    @ApiModelProperty(value = "时间范围")
    private String dateLimit;
} 