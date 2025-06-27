package com.zbkj.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 分类VO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVo {
    
    /** 分类ID */
    private Integer id;
    
    /** 分类名称 */
    private String name;
    
    /** 图标 */
    private String icon;
} 