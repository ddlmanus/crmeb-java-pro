package com.zbkj.common.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品VO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ProductVo {
    
    /** 商品ID */
    private Integer id;
    
    /** 商品名称 */
    private String name;
    
    /** 商品副标题 */
    private String subtitle;
    
    /** 商品主图 */
    private String image;
    
    /** 商品图片列表 */
    private List<String> images;
    
    /** 现价 */
    private BigDecimal price;
    
    /** 原价 */
    private BigDecimal originalPrice;
    
    /** 销量 */
    private Integer sales;
    
    /** 库存 */
    private Integer stock;
    
    /** 评分 */
    private Double rating;
    
    /** 标签列表 */
    private List<ProductLabel> labels;
    
    /** 商品详情 */
    private String detail;
    
    /** 规格参数 */
    private List<ProductParam> params;
    
    /** 用户评价列表 */
    private List<ProductReview> reviews;
    
    /** 平均评分 */
    private Double averageRating;
    
    /** 评价数量 */
    private Integer reviewCount;
    
    /**
     * 商品标签
     */
    @Data
    public static class ProductLabel {
        private String text;
        private String type; // hot, new, sale等
    }
    
    /**
     * 商品参数
     */
    @Data
    public static class ProductParam {
        private String name;
        private String value;
    }
    
    /**
     * 商品评价
     */
    @Data
    public static class ProductReview {
        private Integer id;
        private String username;
        private String avatar;
        private Double rating;
        private String date;
        private String content;
        private List<String> images;
    }
} 