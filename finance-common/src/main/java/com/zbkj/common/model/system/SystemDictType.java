package com.zbkj.common.model.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 字典类型表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_dict_type")
@ApiModel(value="SystemDictType对象", description="字典类型表")
public class SystemDictType implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "字典主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "字典名称")
    private String dictName;

    @ApiModelProperty(value = "字典类型")
    private String dictType;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    private Integer status;

    @ApiModelProperty(value = "创建者")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新者")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "删除标志（0代表存在 1代表删除）")
    private Integer deleteFlag;
} 