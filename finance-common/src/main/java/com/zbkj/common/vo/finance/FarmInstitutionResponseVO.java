package com.zbkj.common.vo.finance;

import lombok.Data;

/**
 * 养殖场机构信息响应VO
 */
@Data
public class FarmInstitutionResponseVO {

    private String id;
    /**
     * 机构类型
     */
    private String jgdxlx;
    
    /**
     * 机构标识代码
     */
    private String code;
    
    /**
     * 用户姓名
     */
    private String userName;
    
    /**
     * 养殖场名称
     */
    private String farmName;

    /**
     * 养殖场code
     */
    private String farmCode;
    
    /**
     * 养殖场负责人电话
     */
    private String fzrPhone;
    
    /**
     * 养殖场负责人姓名
     */
    private String fzr;
} 