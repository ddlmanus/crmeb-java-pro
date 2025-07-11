package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 商户订单直接退款请求对象
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
@ApiModel(value = "MerchantOrderDirectRefundRequest", description = "商户订单直接退款请求对象")
public class MerchantOrderDirectRefundRequest {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单号", required = true)
    @NotEmpty(message = "订单号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "退货类型：1-整单退款，2-分单退款", required = true)
    @NotNull(message = "请选择退货类型")
    @Range(min = 1, max = 2, message = "未知的退货类型")
    private Integer returnType;

    @ApiModelProperty(value = "订单详情列表,分单退款必填")
    private List<MerchantOrderDirectRefundDetailRequest> detailList;
}
