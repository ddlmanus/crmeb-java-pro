package com.zbkj.common.request.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 养殖场机构信息查询请求对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="FarmInstitutionSearchRequest对象", description="养殖场机构信息查询请求对象")
public class FarmInstitutionSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "机构分类编码")
    private String categoryCode;

    @ApiModelProperty(value = "机构标识代码")
    private String farmCode;
    @ApiModelProperty(value = "养殖场名称")
    private String farmName;

    @ApiModelProperty(value = "养殖场负责人姓名")
    private String contactName;

    @ApiModelProperty(value = "养殖场负责人电话")
    private String contactPhone;

    @ApiModelProperty(value = "关键词（支持养殖场名称、负责人姓名、电话）")
    private String keywords;

    @ApiModelProperty(value = "创建时间区间")
    private String dateLimit;
} 