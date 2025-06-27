package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 还款记录实体类
 */
@Data
@TableName("eb_repayment_record")
public class RepaymentRecord {
    
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
     * 用户名称
     */
    private String userName;
    
    /**
     * 授信支付订单号
     */
    private String creditOrderNo;
    
    /**
     * 原始订单号
     */
    private String originalOrderNo;
    
    /**
     * 还款金额
     */
    private BigDecimal repaymentAmount;
    
    /**
     * 还款凭证（图片URL）
     */
    private String repaymentProof;
    
    /**
     * 申请时间
     */
    private Date applyTime;
    
    /**
     * 还款时间
     */
    private Date repaymentTime;
    
    /**
     * 还款方式：0-银行转账，1-现金，2-其他
     */
    private Integer repaymentMethod;

    /**
     * 审核状态：0-待审核，2-已通过，1-已拒绝
     */
    private Integer repaymentStatus;
    
    /**
     * 审核人ID
     */
    private Integer auditorId;
    
    /**
     * 审核人名称
     */
    private String auditorName;
    
    /**
     * 审核时间
     */
    private Date auditTime;
    
    /**
     * 审核备注
     */
    private String auditRemark;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 删除标志
     */
    private Integer deleteFlag;

    /**
     * 机构ID
     */
    private String organizationId;
} 