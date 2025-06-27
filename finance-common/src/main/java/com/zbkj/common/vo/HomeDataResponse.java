package com.zbkj.common.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 首页数据响应VO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class HomeDataResponse {
    
    /** 轮播图列表 */
    private List<BannerVo> banners;
    
    /** 分类列表 */
    private List<CategoryVo> categories;
    
    /** 秒杀商品列表 */
    private List<ProductVo> seckillProducts;
    
    /** 推荐商品列表 */
    private List<ProductVo> recommendProducts;
    
    /** 分类商品区域列表 */
    private List<CategoryProductSection> categoryProductSections;
} 