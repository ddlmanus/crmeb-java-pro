package com.zbkj.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 轮播图VO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class BannerVo {
    
    /** 轮播图ID */
    private Integer id;
    
    /** 标题 */
    private String title;
    
    /** 图片URL */
    private String image;
    
    /** 链接地址 */
    private String link;
} 