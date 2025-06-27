package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 养殖场机构信息实体类
 */
@Data
@TableName("eb_farm_institution")
public class FarmInstitution {
    
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Integer id;

    /**
     * 所属机构ID
     */
    private String organizationId;

    /**
     * 所属机构名称
     */
    private String organizationName;

    /**
     * 所属机构编号
     */
    private String organizationCode;

    /**
     * 机构标识代码
     */
    private String farmCode;
    
    /**
     * 养殖场名称
     */
    private String farmName;
    
    /**
     * 法人代表
     */
    private String legalPerson;
    
    /**
     * 养殖场负责人电话
     */
    private String contactPhone;

    /**
     * 养殖场负责人姓名
     */
    private String contactName;
    
    /**
     * 养殖规模
     */
    private String scale;

    /**
     * 注册资本（万元）
     */
    private BigDecimal registeredCapital;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 养殖场地址
     */
    private String address;

    /**
     * 营业执照
     */
    private String businessLicense;

    /**
     * 经营范围
     */
    private String businessScope;

    /**
     * 审核状态（0:待审核 1:审核通过 2:审核拒绝）
     */
    private Integer auditStatus;
    
    /**
     * 审核意见
     */
    private String auditRemark;
    
    /**
     * 审核员ID
     */
    private String auditUserId;
    
    /**
     * 审核员姓名
     */
    private String auditUserName;
    
    /**
     * 审核时间
     */
    private Date auditTime;
    
    /**
     * 创建时间
     */
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