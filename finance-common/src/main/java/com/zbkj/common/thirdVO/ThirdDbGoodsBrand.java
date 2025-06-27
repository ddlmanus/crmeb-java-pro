package com.zbkj.common.thirdVO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 第三方数据库商品品牌实体类
 * 对应三牧优选数据库中的li_brand表
 */
@Data
public class ThirdDbGoodsBrand {

    @ApiModelProperty(value = "品牌ID")
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

    @ApiModelProperty(value = "品牌名称")
    private String name;

    @ApiModelProperty(value = "品牌图标")
    private String logo;
} 