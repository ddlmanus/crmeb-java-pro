package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 机构管理实体类
 */
@Data
@TableName("eb_organization")
public class Organization {
    
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 机构编号
     */
    private String orgCode;

    /**
     * 所属区域（省市县）
     */
    private String region;

    /**
     * 省份ID
     */
    private Integer provinceId;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市ID
     */
    private Integer cityId;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县ID
     */
    private Integer districtId;

    /**
     * 区县
     */
    private String district;

    /**
     * 上级机构ID
     */
    private String parentOrgId;

    /**
     * 上级机构名称
     */
    private String parentOrgName;

    /**
     * 上级机构编号
     */
    private String parentOrgCode;

    /**
     * 机构分类ID
     */
    private String categoryId;

    /**
     * 机构分类名称
     */
    private String categoryName;

    /**
     * 机构分类编码
     */
    private String categoryCode;

    /**
     * 机构层级（1:一级 2:二级 3:三级）
     */
    private Integer level;

    /**
     * 负责人姓名
     */
    private String contactName;

    /**
     * 负责人电话
     */
    private String contactPhone;

    /**
     * 机构地址
     */
    private String address;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态（0:禁用 1:启用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标志（0:未删除 1:已删除）
     */
    private Integer deleteFlag;
} 