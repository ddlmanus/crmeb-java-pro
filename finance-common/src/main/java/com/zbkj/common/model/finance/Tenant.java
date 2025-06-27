package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("li_tenant")
@ApiModel(value = "租户信息实体类")
public class Tenant extends BaseEntity {
    private static final long serialVersionUID = 4685757517034933928L;

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
     * 状态（0正常 1停用）
     */
    @ApiModelProperty(value  = "状态（0正常 1停用）")
    private Integer status;

    /**
     * 租户类型（1：种植企业 2：合作社 3：服务供应商 4：商品供应商 5：种植户）
     */
    @ApiModelProperty(value  = "租户类型（1：种植企业 2：合作社 3：服务供应商 4：商品供应商 5：种植户）")
    private Integer tenantType;

    /**
     * 审核状态（2：已通过 1：被驳回 0：待审核）
     */
    @ApiModelProperty(value  = "审核状态（2：已通过 1：被驳回 0：待审核）")
    private Integer auditStatus;

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
     * 员工数量
     */
    @ApiModelProperty(value  = "员工数量")
    private Integer employeeCount;

    /**
     * 用户ID
     */
    @ApiModelProperty(value  = "用户ID")
    private Long userId;

    /**
     * 审核时间
     */
    @ApiModelProperty(value  = "审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime auditTime;

    /**
     * 审核人
     */
    @ApiModelProperty(value  = "审核人")
    private String reviewer;

    /**
     * 身份证姓名
     */
    @ApiModelProperty(value  = "身份证姓名")
    private String cardUsername;
}
