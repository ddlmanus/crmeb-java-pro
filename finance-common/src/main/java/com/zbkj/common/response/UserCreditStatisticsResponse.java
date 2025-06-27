package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户授信统计响应类
 */
@Data
@ApiModel(value = "UserCreditStatisticsResponse", description = "用户授信统计响应")
public class UserCreditStatisticsResponse {
    
    @ApiModelProperty(value = "总用户数")
    private Long totalUsers;
    
    @ApiModelProperty(value = "已授信用户数")
    private Long creditedUsers;
    
    @ApiModelProperty(value = "申请中用户数")
    private Long applyingUsers;
    
    @ApiModelProperty(value = "未授信用户数")
    private Long uncreditedUsers;
    
    @ApiModelProperty(value = "游客数量")
    private Long guestUsers;
    
    @ApiModelProperty(value = "管理员数量")
    private Long adminUsers;
    
    @ApiModelProperty(value = "员工数量")
    private Long employeeUsers;
    
    @ApiModelProperty(value = "总授信额度")
    private BigDecimal totalCreditAmount;
    
    @ApiModelProperty(value = "已使用授信额度")
    private BigDecimal usedCreditAmount;
    
    @ApiModelProperty(value = "剩余授信额度")
    private BigDecimal remainingCreditAmount;
    
    @ApiModelProperty(value = "总评估额度")
    private BigDecimal totalAssessmentAmount;
    
    @ApiModelProperty(value = "授信使用率")
    private BigDecimal creditUtilizationRate;
    
    @ApiModelProperty(value = "授信覆盖率")
    private BigDecimal creditCoverageRate;
} 