package com.zbkj.common.response;

import com.zbkj.common.vo.PreOrderInfoVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付配置响应对象
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
@ApiModel(value = "PayConfigResponse对象", description = "支付配置响应对象")
public class PayConfigResponse implements Serializable {

    private static final long serialVersionUID = 7282892323898493847L;

    @ApiModelProperty(value = "余额支付")
    private Boolean yuePayStatus;

    @ApiModelProperty(value = "微信支付")
    private Boolean payWechatOpen;

    @ApiModelProperty(value = "支付宝支付")
    private Boolean aliPayStatus;

    @ApiModelProperty(value = "授信支付")
    private Boolean creditPayStatus;

    @ApiModelProperty(value = "用户余额")
    private BigDecimal userBalance;

    @ApiModelProperty(value = "用户可用授信额度")
    private BigDecimal availableCreditAmount;

    @ApiModelProperty(value = "用户已使用授信额度")
    private BigDecimal usedCreditAmount;

    @ApiModelProperty(value = "用户总待还款金额")
    private BigDecimal totalPendingRepaymentAmount;
}
