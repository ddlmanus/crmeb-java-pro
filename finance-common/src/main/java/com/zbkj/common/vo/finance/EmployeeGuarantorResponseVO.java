package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 员工担保人响应VO
 */
@Data
@ApiModel(value = "EmployeeGuarantorResponseVO", description = "员工担保人响应")
public class EmployeeGuarantorResponseVO {
    
    /**
     * 员工ID
     */
    @ApiModelProperty(value = "员工ID")
    private Integer employeeId;
    
    /**
     * 员工姓名
     */
    @ApiModelProperty(value = "员工姓名")
    private String employeeName;
    
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    
    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String userName;
    
    /**
     * 担保人列表
     */
    @ApiModelProperty(value = "担保人列表")
    private List<GuarantorVO> guarantors;
} 