package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 热门商品排行响应对象
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
@ApiModel(value = "HotProductRankingResponse对象", description = "热门商品排行响应对象")
public class HotProductRankingResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品图片")
    private String productImage;

    @ApiModelProperty(value = "商户ID")
    private Integer merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "销售数量")
    private Integer salesCount;

    @ApiModelProperty(value = "销售额")
    private BigDecimal salesAmount;

    @ApiModelProperty(value = "浏览量")
    private Integer pageViews;

    @ApiModelProperty(value = "收藏量")
    private Integer collectCount;

    @ApiModelProperty(value = "评价数量")
    private Integer reviewCount;

    @ApiModelProperty(value = "商品评分")
    private BigDecimal averageRating;

    @ApiModelProperty(value = "转化率")
    private BigDecimal conversionRate;

    @ApiModelProperty(value = "商品价格")
    private BigDecimal price;

    @ApiModelProperty(value = "库存数量")
    private Integer stock;

    @ApiModelProperty(value = "商品分类")
    private String categoryName;

    @ApiModelProperty(value = "排名")
    private Integer ranking;

    @ApiModelProperty(value = "销量占比百分比")
    private BigDecimal salesPercentage;

    @ApiModelProperty(value = "热度指数(综合评分)")
    private BigDecimal hotIndex;

    @ApiModelProperty(value = "上升/下降趋势(与昨日对比)")
    private Integer trendDirection; // 1:上升, 0:持平, -1:下降

    @ApiModelProperty(value = "趋势变化幅度")
    private Integer trendChange;
} 