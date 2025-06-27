package com.zbkj.common.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 分类商品区域VO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CategoryProductSection {
    
    /** 区域ID */
    private Integer id;
    
    /** 区域名称 */
    private String name;
    
    /** 区域副标题 */
    private String subtitle;
    
    /** 商品列表 */
    private List<ProductVo> products;
} 