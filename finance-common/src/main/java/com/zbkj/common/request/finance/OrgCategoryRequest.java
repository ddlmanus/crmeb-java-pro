package com.zbkj.common.request.finance;

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
 * 养殖机构分类请求对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrgCategoryRequest对象", description="养殖机构分类请求对象")
public class OrgCategoryRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "分类ID")
    private String id;

    @ApiModelProperty(value = "分类代码")
    @Length(max = 50, message = "分类代码长度不能超过50个字符")
    private String code;

    @ApiModelProperty(value = "分类类型id（如：养殖场、合作社等）")
    @Length(max = 50, message = "分类类型id长度不能超过50个字符")
    private String typeCode;

    @ApiModelProperty(value = "分类类型名称")
    @NotEmpty(message = "分类类型名称不能为空")
    @Length(max = 100, message = "分类类型名称长度不能超过100个字符")
    private String typeName;

    @ApiModelProperty(value = "分类名称")
    @NotEmpty(message = "分类名称不能为空")
    @Length(max = 100, message = "分类名称长度不能超过100个字符")
    private String name;

    @ApiModelProperty(value = "父级分类代码")
    @Length(max = 50, message = "父级分类代码长度不能超过50个字符")
    private String parentCode;

    @ApiModelProperty(value = "层级（1:一级 2:二级 3:三级）")
    @NotNull(message = "层级不能为空")
    @Range(min = 1, max = 3, message = "层级必须在1-3之间")
    private Integer level;

    @ApiModelProperty(value = "排序")
    @NotNull(message = "排序不能为空")
    @Range(min = 0, max = 9999, message = "排序范围为0-9999")
    private Integer sort;

    @ApiModelProperty(value = "状态（0:禁用 1:启用）")
    @NotNull(message = "状态不能为空")
    @Range(min = 0, max = 1, message = "状态值必须为0或1")
    private Integer status;

    @ApiModelProperty(value = "备注")
    @Length(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    @ApiModelProperty(value = "管理员ID")
    @Length(max = 50, message = "管理员ID长度不能超过50个字符")
    private String managerId;

    @ApiModelProperty(value = "管理员姓名")
    @Length(max = 50, message = "管理员姓名长度不能超过50个字符")
    private String managerName;
} 