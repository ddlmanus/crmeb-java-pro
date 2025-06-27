package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 授信支付订单实体类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@TableName("eb_credit_payment_order")
@ApiModel(value = "CreditPaymentOrder对象", description = "授信支付订单")
public class CreditPaymentOrder {
    
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    
    @ApiModelProperty(value = "用户名称")
    private String userName;
    
    @ApiModelProperty(value = "原始订单号")
    private String originalOrderNo;
    
    @ApiModelProperty(value = "授信支付订单号")
    private String creditOrderNo;
    
    @ApiModelProperty(value = "支付金额")
    private BigDecimal paymentAmount;
    
    @ApiModelProperty(value = "利息金额")
    private BigDecimal interestAmount;
    
    @ApiModelProperty(value = "总应还金额")
    private BigDecimal totalRepaymentAmount;
    
    @ApiModelProperty(value = "已还金额")
    private BigDecimal paidAmount;
    
    @ApiModelProperty(value = "剩余应还金额")
    private BigDecimal remainingAmount;
    
    @ApiModelProperty(value = "授信利率")
    private BigDecimal creditRatio;
    
    @ApiModelProperty(value = "还款状态：0-未还款，1-部分还款，2-已还清")
    private Integer repaymentStatus;
    
    @ApiModelProperty(value = "还款期限(天)")
    private Integer repaymentDeadline;
    
    @ApiModelProperty(value = "订单状态：0-待还款，1-逾期，2-已还清，3-已取消")
    private Integer status;
    
    @ApiModelProperty(value = "备注")
    private String remark;
    
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    
    @ApiModelProperty(value = "删除标志：0-未删除，1-已删除")
    private Integer deleteFlag;
} 