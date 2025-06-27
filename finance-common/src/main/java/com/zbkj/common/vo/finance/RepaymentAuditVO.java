package com.zbkj.common.vo.finance;

import lombok.Data;

/**
 * 还款审核VO
 */
@Data
public class RepaymentAuditVO {
    
    /**
     * 还款记录ID
     */
    private String id;
    
    /**
     * 审核状态：0-待审核，2-已通过，1-已拒绝
     */
    private Integer repaymentStatus;
    
    /**
     * 审核原因
     */
    private String auditRemark;
} 