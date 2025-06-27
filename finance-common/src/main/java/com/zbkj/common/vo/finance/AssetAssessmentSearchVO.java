package com.zbkj.common.vo.finance;

import com.zbkj.common.request.PageParamRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资产评估搜索条件VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AssetAssessmentSearchVO", description = "资产评估搜索条件")
public class AssetAssessmentSearchVO extends PageParamRequest {

    @ApiModelProperty(value = "关键词（用户名称、养殖场名称）")
    private String keywords;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "评估对象：1-合作社，2-养殖户/养殖企业")
    private Integer assessmentType;

    @ApiModelProperty(value = "养殖场名称")
    private String farmName;

    @ApiModelProperty(value = "养殖场机构ID")
    private Integer farmInstitutionId;

    @ApiModelProperty(value = "评估状态：0-草稿，1-已提交")
    private Integer assessmentStatus;

    @ApiModelProperty(value = "是否已用于申请：0-未使用，1-已使用")
    private Integer isUsed;

    @ApiModelProperty(value = "创建时间开始")
    private String startTime;

    @ApiModelProperty(value = "创建时间结束")
    private String endTime;

    @ApiModelProperty(value = "排序字段")
    private String orderBy;

    @ApiModelProperty(value = "排序方式：asc/desc")
    private String order = "desc";
} 