package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 授信申请详情响应VO
 */
@Data
public class CreditApplicationDetailVO {
    /**
     * 申请ID
     */
    @ApiModelProperty(value = "申请ID")
    private String id;
    
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    
    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    private String userName;
    
    /**
     * 养殖场名称
     */
    @ApiModelProperty(value = "养殖场名称")
    private String farmName;
    
    /**
     * 养殖场负责人电话
     */
    @ApiModelProperty(value = "养殖场负责人电话")
    private String managerPhone;

    /**
     * 养殖场负责人姓名
     */
    @ApiModelProperty(value = "养殖场负责人姓名")
    private String managerName;

    /**
     * 身份证号/统一社会信用代码
     */
    @ApiModelProperty(value = "身份证号/统一社会信用代码")
    private String idNumber;
    
    /**
     * 申请金额
     */
    @ApiModelProperty(value = "申请金额")
    private BigDecimal applyAmount;

    /**
     * 申请期限（月）
     */
    @ApiModelProperty(value = "申请期限（月）")
    private Integer creditPeriod;
    
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    
    /**
     * 申请状态：0-暂存，1-已提交
     */
    @ApiModelProperty(value = "申请状态：0-暂存，1-已提交")
    private Integer applyStatus;
    
    /**
     * 审核状态：0-待审核，1-拒绝，2-通过
     */
    @ApiModelProperty(value = "审核状态：0-待审核，1-拒绝，2-通过")
    private Integer auditStatus;
    
    /**
     * 申请时间
     */
    @ApiModelProperty(value = "申请时间")
    private Date applyTime;
    
    /**
     * 审核时间
     */
    @ApiModelProperty(value = "审核时间")
    private Date auditTime;
    
    /**
     * 审核备注
     */
    @ApiModelProperty(value = "审核备注")
    private String auditRemark;
    
    /**
     * 担保人列表
     */
    @ApiModelProperty(value = "担保人列表")
    private List<GuarantorVO> guarantors;

    /**
     * 养殖品种列表
     */
    @ApiModelProperty(value = "养殖品种列表")
    private List<BreedingRequest> breedingProducts;
} 