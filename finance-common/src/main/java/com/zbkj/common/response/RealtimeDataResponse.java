package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 实时数据流响应对象
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
@ApiModel(value = "RealtimeDataResponse对象", description = "实时数据流响应对象")
public class RealtimeDataResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "在线用户数")
    private Integer onlineUsers;

    @ApiModelProperty(value = "活跃商户数")
    private Integer activeMerchants;

    @ApiModelProperty(value = "实时订单数")
    private Integer realtimeOrders;

    @ApiModelProperty(value = "支付成功率")
    private BigDecimal paymentSuccessRate;

    @ApiModelProperty(value = "今日访问量")
    private Integer todayVisits;

    @ApiModelProperty(value = "今日新增用户")
    private Integer todayNewUsers;

    @ApiModelProperty(value = "今日销售额")
    private BigDecimal todaySales;

    @ApiModelProperty(value = "系统负载")
    private String systemLoad;

    @ApiModelProperty(value = "在线用户趋势(与昨日同时段对比百分比)")
    private BigDecimal onlineUsersTrend;

    @ApiModelProperty(value = "活跃商户趋势(与昨日同时段对比百分比)")
    private BigDecimal activeMerchantsTrend;

    @ApiModelProperty(value = "实时订单趋势(与昨日同时段对比百分比)")
    private BigDecimal realtimeOrdersTrend;

    @ApiModelProperty(value = "支付成功率趋势(与昨日同时段对比百分比)")
    private BigDecimal paymentSuccessRateTrend;
} 