package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 担保人VO对象
 */
@Data
@ApiModel(value = "担保人信息", description = "担保人信息")
public class GuarantorVO {

    @ApiModelProperty(value = "ID")
    private String id;
    @ApiModelProperty(value = "员工ID")
    private Integer userId;
    @ApiModelProperty(value = "担保人姓名")
    private String name;

    @ApiModelProperty(value = "担保人手机号")
    private String mobile;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "电子签名")
    private String signature;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
} 