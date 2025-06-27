package com.zbkj.common.vo.finance;

import lombok.Data;

/**
 * 养殖品种响应VO
 */
@Data
public class BreedingProductReponse {
    
    /**
     * 养殖品种ID
     */
    private String id;
    
    /**
     * 养殖品种名称
     */
    private String name;
    
    /**
     * 描述
     */
    private String description;

    /**
     * 养殖场编码
     */
    private String farmCode;

    /**
     * 养殖场名称
     */
    private String farmName;
    
    /**
     * 存栏量
     */
    private Integer stockQuantity;
}
