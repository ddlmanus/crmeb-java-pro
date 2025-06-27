package com.zbkj.common.request.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 字典数据请求对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SystemDictDataRequest对象", description="字典数据请求对象")
public class SystemDictDataRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "字典编码")
    private Long id;

    @ApiModelProperty(value = "字典排序")
    @NotNull(message = "字典排序不能为空")
    @Range(min = 0, max = 9999, message = "字典排序范围为0-9999")
    private Integer dictSort;

    @ApiModelProperty(value = "字典标签")
    @NotEmpty(message = "字典标签不能为空")
    @Length(max = 100, message = "字典标签长度不能超过100个字符")
    private String dictLabel;

    @ApiModelProperty(value = "字典键值")
    @NotEmpty(message = "字典键值不能为空")
    @Length(max = 100, message = "字典键值长度不能超过100个字符")
    private String dictValue;

    @ApiModelProperty(value = "字典类型")
    @NotEmpty(message = "字典类型不能为空")
    @Length(max = 100, message = "字典类型长度不能超过100个字符")
    private String dictType;

    @ApiModelProperty(value = "样式属性（其他样式扩展）")
    @Length(max = 100, message = "样式属性长度不能超过100个字符")
    private String cssClass;

    @ApiModelProperty(value = "表格回显样式")
    @Length(max = 100, message = "表格回显样式长度不能超过100个字符")
    private String listClass;

    @ApiModelProperty(value = "是否默认（Y是 N否）")
    @Length(max = 1, message = "是否默认值长度不能超过1个字符")
    private String isDefault;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    @NotNull(message = "状态不能为空")
    @Range(min = 0, max = 1, message = "状态值必须为0或1")
    private Integer status;

    @ApiModelProperty(value = "备注")
    @Length(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
} 