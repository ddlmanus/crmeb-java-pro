package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 还款记录VO
 */
@Data
@ApiModel(value = "还款记录", description = "还款记录")
public class RepaymentRecordVO {
    
    @ApiModelProperty(value = "ID")
    private String id;
    
    @ApiModelProperty(value = "订单号")
    private String orderNo;
    
    @ApiModelProperty(value = "租户ID")
    private String tenantId;
    
    @ApiModelProperty(value = "社员名称")
    private String memberName;
    
    @ApiModelProperty(value = "社员账号")
    private String memberAccount;
    
    @ApiModelProperty(value = "总额度")
    private BigDecimal totalAmount;
    
    @ApiModelProperty(value = "剩余额度")
    private BigDecimal remainingAmount;
    
    @ApiModelProperty(value = "还款金额")
    private BigDecimal repaymentAmount;
    
    @ApiModelProperty(value = "还款时间")
    private Date repaymentTime;
    
    @ApiModelProperty(value = "还款时间格式化")
    private String repaymentTimeClone;
    
    @ApiModelProperty(value = "凭证URL")
    private String voucherUrl;
    
    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    
    @ApiModelProperty(value = "状态")
    private Integer status;
    
    @ApiModelProperty(value = "审核状态")
    private Integer auditStatus;
    
    @ApiModelProperty(value = "审核时间")
    private Date auditTime;
    
    @ApiModelProperty(value = "审核人")
    private String auditUser;
    
    @ApiModelProperty(value = "审核原因")
    private String auditReason;
    
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "状态文字")
    private String statusText;
    
    @ApiModelProperty(value = "用户名称")
    private String userName;
    
    @ApiModelProperty(value = "授信支付订单号")
    private String creditOrderNo;
    
    @ApiModelProperty(value = "原始订单号")
    private String originalOrderNo;
    
    @ApiModelProperty(value = "还款凭证")
    private String repaymentProof;
    
    @ApiModelProperty(value = "申请时间")
    private Date applyTime;
    
    @ApiModelProperty(value = "还款方式")
    private Integer repaymentMethod;
    
    @ApiModelProperty(value = "还款状态")
    private Integer repaymentStatus;
    
    @ApiModelProperty(value = "审核人ID")
    private Integer auditorId;
    
    @ApiModelProperty(value = "审核人名称")
    private String auditorName;
    
    @ApiModelProperty(value = "审核备注")
    private String auditRemark;
    
    @ApiModelProperty(value = "备注")
    private String remark;
} 