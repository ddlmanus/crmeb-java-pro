package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 合作社审核员工授信申请VO
 */
@Data
@ApiModel(value = "CooperativeCreditAuditVO", description = "合作社审核员工授信申请请求参数")
public class CooperativeCreditAuditVO {
    
    @ApiModelProperty(value = "授信申请ID", required = true)
    @NotBlank(message = "申请ID不能为空")
    private String applicationId;
    
    @ApiModelProperty(value = "审核状态：1-拒绝，2-通过", required = true, example = "2")
    @NotNull(message = "审核状态不能为空")
    @Min(value = 1, message = "审核状态值错误")
    @Max(value = 2, message = "审核状态值错误")
    private Integer auditStatus;
    
    @ApiModelProperty(value = "审批授信额度（仅审核通过时需要）", example = "50000.00")
    private BigDecimal approvedAmount;
    
    @ApiModelProperty(value = "授信利率（千分比，仅审核通过时需要）", example = "15.0")
    private BigDecimal creditRatio;
    
    @ApiModelProperty(value = "授信期限（月）", example = "12")
    private Integer creditPeriod;

    @ApiModelProperty(value = "POS卡号")
    @Size(max = 50, message = "卡号长度不能超过50个字符")
    private String cardNumber;

    @ApiModelProperty(value = "审核备注")
    @Size(max = 500, message = "审核备注长度不能超过500个字符")
    private String auditRemark;
} 