package com.zbkj.common.vo.finance;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建还款记录VO
 */
@Data
public class RepaymentCreateVO {
    
    /**
     * 还款金额
     */
    private BigDecimal repaymentAmount;
    
    /**
     * 还款凭证（图片URL）
     */
    private String repaymentProof;
    
    /**
     * 还款方式：0-银行转账，1-现金，2-其他
     */
    private Integer repaymentMethod;
    
    /**
     * 备注
     */
    private String remark;
} 