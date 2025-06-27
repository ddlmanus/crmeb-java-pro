package com.zbkj.common.dto;

import lombok.Data;

/**
 * 养殖品种API请求参数
 */
@Data
public class BreedingProductApiRequest {
    
    /**
     * 养殖场编码
     */
    private String farm_code;
    
    /**
     * 页码
     */
    private Integer page;
    
    /**
     * 每页数量
     */
    private Integer pageSize;
} 