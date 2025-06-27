package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 授信申请审核历史VO
 */
@Data
@ApiModel(value = "CreditApplicationAuditHistory", description = "授信申请审核历史")
public class CreditApplicationAuditHistory {

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    @ApiModelProperty(value = "申请ID")
    private String applicationId;
    @ApiModelProperty(value = "社员名称")
    private String userName;
    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    //组织ID
    @ApiModelProperty(value = "组织ID")
    private String organizationId;
    @ApiModelProperty(value = "申请金额")
    private BigDecimal applyAmount;
    
    @ApiModelProperty(value = "审核状态：0-待审核，1-拒绝，2-通过")
    private Integer auditStatus;
    
    @ApiModelProperty(value = "审核备注")
    private String auditRemark;
    
    @ApiModelProperty(value = "审核时间")
    private Date auditTime;
    
    @ApiModelProperty(value = "审核人ID")
    private Integer auditorId;
    
    @ApiModelProperty(value = "审核人姓名")
    private String auditorName;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
} 