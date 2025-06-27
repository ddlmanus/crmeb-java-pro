package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 资产评估分页请求VO
 */
@Data
@ApiModel(value = "AssetAssessmentPageRequest", description = "资产评估分页请求参数")
public class AssetAssessmentPageRequest {
    
    /**
     * 页码
     */
    @ApiModelProperty(value = "页码", required = true)
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNumber;
    
    /**
     * 每页大小
     */
    @ApiModelProperty(value = "每页大小", required = true)
    @NotNull(message = "每页大小不能为空")
    @Min(value = 1, message = "每页大小不能小于1")
    private Integer pageSize;
    
    /**
     * 评估状态：0-草稿，1-已提交
     */
    @ApiModelProperty(value = "评估状态")
    private Integer assessmentStatus;
    
    /**
     * 是否已用于申请：0-未使用，1-已使用
     */
    @ApiModelProperty(value = "是否已用于申请")
    private Integer isUsed;
    
    /**
     * 养殖场名称（模糊查询）
     */
    @ApiModelProperty(value = "养殖场名称")
    private String farmName;
} 