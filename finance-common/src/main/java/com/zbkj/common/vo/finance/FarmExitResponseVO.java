package com.zbkj.common.vo.finance;

import lombok.Data;

/**
 * 养殖场离场数据响应VO
 */
@Data
public class FarmExitResponseVO {
    
    /**
     * ID
     */
    private String id;
    
    /**
     * 省
     */
    private String province;
    
    /**
     * 市
     */
    private String city;
    
    /**
     * 县
     */
    private String county;
    
    /**
     * 乡
     */
    private String township;
    
    /**
     * 养殖场编码
     */
    private String farmCode;
    
    /**
     * 养殖场名称
     */
    private String farmName;
    
    /**
     * 离场日期
     */
    private String exitDate;
    
    /**
     * 畜种
     */
    private String livestock;
    
    /**
     * 品种
     */
    private String breed;
    
    /**
     * 畜种类型
     */
    private String livestockType;
    
    /**
     * 离场数量
     */
    private Integer exitQuantity;
    
    /**
     * 出栏数量
     */
    private Integer slaughteredQuantity;
    
    /**
     * 调出数量
     */
    private Integer transferredOutQuantity;
    
    /**
     * 隔离数量
     */
    private Integer isolatedQuantity;
    
    /**
     * 死亡数量
     */
    private Integer deadQuantity;
    
    /**
     * 淘汰数量
     */
    private Integer eliminationQuantity;
    
    /**
     * 扑杀数量
     */
    private Integer killQuantity;
} 