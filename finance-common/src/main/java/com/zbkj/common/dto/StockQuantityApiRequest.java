package com.zbkj.common.dto;

import lombok.Data;

/**
 * 牧码通存栏量接口请求参数
 */
@Data
public class StockQuantityApiRequest {
    
    /**
     * 养殖场编码
     */
    private String farm_code;

    /**
     * 品种名称
     */
    private String breed;
} 