package com.zbkj.common.thirdVO;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * 第三方商品品牌
 */
@SuppressWarnings("ALL")
@lombok.Data
public class ThirdGoodsBrand {

    @ApiModelProperty(value = "品牌id", required = true)
    private Integer id;

    @ApiModelProperty(value = "品牌名称", required = true)
    private String name;

    @ApiModelProperty(value = "品牌图标", required = true)
    private String logo;
}
