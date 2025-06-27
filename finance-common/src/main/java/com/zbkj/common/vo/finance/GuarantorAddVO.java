package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "添加担保人请求", description = "添加担保人请求")
public class GuarantorAddVO {

    @ApiModelProperty(value = "担保人姓名", required = true)
    @NotBlank(message = "担保人姓名不能为空")
    private String fullName;

    @ApiModelProperty(value = "担保人手机号", required = true)
    @NotBlank(message = "担保人手机号不能为空")
    private String mobile;

    @ApiModelProperty(value = "身份证号", required = true)
    @NotBlank(message = "身份证号不能为空")
    private String idCard;

    @ApiModelProperty(value = "电子签名")
    private String signature;
} 