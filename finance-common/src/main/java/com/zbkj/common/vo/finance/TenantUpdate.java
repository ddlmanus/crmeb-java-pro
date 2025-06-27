package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TenantUpdate {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value  = "唯一标识", hidden = true)
    private String id;
    /**
     * 企业名称
     */
    @ApiModelProperty(value  = "企业名称")
    private String enterpriseName;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value  = "统一社会信用代码")
    private String creditCode;

    /**
     * 所属省
     */
    @ApiModelProperty(value  = "所属省")
    private String province;
    /**
     * 所属省编码
     */
    @ApiModelProperty(value  = "所属省编码")
    private String provinceId;
    /**
     * 所属市
     */
    @ApiModelProperty(value  = "所属市")
    private String city;
    /**
     * 所属市编码
     */
    @ApiModelProperty(value  = "所属市编码")
    private String cityId;
    /**
     * 所属区
     */
    @ApiModelProperty(value  = "所属区")
    private String area;
    /**
     *所属区编码
     */
    @ApiModelProperty(value  = "所属区编码")
    private String areaId;
    /**
     * 所属街道
     */
    @ApiModelProperty(value  = "所属街道")
    private String street;
    @ApiModelProperty(value  = "所属街道编码")
    private String streetId;

    /**
     * 详细地址
     */
    @ApiModelProperty(value  = "详细地址")
    private String address;

    /**
     * 联系人
     */
    @ApiModelProperty(value  = "联系人")
    private String contacts;

    /**
     * 联系方式
     */
    @ApiModelProperty(value  = "联系方式")
    private String contactInformation;

    /**
     * 租户类型（1：种植企业 2：合作社 3：服务供应商 4：商品供应商 5：种植户）
     */
    @ApiModelProperty(value  = "租户类型（1：种植企业 2：合作社 3：服务供应商 4：商品供应商 5：种植户）")
    private Integer tenantType;


    /**
     * 驳回原因
     */
    @ApiModelProperty(value  = "驳回原因")
    private String rejectReason;

    /**
     * 营业执照
     */
    @ApiModelProperty(value  = "营业执照")
    private String businessLicense;

    /**
     * 身份证号
     */
    @ApiModelProperty(value  = "身份证号")
    private String idCard;

    /**
     * 身份证姓名
     */
    @ApiModelProperty(value  = "身份证姓名")
    private String cardUsername;
}
