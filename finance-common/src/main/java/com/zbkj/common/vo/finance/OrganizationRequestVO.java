package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 机构管理请求VO
 */
@Data
@ApiModel(value = "OrganizationRequestVO", description = "机构管理请求参数")
public class OrganizationRequestVO {

    @ApiModelProperty(value = "机构ID", example = "1")
    private String id;

    @ApiModelProperty(value = "机构名称", required = true)
    @NotBlank(message = "机构名称不能为空")
    private String orgName;

    @ApiModelProperty(value = "机构编号", required = true)
    @NotBlank(message = "机构编号不能为空")
    private String orgCode;

    @ApiModelProperty(value = "所属区域")
    private String region;

    @ApiModelProperty(value = "省份ID")
    private Integer provinceId;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市ID")
    private Integer cityId;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "区县ID")
    private Integer districtId;

    @ApiModelProperty(value = "区县")
    private String district;

    @ApiModelProperty(value = "上级机构ID")
    private String parentOrgId;

    @ApiModelProperty(value = "机构分类ID", required = true)
    @NotBlank(message = "机构分类不能为空")
    private String categoryId;

    @ApiModelProperty(value = "负责人姓名")
    private String contactName;

    @ApiModelProperty(value = "负责人电话")
    private String contactPhone;

    @ApiModelProperty(value = "机构地址")
    private String address;

    @ApiModelProperty(value = "排序", example = "1")
    private Integer sortOrder;

    @ApiModelProperty(value = "状态（0:禁用 1:启用）", example = "1")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;
} 