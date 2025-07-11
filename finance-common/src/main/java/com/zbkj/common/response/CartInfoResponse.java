package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车详情响应对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CartInfoResponse对象", description = "购物车详情响应对象")
public class CartInfoResponse implements Serializable {

    private static final long serialVersionUID = 3558884699193209193L;

    @ApiModelProperty(value = "购物车表ID")
    private Integer id;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "商品属性")
    private String productAttrUnique;

    @ApiModelProperty(value = "商品数量")
    private Integer cartNum;

    @ApiModelProperty(value = "商品图片")
    private String image;

    @ApiModelProperty(value = "商品名称")
    private String proName;

    @ApiModelProperty(value = "商品规格id")
    private Integer attrId;

    @ApiModelProperty(value = "商品属性sku")
    private String sku;

    @ApiModelProperty(value = "sku价格")
    private BigDecimal price;

    @ApiModelProperty(value = "商品是否有效")
    private Boolean attrStatus;

    @ApiModelProperty(value = "sku库存")
    private Integer stock;

    @ApiModelProperty(value = "是否付费会员商品")
    private Boolean isPaidMember = false;

    @ApiModelProperty(value = "会员价格")
    private BigDecimal vipPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "配送方式：1-商家配送，2-到店核销")
    private String deliveryMethod;
}
