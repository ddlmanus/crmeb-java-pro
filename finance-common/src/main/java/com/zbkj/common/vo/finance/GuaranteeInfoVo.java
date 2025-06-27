package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 担保人信息
 */
@Data
public class GuaranteeInfoVo {
    @ApiModelProperty(value = "担保人名称")
    private String name;

    @ApiModelProperty(value = "担保人身份证号")
    private String idNumber;

    @ApiModelProperty(value = "担保人联系电话")
    private String phone;
    @ApiModelProperty(value = "担保人地址信息")
    private String address;

    @ApiModelProperty(value = "担保人签名图片URL")
    private String signature;
}
