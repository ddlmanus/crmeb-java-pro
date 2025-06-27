package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 股份管理实体类
 */
@Data
@TableName("eb_share_management")
public class ShareManagement {
    
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 社员名称
     */
    private String memberName;
    
    /**
     * 持有比例
     */
    private BigDecimal holdingRatio;
    
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
    /**
     * 所属机构
     */
    private String organizationId;

    /**
     * 变更日期ID
     */
    private String changeDateId;
} 