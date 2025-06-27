package com.zbkj.common.request.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 养殖场机构信息请求对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="FarmInstitutionRequest对象", description="养殖场机构信息请求对象")
public class FarmInstitutionRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "机构ID")
    private String id;

    @ApiModelProperty(value = "所属机构ID")
    private String organizationId;
    @ApiModelProperty(value = "机构标识代码")
    @NotEmpty(message = "机构标识代码不能为空")
    @Length(max = 50, message = "机构标识代码长度不能超过50个字符")
    private String farmCode;

    @ApiModelProperty(value = "养殖场名称")
    @NotEmpty(message = "养殖场名称不能为空")
    @Length(max = 100, message = "养殖场名称长度不能超过100个字符")
    private String farmName;

    @ApiModelProperty(value = "法人代表")
    @NotEmpty(message = "养殖场负责人姓名不能为空")
    @Length(max = 50, message = "养殖场负责人姓名长度不能超过50个字符")
    private String legalPerson;

    @ApiModelProperty(value = "养殖场负责人电话")
    @NotEmpty(message = "养殖场负责人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;

    @ApiModelProperty(value = "养殖场负责人姓名")
    @NotEmpty(message = "养殖场负责人姓名不能为空")
    @Length(max = 50, message = "养殖场负责人姓名长度不能超过50个字符")
    private String contactName;

    @ApiModelProperty(value = "审核状态（0:待审核 1:审核通过 2:审核拒绝）")
    private Integer auditStatus;

    @ApiModelProperty(value = "审核意见")
    @Length(max = 500, message = "审核意见长度不能超过500个字符")
    private String auditRemark;

    @ApiModelProperty(value = "养殖规模")
    private String scale;

    @ApiModelProperty(value = "注册资本（万元）")
    private BigDecimal registeredCapital;

    @ApiModelProperty(value = "省份")
    @Length(max = 50, message = "省份长度不能超过50个字符")
    private String province;

    @ApiModelProperty(value = "城市")
    @Length(max = 50, message = "城市长度不能超过50个字符")
    private String city;

    @ApiModelProperty(value = "区县")
    @Length(max = 50, message = "区县长度不能超过50个字符")
    private String district;

    @ApiModelProperty(value = "养殖场地址")
    private String address;

    @ApiModelProperty(value = "营业执照")
    private String businessLicense;

    @ApiModelProperty(value = "经营范围")
    private String businessScope;
} 