package com.zbkj.common.thirdVO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ThirdGoodsDetail extends ThirdGoods{
    @ApiModelProperty(value = "分类名称")
    private List<String> categoryName;

    @ApiModelProperty(value = "商品图片")
    private List<String> goodsGalleryList;

    @ApiModelProperty(value = "sku列表")
    private List<ThirdGoodsSku> skuList;

}
