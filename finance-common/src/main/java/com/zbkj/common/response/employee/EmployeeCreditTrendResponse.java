package com.zbkj.common.response.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 员工授信趋势响应
 * </p>
 *
 * @author zbkj
 * @since 2024-12-19
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "EmployeeCreditTrendResponse对象", description = "员工授信趋势响应")
public class EmployeeCreditTrendResponse {

    @ApiModelProperty(value = "日期列表")
    private List<String> dates;

    @ApiModelProperty(value = "总授信额度趋势")
    private List<BigDecimal> totalAmounts;

    @ApiModelProperty(value = "已使用额度趋势")
    private List<BigDecimal> usedAmounts;

    @ApiModelProperty(value = "授信员工数量趋势")
    private List<Integer> creditEmployeeCounts;
} 