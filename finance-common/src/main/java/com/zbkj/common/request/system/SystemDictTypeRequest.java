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
 * 字典类型请求对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SystemDictTypeRequest对象", description="字典类型请求对象")
public class SystemDictTypeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "字典主键")
    private Long id;

    @ApiModelProperty(value = "字典名称")
    @NotEmpty(message = "字典名称不能为空")
    @Length(max = 100, message = "字典名称长度不能超过100个字符")
    private String dictName;

    @ApiModelProperty(value = "字典类型")
    @NotEmpty(message = "字典类型不能为空")
    @Length(max = 100, message = "字典类型长度不能超过100个字符")
    private String dictType;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    @NotNull(message = "状态不能为空")
    @Range(min = 0, max = 1, message = "状态值必须为0或1")
    private Integer status;

    @ApiModelProperty(value = "备注")
    @Length(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
} 