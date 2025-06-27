package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 财务概览响应对象
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
@ApiModel(value = "FinanceOverviewResponse对象", description = "财务概览响应对象")
public class FinanceOverviewResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "今日收入")
    private BigDecimal todayIncome;

    @ApiModelProperty(value = "昨日收入")
    private BigDecimal yesterdayIncome;

    @ApiModelProperty(value = "本月收入")
    private BigDecimal monthIncome;

    @ApiModelProperty(value = "上月收入")
    private BigDecimal lastMonthIncome;

    @ApiModelProperty(value = "今年收入")
    private BigDecimal yearIncome;

    @ApiModelProperty(value = "去年收入")
    private BigDecimal lastYearIncome;

    @ApiModelProperty(value = "待结算金额")
    private BigDecimal pendingSettlement;

    @ApiModelProperty(value = "已结算金额")
    private BigDecimal settledAmount;

    @ApiModelProperty(value = "冻结金额")
    private BigDecimal frozenAmount;

    @ApiModelProperty(value = "退款金额(今日)")
    private BigDecimal todayRefund;

    @ApiModelProperty(value = "佣金支出(今日)")
    private BigDecimal todayCommission;

    @ApiModelProperty(value = "平台服务费(今日)")
    private BigDecimal todayPlatformFee;

    @ApiModelProperty(value = "净收入(今日)")
    private BigDecimal todayNetIncome;

    @ApiModelProperty(value = "交易手续费(今日)")
    private BigDecimal todayTransactionFee;

    @ApiModelProperty(value = "收入增长率(与昨日对比)")
    private BigDecimal incomeGrowthRate;

    @ApiModelProperty(value = "本月增长率(与上月对比)")
    private BigDecimal monthGrowthRate;
} 