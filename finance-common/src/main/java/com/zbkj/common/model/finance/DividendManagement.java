package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 分红管理实体类
 */
@Data
@TableName("eb_dividend_management")
public class DividendManagement {
    
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 分红标题
     */
    private String dividendTitle;
    
    /**
     * 分红总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 分红日期
     */
    private Date dividendDate;
    
    /**
     * 创建用户ID
     */
    private Integer userId;
    /**
     * 机构ID
     */
    private String organizationId;
    
    /**
     * 状态（0:草稿 1:已发布 2:已完成）
     */
    private Integer status;
    
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