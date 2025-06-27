package com.zbkj.common.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 购物车统计VO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CartSummaryVo {
    
    /** 总商品数量 */
    private Integer totalCount;
    
    /** 选中商品数量 */
    private Integer selectedCount;
    
    /** 总金额 */
    private BigDecimal totalAmount;
} 