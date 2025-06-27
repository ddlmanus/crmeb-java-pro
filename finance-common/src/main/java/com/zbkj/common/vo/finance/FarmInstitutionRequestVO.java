package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 养殖场机构信息请求VO
 */
@Data
@ApiModel(value = "FarmInstitutionRequestVO", description = "养殖场机构信息请求参数")
public class FarmInstitutionRequestVO {

    @ApiModelProperty(value = "养殖场ID")
    private Integer id;

    @ApiModelProperty(value = "所属机构ID", required = true)
    @NotBlank(message = "所属机构不能为空")
    private String organizationId;

    @ApiModelProperty(value = "养殖场编码", required = true)
    @NotBlank(message = "养殖场编码不能为空")
    private String farmCode;

    @ApiModelProperty(value = "养殖场名称", required = true)
    @NotBlank(message = "养殖场名称不能为空")
    private String farmName;

    @ApiModelProperty(value = "法人代表")
    private String legalPerson;

    @ApiModelProperty(value = "负责人姓名")
    private String contactName;

    @ApiModelProperty(value = "负责人电话")
    private String contactPhone;

    @ApiModelProperty(value = "养殖规模")
    private String scale;

    @ApiModelProperty(value = "注册资本（万元）")
    private BigDecimal registeredCapital;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "区县")
    private String district;

    @ApiModelProperty(value = "养殖场地址")
    private String address;

    @ApiModelProperty(value = "营业执照")
    private String businessLicense;

    @ApiModelProperty(value = "经营范围")
    private String businessScope;
} 