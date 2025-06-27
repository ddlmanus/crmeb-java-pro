package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;

/**
 * 养殖品种请求VO
 */
@Data
public class BreedingProductRequestVO {
    
    /**
     * ID（编辑时使用）
     */
    @ApiModelProperty(value = "ID")
    private String id;

    /**
     * 养殖场编码
     */
    @NotBlank(message = "养殖场编码不能为空")
    @ApiModelProperty(value = "养殖场编码", required = true)
    private String farmCode;

    /**
     * 养殖场名称
     */
    @NotBlank(message = "养殖场名称不能为空")
    @ApiModelProperty(value = "养殖场名称", required = true)
    private String farmName;

    /**
     * 品种编号
     */
    @NotBlank(message = "品种编号不能为空")
    @ApiModelProperty(value = "品种编号", required = true)
    private String code;
    
    /**
     * 品种名称
     */
    @NotBlank(message = "品种名称不能为空")
    @ApiModelProperty(value = "品种名称", required = true)
    private String name;
    
    /**
     * 品种编码
     */
    @ApiModelProperty(value = "品种编码")
    private String splitSpecies;
    
    /**
     * 存栏量
     */
    @NotNull(message = "存栏量不能为空")
    @Min(value = 0, message = "存栏量不能小于0")
    @ApiModelProperty(value = "存栏量", required = true)
    private Integer stockQuantity;
    
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
} 