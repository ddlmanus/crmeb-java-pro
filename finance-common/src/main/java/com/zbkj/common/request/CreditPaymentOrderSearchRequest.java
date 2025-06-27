package com.zbkj.common.request;

import com.zbkj.common.request.PageParamRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 授信支付订单查询请求对象
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
@ApiModel(value = "CreditPaymentOrderSearchRequest对象", description = "授信支付订单查询")
public class CreditPaymentOrderSearchRequest extends PageParamRequest {

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "原始订单号")
    private String originalOrderNo;

    @ApiModelProperty(value = "授信支付订单号")
    private String creditOrderNo;

    @ApiModelProperty(value = "还款状态：0-未还款，1-部分还款，2-已还清")
    private Integer repaymentStatus;

    @ApiModelProperty(value = "订单状态：0-待还款，1-逾期，2-已还清，3-已取消")
    private Integer status;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
} 