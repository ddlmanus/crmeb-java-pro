package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 销售趋势响应对象
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
@ApiModel(value = "SalesTrendResponse对象", description = "销售趋势响应对象")
public class SalesTrendResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "日期列表")
    private List<String> dates;

    @ApiModelProperty(value = "订单数据")
    private List<Integer> orderData;

    @ApiModelProperty(value = "销售额数据")
    private List<BigDecimal> salesData;

    @ApiModelProperty(value = "新增用户数据")
    private List<Integer> newUserData;

    @ApiModelProperty(value = "访问量数据")
    private List<Integer> visitData;

    @ApiModelProperty(value = "转化率数据")
    private List<BigDecimal> conversionRateData;

    @ApiModelProperty(value = "客单价数据")
    private List<BigDecimal> avgOrderValueData;

    @ApiModelProperty(value = "商品销量数据")
    private List<Integer> productSalesData;

    @ApiModelProperty(value = "时间范围类型: 7d, 30d, 90d")
    private String timeRange;

    @ApiModelProperty(value = "数据汇总信息")
    private SalesTrendSummary summary;

    @Data
    @ApiModel(value = "SalesTrendSummary", description = "销售趋势汇总信息")
    public static class SalesTrendSummary implements Serializable {
        @ApiModelProperty(value = "总订单数")
        private Integer totalOrders;

        @ApiModelProperty(value = "总销售额")
        private BigDecimal totalSales;

        @ApiModelProperty(value = "总新增用户")
        private Integer totalNewUsers;

        @ApiModelProperty(value = "总访问量")
        private Integer totalVisits;

        @ApiModelProperty(value = "平均转化率")
        private BigDecimal avgConversionRate;

        @ApiModelProperty(value = "平均客单价")
        private BigDecimal avgOrderValue;

        @ApiModelProperty(value = "销售增长率(与上期对比)")
        private BigDecimal salesGrowthRate;

        @ApiModelProperty(value = "订单增长率(与上期对比)")
        private BigDecimal orderGrowthRate;
    }
} 