package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 资产评估分页VO
 */
@Data
@ApiModel(value = "AssetAssessmentPageVO", description = "资产评估分页响应")
public class AssetAssessmentPageVO {
    
    /**
     * 评估ID
     */
    @ApiModelProperty(value = "评估ID")
    private String id;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    private String userName;
    
    /**
     * 评估对象 1-合作社 2-养殖户/养殖企业
     */
    @ApiModelProperty(value = "评估对象：1-合作社，2-养殖户/养殖企业")
    private Integer assessmentType;

    /**
     * 养殖场名称
     */
    @ApiModelProperty(value = "养殖场名称")
    private String farmName;

    /**
     * 养殖场负责人电话
     */
    @ApiModelProperty(value = "养殖场负责人电话")
    private String managerPhone;

    /**
     * 养殖场负责人姓名
     */
    @ApiModelProperty(value = "养殖场负责人姓名")
    private String managerName;

    /**
     * 身份证号/统一社会信用代码
     */
    @ApiModelProperty(value = "身份证号/统一社会信用代码")
    private String idNumber;
    /**
     * 统一信用代码
     */
    private String creditCode;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    
    
    /**
     * 评估状态：0-草稿，1-已提交
     */
    @ApiModelProperty(value = "评估状态：0-草稿，1-已提交")
    private Integer assessmentStatus;
    
    /**
     * 是否已用于申请：0-未使用，1-已使用
     */
    @ApiModelProperty(value = "是否已用于申请：0-未使用，1-已使用")
    private Integer isUsed;

    
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
} 