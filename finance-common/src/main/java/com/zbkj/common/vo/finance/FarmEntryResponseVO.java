package com.zbkj.common.vo.finance;

import lombok.Data;

/**
 * 养殖场进场数据响应VO
 */
@Data
public class FarmEntryResponseVO {
    
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
     * 进场日期
     */
    private String entryDate;
    
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
     * 进场数量
     */
    private Integer entryQuantity;
    
    /**
     * 自繁进场数量
     */
    private Integer selfBredQuantity;
    
    /**
     * 调入进场数量
     */
    private Integer transferredQuantity;
} 