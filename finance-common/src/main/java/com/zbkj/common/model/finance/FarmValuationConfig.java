package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 养殖场评估价值配置表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_farm_valuation_config")
@ApiModel(value = "FarmValuationConfig", description = "养殖场评估价值配置")
public class FarmValuationConfig {

    @ApiModelProperty(value = "配置ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "养殖场ID")
    private Integer farmId;
    @ApiModelProperty(value = "养殖场名称")
    private String farmName;
    @ApiModelProperty(value = "养殖场编码")
    private String farmCode;
    @ApiModelProperty(value = "养殖品种")
    private String breedingName;

    @ApiModelProperty(value = "品种编码")
    private String breedingCode;

    @ApiModelProperty(value = "品种类型")
    private String breedingType;

    @ApiModelProperty(value = "单位名称")
    private String unitName;

    @ApiModelProperty(value = "单价（元）")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "状态 0-禁用 1-启用")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "删除标识 0-未删除 1-已删除")
    private Integer deleteFlag;
} 