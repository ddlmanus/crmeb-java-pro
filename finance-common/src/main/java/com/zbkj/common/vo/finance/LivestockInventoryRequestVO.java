package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 牧码通存栏数据查询请求VO
 */
@Data
@ApiModel(value = "LivestockInventoryRequestVO", description = "牧码通存栏数据查询请求")
public class LivestockInventoryRequestVO {

    @ApiModelProperty(value = "养殖场编码", required = true)
    @NotBlank(message = "养殖场编码不能为空")
    private String farmCode;
} 