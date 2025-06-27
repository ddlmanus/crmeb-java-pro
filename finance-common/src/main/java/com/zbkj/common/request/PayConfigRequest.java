package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 支付配置请求对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="PayConfigRequest", description="支付配置请求对象")
public class PayConfigRequest {

    @ApiModelProperty(value = "微信支付开关")
    @NotBlank(message = "微信支付开关不能为空")
    private String payWeixinOpen;

    @ApiModelProperty(value = "支付宝支付状态")
    @NotBlank(message = "支付宝支付状态不能为空")
    private String aliPayStatus;

    @ApiModelProperty(value = "余额支付状态")
    @NotBlank(message = "余额支付状态不能为空")
    private String yuePayStatus;

    @ApiModelProperty(value = "授信支付状态")
    @NotBlank(message = "授信支付状态不能为空")
    private String creditPayStatus;

    @ApiModelProperty(value = "微信支付来源")
    private String wechatPaySource;
} 