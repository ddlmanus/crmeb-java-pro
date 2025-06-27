package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 授信申请实体类
 */
@Data
@TableName("eb_credit_application")
public class CreditApplication {
    
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 用户类型
     */
    private Integer userType;
    
    /**
     * 用户名称
     */
    private String userName;

    /**
     * 机构ID
     */
    private String organizationId;

    /**
     * 养殖户类型
     */
    private Integer farmType;

    /**
     * 养殖场名称
     */
    private String farmName;
    /**
     * 养殖场负责人电话
     */
    private String managerPhone;

    /**
     * 养殖场负责人姓名
     */
    private String managerName;

    /**
     * 身份证号
     */
    private String idNumber;
    /**
     * 信用代码
     */
    private String creditCode;

    /**
     * 申请类型：0-社员申请，1-合作社申请
     */
    private Integer applyType;

    /**
     * 资产评估ID
     */
    private String assessmentId;

    /**
     * 申请金额
     */
    private BigDecimal applyAmount;
    
    /**
     * 申请凭证
     */
    private String applyImages;

    /**
     * 授信总额度
     */
    private BigDecimal totalAmount;
    
    /**
     * 可用授信额度
     */
    private BigDecimal availableCreditAmount;
    
    /**
     * 总待还款金额
     */
    private BigDecimal totalRepaymentAmount;
    
    /**
     * 授信利率
     */
    private BigDecimal creditRatio;

    /**
     * POS卡号
     */
    private String cardNumber;

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
     * 审核备注
     */
    private String auditRemark;
    
    /**
     * 审核时间
     */
    private Date auditTime;
    
    /**
     * 申请时间
     */
    private Date applyTime;
    
    /**
     * 申请状态：0-暂存，1-已提交
     */
    private Integer applyStatus;
} 