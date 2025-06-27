package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.List;

/**
 * 授信申请暂存/提交请求VO
 */
@Data
public class CreditApplicationDraftVO {
    /**
     * 申请ID（暂存时可能为空，修改时必填）
     */
    @ApiModelProperty(value = "申请ID")
    private String id;
    
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
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 养殖品种列表
     */
    @ApiModelProperty(value = "养殖品种列表")
    private List<BreedingRequest> breedingProducts;
    
    /**
     * 申请状态：0-暂存，1-提交
     */
    @ApiModelProperty(value = "申请状态：0-暂存，1-提交")
    private Integer applyStatus;
} 