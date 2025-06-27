package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 资产评估实体类
 */
@Data
@TableName("eb_asset_assessment")
public class AssetAssessment {
    
    /**
     * 评估ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户名称
     */
    private String userName;

    /**
     * 评估对象 1-合作社 2-养殖户/养殖企业
     */
    private Integer assessmentType;

    /**
     * 养殖场机构ID
     */
    private Integer farmInstitutionId;
    
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
    private String idNumber;

    /**
     * 统一信用代码
     */
    private String creditCode;
    /**
     * 备注
     */
    private String remark;

    
    /**
     * 评估状态：0-草稿，1-已提交
     */
    private Integer assessmentStatus;
    
    /**
     * 是否已用于申请：0-未使用，1-已使用
     */
    private Integer isUsed;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 删除标志：0-未删除，1-已删除
     */
    private Integer deleteFlag;
} 