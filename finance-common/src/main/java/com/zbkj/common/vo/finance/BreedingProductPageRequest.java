package com.zbkj.common.vo.finance;

import com.zbkj.common.request.PageParamRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 养殖品种分页查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "BreedingProductPageRequest", description = "养殖品种分页查询请求")
public class BreedingProductPageRequest extends PageParamRequest {
    
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
     * 日期范围
     */
    @ApiModelProperty(value = "日期范围")
    private String dateLimit;
} 