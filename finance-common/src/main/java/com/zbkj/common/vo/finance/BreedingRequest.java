package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



@Data
public class BreedingRequest {

    @ApiModelProperty(value = "养殖场code", required = true)
    private String farmCode;
    
    @ApiModelProperty(value = "养殖场名称", required = true)
    private String farmName;
    
    /**
     * 存栏量
     */
    @ApiModelProperty(value = "存栏量", required = true)
    private Integer stockQuantity;
    
    /**
     * 养殖品种名称
     */
    @ApiModelProperty(value = "养殖品种名称", required = true)
    private String breedName;

    /**
     * 品种类型
     */
    @ApiModelProperty(value = "品种类型")
    private String breedType;
}
