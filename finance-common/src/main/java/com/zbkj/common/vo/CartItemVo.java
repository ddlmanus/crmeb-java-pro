package com.zbkj.common.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 购物车项VO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CartItemVo {
    
    /** 购物车项ID */
    private Integer id;
    
    /** 商品ID */
    private Integer productId;
    
    /** 商品名称 */
    private String productName;
    
    /** 商品图片 */
    private String productImage;
    
    /** 商品价格 */
    private BigDecimal price;
    
    /** 数量 */
    private Integer quantity;
    
    /** 规格信息 */
    private String specs;
    
    /** 是否选中 */
    private Boolean selected;
} 