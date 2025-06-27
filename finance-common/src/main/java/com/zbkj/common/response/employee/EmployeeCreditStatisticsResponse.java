package com.zbkj.common.response.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 员工授信统计概览响应
 * </p>
 *
 * @author zbkj
 * @since 2024-12-19
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "EmployeeCreditStatisticsResponse对象", description = "员工授信统计概览响应")
public class EmployeeCreditStatisticsResponse {

    @ApiModelProperty(value = "员工总数")
    private Integer totalEmployees;

    @ApiModelProperty(value = "有授信员工数")
    private Integer creditEmployees;

    @ApiModelProperty(value = "总授信额度")
    private BigDecimal totalCreditAmount;

    @ApiModelProperty(value = "已使用额度")
    private BigDecimal usedCreditAmount;

    @ApiModelProperty(value = "可用额度")
    private BigDecimal availableCreditAmount;

    @ApiModelProperty(value = "平均授信额度")
    private BigDecimal averageCreditAmount;
} 