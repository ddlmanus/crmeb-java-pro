package com.zbkj.common.vo.finance;

import lombok.Data;
import org.apache.ibatis.javassist.runtime.Inner;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 股份管理VO
 */
@Data
public class ShareManagementVO {
    
    /**
     * ID
     */
    private String id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 股份占比
     */
    private BigDecimal sharePercentage;
} 