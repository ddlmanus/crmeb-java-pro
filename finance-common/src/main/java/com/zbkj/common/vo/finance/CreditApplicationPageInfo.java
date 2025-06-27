package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * 授信申请分页查询参数
 */
@Data
@ApiModel(value = "CreditApplicationPageInfo", description = "授信申请分页查询参数")
public class CreditApplicationPageInfo {
    
    @ApiModelProperty(value = "页码", example = "1")
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNumber = 1;
    
    @ApiModelProperty(value = "每页数量", example = "10")
    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能大于100")
    private Integer pageSize = 10;
    
    @ApiModelProperty(value = "审核状态：0-待审核，1-拒绝，2-通过", example = "0")
    @Min(value = 0, message = "审核状态值错误")
    @Max(value = 2, message = "审核状态值错误")
    private Integer auditStatus;
    
    @ApiModelProperty(value = "用户ID")
    private String userId;
}
