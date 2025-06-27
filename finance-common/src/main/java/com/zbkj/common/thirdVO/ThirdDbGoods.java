package com.zbkj.common.thirdVO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 第三方数据库商品实体类
 * 对应三牧优选数据库中的li_goods表
 */
@Data
public class ThirdDbGoods {

    @ApiModelProperty(value = "商品ID")
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

    @ApiModelProperty(value = "审核信息")
    private String authMessage;

    @ApiModelProperty(value = "品牌ID")
    private String brandId;

    @ApiModelProperty(value = "购买数量")
    private Integer buyCount;

    @ApiModelProperty(value = "分类路径")
    private String categoryPath;

    @ApiModelProperty(value = "评价数量")
    private Integer commentNum;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品单位")
    private String goodsUnit;

    @ApiModelProperty(value = "商品视频")
    private String goodsVideo;

    @ApiModelProperty(value = "商品好评率")
    private Double grade;

    @ApiModelProperty(value = "商品详情")
    private String intro;

    @ApiModelProperty(value = "审核状态")
    private String authFlag;

    @ApiModelProperty(value = "上架状态")
    private String marketEnable;

    @ApiModelProperty(value = "移动端商品详情")
    private String mobileIntro;

    @ApiModelProperty(value = "原图路径")
    private String original;

    @ApiModelProperty(value = "商品价格")
    private Double price;

    @ApiModelProperty(value = "库存")
    private Integer quantity;

    @ApiModelProperty(value = "是否为推荐商品")
    private Boolean recommend;

    @ApiModelProperty(value = "销售模式")
    private String salesModel;

    @ApiModelProperty(value = "是否自营")
    private Boolean selfOperated;

    @ApiModelProperty(value = "店铺ID")
    private String storeId;

    @ApiModelProperty(value = "店铺名称")
    private String storeName;

    @ApiModelProperty(value = "卖点")
    private String sellingPoint;

    @ApiModelProperty(value = "小图路径")
    private String small;

    @ApiModelProperty(value = "运费模板ID")
    private String templateId;

    @ApiModelProperty(value = "缩略图路径")
    private String thumbnail;

    @ApiModelProperty(value = "下架原因")
    private String underMessage;

    @ApiModelProperty(value = "商品类型")
    private String goodsType;

    @ApiModelProperty(value = "商品参数")
    private String params;

    @ApiModelProperty(value = "店铺分类路径")
    private String storeCategoryPath;
} 