package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 机构管理搜索VO
 */
@Data
@ApiModel(value = "OrganizationSearchVO", description = "机构管理搜索参数")
public class OrganizationSearchVO {

    @ApiModelProperty(value = "关键词（机构名称、编号）")
    private String keywords;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "机构编号")
    private String orgCode;

    @ApiModelProperty(value = "所属区域")
    private String region;

    @ApiModelProperty(value = "上级机构ID")
    private String parentOrgId;

    @ApiModelProperty(value = "机构分类ID")
    private String categoryId;

    @ApiModelProperty(value = "状态（0:禁用 1:启用）")
    private Integer status;

    @ApiModelProperty(value = "创建时间范围")
    private String dateLimit;
} 