package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 授信申请审核请求VO
 */
@Data
@ApiModel(value = "CreditApplicationAuditRequest", description = "授信申请审核请求参数")
public class CreditApplicationAuditRequest {
    
    @ApiModelProperty(value = "授信申请ID", required = true)
    @NotBlank(message = "申请ID不能为空")
    private String id;
    
    @ApiModelProperty(value = "审核状态：0-待审核，1-拒绝，2-通过", required = true, example = "2")
    @NotNull(message = "审核状态不能为空")
    @Min(value = 1, message = "审核状态值错误")
    @Max(value = 2, message = "审核状态值错误")
    private Integer auditStatus;
    
    @ApiModelProperty(value = "授信额度（仅审核通过时需要）", example = "100000.00")
    private BigDecimal creditAmount;
    
    @ApiModelProperty(value = "授信利率（仅审核通过时需要）", example = "0.05")
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
