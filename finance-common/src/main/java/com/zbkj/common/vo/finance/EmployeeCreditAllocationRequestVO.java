package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 员工授信额度分配请求VO
 */
@Data
@ApiModel(value = "EmployeeCreditAllocationRequestVO", description = "员工授信额度分配请求参数")
public class EmployeeCreditAllocationRequestVO {
    
    @ApiModelProperty(value = "员工ID", required = true)
    @NotNull(message = "员工ID不能为空")
    private Integer employeeId;
    
    @ApiModelProperty(value = "授信额度", required = true, example = "100000.00")
    @NotNull(message = "授信额度不能为空")
    @DecimalMin(value = "0.01", message = "授信额度必须大于0")
    private BigDecimal creditAmount;
    
    @ApiModelProperty(value = "授信利率", required = true, example = "0.05")
    @NotNull(message = "授信利率不能为空")
    @DecimalMin(value = "0.0001", message = "授信利率必须大于0")
    @DecimalMax(value = "1", message = "授信利率不能超过1")
    private BigDecimal creditRatio;
    
    @ApiModelProperty(value = "授信期限（月）", required = true, example = "12")
    @NotNull(message = "授信期限不能为空")
    @Min(value = 1, message = "授信期限至少1个月")
    @Max(value = 120, message = "授信期限不能超过120个月")
    private Integer creditPeriod;

    @ApiModelProperty(value = "POS卡号")
    @Size(max = 50, message = "卡号长度不能超过50个字符")
    private String cardNumber;

    @ApiModelProperty(value = "分配备注")
    @Size(max = 500, message = "分配备注长度不能超过500个字符")
    private String remark;
} 