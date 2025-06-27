package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 授信还款请求对象
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
@ApiModel(value = "CreditRepaymentRequest对象", description = "授信还款")
public class CreditRepaymentRequest {

    @ApiModelProperty(value = "授信支付订单号", required = true)
    @NotBlank(message = "授信支付订单号不能为空")
    private String creditOrderNo;

    @ApiModelProperty(value = "还款金额", required = true)
    @NotNull(message = "还款金额不能为空")
    @DecimalMin(value = "0.01", message = "还款金额必须大于0")
    private BigDecimal repaymentAmount;

    @ApiModelProperty(value = "还款备注")
    private String remark;
    @ApiModelProperty(value = "还款凭证")
    private String repaymentProof;
} 