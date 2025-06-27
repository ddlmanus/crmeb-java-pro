package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 员工申请授信额度VO
 */
@Data
@ApiModel(value = "EmployeeCreditApplicationVO", description = "员工申请授信额度请求参数")
public class EmployeeCreditApplicationVO {
    
    /**
     * 申请金额
     */
    @ApiModelProperty(value = "申请金额", required = true)
    @NotNull(message = "申请金额不能为空")
    @DecimalMin(value = "0.01", message = "申请金额必须大于0")
    private BigDecimal applyAmount;

    /**
     * 申请凭证（图片URL等）
     */
    @ApiModelProperty(value = "申请凭证")
    private String applyImages;
    
    /**
     * 申请备注
     */
    @ApiModelProperty(value = "申请备注")
    private String applyRemark;
} 