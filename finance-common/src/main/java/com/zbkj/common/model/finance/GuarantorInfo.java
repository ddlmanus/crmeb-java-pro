package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 担保人信息实体类
 */
@Data
@TableName("eb_guarantee_info")
public class GuarantorInfo {
    
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 用户ID（关联的用户）
     */
    @ApiModelProperty (value = "用户ID（关联的用户）")
    private Integer userId;
    
    /**
     * 担保人姓名
     */
    @ApiModelProperty (value = "担保人姓名")
    private String name;
    
    /**
     * 担保人身份证号
     */
    @ApiModelProperty (value = "担保人身份证号")
    private String idNumber;
    
    /**
     * 担保人手机号
     */
    @ApiModelProperty (value = "担保人手机号")
    private String mobile;

    /**
     * 电子签名
     */
    @ApiModelProperty (value = "电子签名")
    private String signature;

    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private java.util.Date createTime;
    
    /**
     * 更新时间
     */
    private java.util.Date updateTime;
} 