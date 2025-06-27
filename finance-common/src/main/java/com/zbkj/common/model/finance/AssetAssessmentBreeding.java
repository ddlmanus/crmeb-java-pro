package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 资产评估养殖品种关联实体类
 */
@Data
@TableName("eb_asset_assessment_breeding")
public class AssetAssessmentBreeding {
    
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 资产评估ID
     */
    @ApiModelProperty(value = "资产评估ID")
    private String assessmentId;

    /**
     * 养殖场code
     */
    @ApiModelProperty(value = "养殖场code")
    private String farmCode;
    /**
     * 养殖场名称
     */
    @ApiModelProperty(value = "养殖场名称")
    private String farmName;
    
    /**
     * 养殖品种名称
     */
    @ApiModelProperty(value = "养殖品种名称")
    private String breedName;
    
    /**
     * 养殖品种类型
     */
    @ApiModelProperty(value = "养殖品种类型")
    private String breedType;
    
    /**
     * 存栏数量
     */
    @ApiModelProperty(value = "存栏数量")
    private Integer stockQuantity;

    /**
     * 评估单位
     */
    @ApiModelProperty(value = "评估单位")
    private String assessmentUnit;
    /**
     * 评估单价
     */
    @ApiModelProperty(value = "评估单价")
    private BigDecimal assessmentPrice;

    /**
     * 授信系数
     */
    @ApiModelProperty(value = "授信系数")
    private BigDecimal creditCoefficient;

    /**
     * 评估总金额
     */
    @ApiModelProperty(value = "评估总金额")
    private BigDecimal assessmentTotalPrice;


    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
} 