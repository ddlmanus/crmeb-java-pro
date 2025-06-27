package com.zbkj.common.vo.finance;

import com.zbkj.common.model.finance.FarmBreedType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.List;

/**
 * 资产评估请求VO
 */
@Data
@ApiModel(value = "AssetAssessmentRequestVO", description = "资产评估请求参数")
public class AssetAssessmentRequestVO {
    
    /**
     * 评估ID（修改时传入）
     */
    @ApiModelProperty(value = "评估ID")
    private String assessmentId;
    
    /**
     * 养殖场名称
     */
    @ApiModelProperty(value = "养殖场名称", required = true)
    @NotNull(message = "养殖场名称不能为空")
    private String farmName;
    
    /**
     * 养殖场负责人电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @ApiModelProperty(value = "养殖场负责人电话", required = true)
    @NotNull(message = "负责人电话不能为空")
    private String managerPhone;

    /**
     * 养殖场负责人姓名
     */
    @ApiModelProperty(value = "养殖场负责人姓名", required = true)
    @NotNull(message = "负责人姓名不能为空")
    private String managerName;

    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号", required = true)
    @NotNull(message = "身份证号不能为空")
    private String idNumber;
    /**
     * 统一信用代码
     */
    @ApiModelProperty(value = "统一信用代码", required = true)
    @NotNull(message = "统一信用代码不能为空")
    private String creditCode;
    
    /**
     * 养殖品种列表
     */
    @ApiModelProperty(value = "养殖品种列表", required = true)
   // @NotNull(message = "养殖品种不能为空")
    private List<FarmBreedType> breedingProducts;

    /**
     * 担保人信息
     */
    @ApiModelProperty(value = "担保人信息")
    private List<GuarantorVO> guarantors;

    /**
     * 申请金额
     */
    @ApiModelProperty(value = "申请金额", required = false)
    private BigDecimal applyAmount;

    /**
     * 评估金额
     */
    @ApiModelProperty(value = "评估金额", required = false)
    private BigDecimal assessmentAmount;

    /**
     * 申请凭证
     */
    @ApiModelProperty(value = "申请凭证")
    private String applyImages;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
} 