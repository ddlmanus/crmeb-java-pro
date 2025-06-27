package com.zbkj.common.thirdVO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 第三方数据库分类实体类
 * 对应三牧优选数据库中的li_category表
 */
@Data
public class ThirdDbCategory {

    @ApiModelProperty(value = "分类ID")
    private Long id;

    @ApiModelProperty(value = "创建者")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "删除标志")
    private Boolean deleteFlag;

    @ApiModelProperty(value = "更新者")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "分类名称")
    private String name;

    @ApiModelProperty(value = "父分类ID")
    private String parentId;

    @ApiModelProperty(value = "分类级别")
    private Integer level;

    @ApiModelProperty(value = "排序值")
    private BigDecimal sortOrder;

    @ApiModelProperty(value = "佣金比例")
    private Double commissionRate;

    @ApiModelProperty(value = "分类图标")
    private String image;

    @ApiModelProperty(value = "是否支持频道")
    private Boolean supportChannel;

    @ApiModelProperty(value = "父级分类名称")
    private String parentTitle;
} 