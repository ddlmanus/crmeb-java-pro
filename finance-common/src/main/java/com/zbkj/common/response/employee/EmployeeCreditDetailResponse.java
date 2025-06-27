package com.zbkj.common.response.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 员工授信明细响应
 * </p>
 *
 * @author zbkj
 * @since 2024-12-19
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "EmployeeCreditDetailResponse对象", description = "员工授信明细响应")
public class EmployeeCreditDetailResponse {

    @ApiModelProperty(value = "员工ID")
    private Integer id;

    @ApiModelProperty(value = "员工姓名")
    private String name;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "养殖机构名称")
    private String farmInstitutionName;

    @ApiModelProperty(value = "授信额度")
    private BigDecimal creditLimit;

    @ApiModelProperty(value = "已使用额度")
    private BigDecimal usedCredit;

    @ApiModelProperty(value = "可用额度")
    private BigDecimal availableCredit;

    @ApiModelProperty(value = "授信系数")
    private BigDecimal creditCoefficient;

    @ApiModelProperty(value = "授信使用率")
    private BigDecimal creditUtilizationRate;

    @ApiModelProperty(value = "最近授信时间")
    private Date lastCreditTime;

    @ApiModelProperty(value = "员工状态：0离职，1在职，2试用期")
    private Integer status;

    @ApiModelProperty(value = "授信状态：0未授信，1已授信")
    private Integer creditStatus;
} 