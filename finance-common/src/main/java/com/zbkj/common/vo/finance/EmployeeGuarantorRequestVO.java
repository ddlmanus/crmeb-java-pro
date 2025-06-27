package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 员工担保人请求VO
 */
@Data
@ApiModel(value = "EmployeeGuarantorRequestVO", description = "员工担保人请求参数")
public class EmployeeGuarantorRequestVO {
    
    /**
     * 员工ID
     */
    @ApiModelProperty(value = "用户ID", required = true)
    @NotNull(message = "用户IDID不能为空")
    private Integer userId;
    
    /**
     * 担保人列表
     */
    @ApiModelProperty(value = "担保人列表")
    @Valid
    private List<GuarantorVO> guarantors;
} 