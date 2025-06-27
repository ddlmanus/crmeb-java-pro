package com.zbkj.common.request.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 字典查询请求对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SystemDictSearchRequest对象", description="字典查询请求对象")
public class SystemDictSearchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "字典名称")
    private String dictName;

    @ApiModelProperty(value = "字典类型")
    private String dictType;

    @ApiModelProperty(value = "字典标签")
    private String dictLabel;

    @ApiModelProperty(value = "字典键值")
    private String dictValue;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    private Integer status;

    @ApiModelProperty(value = "关键词（支持字典名称、类型、标签、键值）")
    private String keywords;

    @ApiModelProperty(value = "创建时间区间")
    private String dateLimit;
} 