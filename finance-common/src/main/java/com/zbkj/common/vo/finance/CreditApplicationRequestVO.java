package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.List;

/**
 * 社员授信申请请求VO
 */
@Data
public class CreditApplicationRequestVO {
    /**
     * 养殖场名称
     */
    @ApiModelProperty(value = "养殖场名称")
    private String farmName;
    /**
     * 养殖场负责人电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
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
