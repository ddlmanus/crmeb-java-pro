package com.zbkj.common.thirdVO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class ThirdGoodsCategory {

    @ApiModelProperty(value = "分类id")
     private Integer id;
    @Size(max = 20)
    @ApiModelProperty(value = "分类名称")
    private String name;

    @ApiModelProperty(value = "父id, 根节点为0")
    private String parentId;

    @Max(value = 3,message = "层级最大为3")
    @ApiModelProperty(value = "层级, 从0开始")
    private Integer level;

    @ApiModelProperty(value = "排序值")
    private BigDecimal sortOrder;

    @ApiModelProperty(value = "佣金比例")
    private Double commissionRate;

    @ApiModelProperty(value = "分类图标")
    private String image;

    @ApiModelProperty(value = "是否支持频道")
    private Boolean supportChannel;
}
