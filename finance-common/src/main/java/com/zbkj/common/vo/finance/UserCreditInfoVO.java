package com.zbkj.common.vo.finance;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户授信信息VO
 */
@Data
public class UserCreditInfoVO {

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户账号
     */
    private String account;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 用户类型文本
     */
    private String userTypeText;

    /**
     * 授信层级
     */
    private Integer creditLevel;

    /**
     * 授信层级文本
     */
    private String creditLevelText;

    /**
     * 授信额度
     */
    private BigDecimal creditLimit;

    /**
     * 评估额度
     */
    private BigDecimal assessmentAmount;

    /**
     * 剩余授信额度
     */
    private BigDecimal remainingCredit;

    /**
     * 已使用授信额度
     */
    private BigDecimal usedCredit;

    /**
     * 待还款额度
     */
    private BigDecimal pendingRepayment;

    /**
     * 授信状态：0-未授信，1-已授信，2-授信冻结，3-授信失效
     */
    private Integer creditStatus;

    /**
     * 授信状态文本
     */
    private String creditStatusText;

    /**
     * 授信系数
     */
    private BigDecimal creditCoefficient;

    /**
     * 授信利率
     */
    private BigDecimal creditRatio;

    /**
     * 还款期限（天）
     */
    private Integer repaymentDays;

    /**
     * 卡号
     */
    private String cardNumber;

    /**
     * 授信开始时间
     */
    private Date creditStartTime;

    /**
     * 授信结束时间
     */
    private Date creditEndTime;

    /**
     * 上级授信用户ID
     */
    private Integer parentCreditUserId;

    /**
     * 上级授信用户姓名
     */
    private String parentUserName;

    /**
     * 养殖场名称
     */
    private String farmName;

    /**
     * 总存栏量
     */
    private Integer totalStockQuantity;

    /**
     * 养殖品种名称（拼接）
     */
    private String breedingVarietyNames;

    /**
     * 养殖品种类型（拼接）
     */
    private String breedingVarietyTypes;

    /**
     * 最近授信审核时间
     */
    private Date lastCreditAuditTime;

    /**
     * 最近授信审核人ID
     */
    private Integer lastCreditAuditUserId;

    /**
     * 审核人姓名
     */
    private String auditUserName;

    /**
     * 授信审核备注
     */
    private String creditAuditRemark;

    /**
     * 可用授信率（剩余额度/总额度）
     */
    public BigDecimal getAvailableRate() {
        if (creditLimit == null || creditLimit.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (remainingCredit == null) {
            return BigDecimal.ZERO;
        }
        return remainingCredit.divide(creditLimit, 4, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 使用率（已使用额度/总额度）
     */
    public BigDecimal getUsageRate() {
        if (creditLimit == null || creditLimit.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (usedCredit == null) {
            return BigDecimal.ZERO;
        }
        return usedCredit.divide(creditLimit, 4, BigDecimal.ROUND_HALF_UP);
    }
} 