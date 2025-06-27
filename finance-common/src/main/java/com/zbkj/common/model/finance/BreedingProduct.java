package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 养殖品种实体类
 */
@Data
@TableName("eb_breeding_product")
public class BreedingProduct {
    
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 养殖场编码
     */
    @ApiModelProperty(value = "养殖场code")
    private String farmCode;

    /**
     * y养殖场名称
     */
    @ApiModelProperty(value = "养殖场名称")
    private String farmName;

    /**
     * 品种编号
     */
    @ApiModelProperty(value = "品种编号")
    private String code;
    
    /**
     * 品种名称
     */
    @ApiModelProperty(value = "品种名称")
    private String name;
    
    /**
     * 品种编码
     */
    @ApiModelProperty(value = "品种编码")
    private String splitSpecies;
    
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    
    /**
     * 存栏量
     */
    @ApiModelProperty(value = "存栏量")
    private Integer stockQuantity;
    
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 删除标志
     */
    private Integer deleteFlag;
}
