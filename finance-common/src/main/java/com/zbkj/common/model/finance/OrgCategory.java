package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 养殖机构分类实体类
 */
@Data
@TableName("eb_org_category")
public class OrgCategory {
    
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 分类代码
     */
    private String code;

    /**
     * 分类类型id（如：养殖场、合作社等）
     */
    private String typeCode;

    /**
     * 分类类型名称
     */
    private String typeName;

    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 父级分类代码
     */
    private String parentCode;
    
    /**
     * 层级（1:一级 2:二级 3:三级）
     */
    private Integer level;
    
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
     * 管理员ID（负责管理此分类的管理员）
     */
    private String managerId;
    
    /**
     * 管理员姓名
     */
    private String managerName;
    
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
