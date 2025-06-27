package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 养殖品种搜索VO
 */
@Data
public class BreedingProductSearchVO {
    
    /**
     * 关键词（品种名称、编号）
     */
    @ApiModelProperty(value = "关键词")
    private String keywords;
    
    /**
     * 品种编号
     */
    @ApiModelProperty(value = "品种编号")
    private String code;
    
    /**
     * 品种名称
     */
    @ApiModelProperty(value = "品种名称")
    private String name;
    
    /**
     * 存栏量
     */
    @ApiModelProperty(value = "存栏量")
    private Integer stockQuantity;
    
    /**
     * 日期范围
     */
    @ApiModelProperty(value = "日期范围")
    private String dateLimit;
} 