package com.zbkj.common.thirdVO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ThirdGoods {

    @ApiModelProperty(value = "商品id")
    private Integer id;
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品价格", required = true)
    private Double price;

    @ApiModelProperty(value = "品牌id")
    private String brandId;

    @ApiModelProperty(value = "分类path")
    private String categoryPath;

    @ApiModelProperty(value = "计量单位")
    private String goodsUnit;


    @ApiModelProperty(value = "卖点")
    private String sellingPoint;

    /**
     * @see
     */
    @ApiModelProperty(value = "上架状态")
    private String marketEnable;

    @ApiModelProperty(value = "详情")
    private String intro;

    @ApiModelProperty(value = "购买数量")
    private Integer buyCount;

    @ApiModelProperty(value = "库存")
    private Integer quantity;

    @ApiModelProperty(value = "商品好评率")
    private Double grade;

    @ApiModelProperty(value = "缩略图路径")
    private String thumbnail;

    @ApiModelProperty(value = "小图路径")
    private String small;

    @ApiModelProperty(value = "原图路径")
    private String original;

    @ApiModelProperty(value = "店铺分类id")
    private String storeCategoryPath;

    @ApiModelProperty(value = "评论数量")
    private Integer commentNum;

    @ApiModelProperty(value = "卖家id")
    private String storeId;

    @ApiModelProperty(value = "卖家名字")
    private String storeName;

    @ApiModelProperty(value = "运费模板id")
    private String templateId;

    /**
     * @see
     */
    @ApiModelProperty(value = "审核状态")
    private String authFlag;

    @ApiModelProperty(value = "审核信息")
    private String authMessage;

    @ApiModelProperty(value = "下架原因")
    private String underMessage;

    @ApiModelProperty(value = "是否自营")
    private Boolean selfOperated;

    @ApiModelProperty(value = "商品移动端详情")
    private String mobileIntro;

    @ApiModelProperty(value = "商品视频")
    private String goodsVideo;


    @ApiModelProperty(value = "是否为推荐商品", required = true)
    private Boolean recommend;

    /**
     * @see
     */
    @ApiModelProperty(value = "销售模式", required = true)
    private String salesModel;


    /**
     * @see
     */
    @ApiModelProperty(value = "商品类型", required = true)
    private String goodsType;

    @ApiModelProperty(value = "商品参数json", hidden = true)
    private String params;
}
