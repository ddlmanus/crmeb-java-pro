package com.zbkj.common.vo.finance;

import com.zbkj.common.model.finance.AssetAssessmentBreeding;
import com.zbkj.common.model.finance.BreedingProduct;
import com.zbkj.common.model.finance.GuarantorInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 授信申请分页VO
 */
@Data
public class CreditApplicationPage {
    
    /**
     * ID
     */
    private String id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户类型
     */
    private Integer userType;
    
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 机构ID
     */
    private String organizationId;
    
    /**
     * 养殖机构类型
     */
    private String farmType;
    
    /**
     * 养殖场名称
     */
    private String farmName;
    
    /**
     * 养殖场负责人电话
     */
    private String managerPhone;
    
    /**
     * 养殖场负责人姓名
     */
    private String managerName;
    
    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号")
    private String idNumber;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value = "统一社会信用代码")
    private String creditCode;
    
    /**
     * 申请类型：0-社员申请，1-合作社申请
     */
    @ApiModelProperty(value = "申请类型：0-社员申请，1-合作社申请")
    private Integer applyType;
    
    /**
     * 申请金额
     */
    @ApiModelProperty(value = "申请金额")
    private BigDecimal applyAmount;
    
    /**
     * 授信总额度
     */
    @ApiModelProperty(value = "授信总额度")
    private BigDecimal totalAmount;
    
    /**
     * 可用授信额度
     */
    @ApiModelProperty(value = "可用授信额度")
    private BigDecimal availableCreditAmount;
    
    /**
     * 总待还款金额
     */
    private BigDecimal totalRepaymentAmount;
    
    /**
     * 授信利率
     */
    private BigDecimal creditRatio;
    
    /**
     * POS卡号
     */
    @ApiModelProperty(value = "POS卡号")
    private String cardNumber;
    
    /**
     * 授信开始时间
     */
    private Date creditStartTime;
    
    /**
     * 授信期限（单位：月）
     */
    @ApiModelProperty(value = "授信期限（单位：月）")
    private Integer creditPeriod;
    
    /**
     * 审核状态：0-待审核，1-拒绝，2-通过
     */
    @ApiModelProperty(value = "审核状态：0-待审核，1-拒绝，2-通过")
    private Integer auditStatus;
    
    /**
     * 审核备注
     */
    @ApiModelProperty(value = "审核备注")
    private String auditRemark;
    
    /**
     * 审核时间
     */
    @ApiModelProperty(value = "审核时间")
    private Date auditTime;
    
    /**
     * 申请时间
     */
    @ApiModelProperty(value = "申请时间")
    private Date applyTime;
    
    /**
     * 申请状态：0-暂存，1-已提交
     */

    @ApiModelProperty(value = "申请状态：0-暂存，1-已提交")
    private Integer applyStatus;
    
    /**
     * 备注
     */
    private String applyRemark;
    private String remark;
    
    /**
     * 资产评估ID
     */
    @ApiModelProperty(value = "资产评估ID")
    private String assessmentId;
    
    /**
     * 担保人列表
     */
    private List<GuarantorInfo> guarantors;

    /**
     * 养殖品种列表
     */
    private List<BreedingProduct> breedingProducts;
    
    /**
     * 资产评估品种列表
     */
    private List<AssetAssessmentBreeding> assessmentBreedingList;
}
