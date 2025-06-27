package com.zbkj.common.vo.finance;

import lombok.Data;

/**
 * 养殖场离场数据请求VO
 */
@Data
public class FarmExitRequestVO {
    
    /**
     * 养殖场编码
     */
    private String farmCode;
    
    /**
     * 品种
     */
    private String breed;
    
    /**
     * 开始日期
     */
    private String rqStart;
    
    /**
     * 结束日期
     */
    private String rqEnd;
    
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页数量
     */
    private Integer pageSize = 10;
} 