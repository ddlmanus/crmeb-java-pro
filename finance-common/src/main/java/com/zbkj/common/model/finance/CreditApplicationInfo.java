package com.zbkj.common.model.finance;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 授信申请信息实体类，用于前端展示用户的授信额度信息
 */
@Data
public class CreditApplicationInfo {
    
    /**
     * ID
     */
    private String id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 授信总额度
     */
    private BigDecimal totalAmount;
    
    /**
     * 可用授信额度
     */
    private BigDecimal availableCreditAmount;
    
    /**
     * 已使用授信额度
     */
    private BigDecimal usedCreditAmount;
    
    /**
     * 总待还款金额
     */
    private BigDecimal totalRepaymentAmount;
    
    /**
     * 授信利率
     */
    private BigDecimal creditRatio;
    
    /**
     * 授信开始时间
     */
    private Date creditStartTime;
    
    /**
     * 授信期限（单位：月）
     */
    private Integer creditPeriod;
    
    /**
     * 审核状态：0-待审核，1-拒绝，2-通过
     */
    private Integer auditStatus;
    
    /**
     * 审核时间
     */
    private Date auditTime;
    
    /**
     * 申请时间
     */
    private Date applyTime;
}
