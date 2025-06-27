package com.zbkj.common.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 商品列表VO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ProductListVo {
    
    /** 商品ID */
    private Integer id;
    
    /** 商品名称 */
    private String name;
    
    /** 商品主图 */
    private String image;
    
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
} 