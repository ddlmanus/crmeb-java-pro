package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 授信申请状态检查VO
 */
@Data
@ApiModel(value = "CreditApplicationStatusVO", description = "授信申请状态检查响应")
public class CreditApplicationStatusVO {
    
    /**
     * 是否可以申请
     */
    @ApiModelProperty(value = "是否可以申请")
    private Boolean canApply;
    
    /**
     * 不可申请的原因
     */
    @ApiModelProperty(value = "不可申请的原因")
    private String reason;
    
    /**
     * 当前申请状态：0-待审核，1-拒绝，2-通过
     */
    @ApiModelProperty(value = "当前申请状态")
    private Integer currentStatus;
    
    /**
     * 当前申请状态描述
     */
    @ApiModelProperty(value = "当前申请状态描述")
    private String currentStatusDesc;
    
    /**
     * 最近申请时间
     */
    @ApiModelProperty(value = "最近申请时间")
    private Date lastApplyTime;
    
    /**
     * 最近申请ID
     */
    @ApiModelProperty(value = "最近申请ID")
    private String lastApplicationId;
    
    /**
     * 是否有有效的资产评估
     */
    @ApiModelProperty(value = "是否有有效的资产评估")
    private Boolean hasValidAssessment;
    
    /**
     * 可用的资产评估ID
     */
    @ApiModelProperty(value = "可用的资产评估ID")
    private String availableAssessmentId;
} 