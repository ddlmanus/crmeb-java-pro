package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 活体交易记录实体类
 */
@Data
@TableName("eb_active_transaction")
public class ActiveTransaction {
    
    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 养殖场编码
     */
    private String farmCode;
    
    /**
     * 养殖场名称
     */
    private String farmName;
    
    /**
     * 交易类型：0-进场，1-离场
     */
    private Integer transactionType;
    
    /**
     * 交易日期
     */
    private Date transactionDate;
    
    /**
     * 品种
     */
    private String breed;
    
    /**
     * 品种编码
     */
    private String breedCode;
    
    /**
     * 畜种
     */
    private String livestock;
    
    /**
     * 畜种类型
     */
    private String livestockType;
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 自繁数量（进场时使用）
     */
    private Integer selfBreedQuantity;
    
    /**
     * 调入数量（进场时使用）
     */
    private Integer transferredInQuantity;
    
    /**
     * 出栏数量（离场时使用）
     */
    private Integer slaughteredQuantity;
    
    /**
     * 调出数量（离场时使用）
     */
    private Integer transferredOutQuantity;
    
    /**
     * 隔离数量（离场时使用）
     */
    private Integer isolatedQuantity;
    
    /**
     * 死亡数量（离场时使用）
     */
    private Integer deadQuantity;
    
    /**
     * 淘汰数量（离场时使用）
     */
    private Integer eliminationQuantity;
    
    /**
     * 扑杀数量（离场时使用）
     */
    private Integer killQuantity;
    
    /**
     * 外部ID（原始数据ID）
     */
    private String externalId;
    
    /**
     * 交易状态：0-正常，1-已删除
     */
    private Integer transactionStatus;
    
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
     * 删除标志
     */
    private Integer deleteFlag;
} 