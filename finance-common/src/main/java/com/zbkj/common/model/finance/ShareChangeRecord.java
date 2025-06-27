package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 股份变更记录实体类
 */
@Data
@TableName("eb_share_change_record")
public class ShareChangeRecord {
    
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
    private BigDecimal ratio;

    /**
     * 变更原因
     */
    private String changeReason;
    
    /**
     * 变更日期
     */
    private Date changeDate;
    
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
     * 变更次数
     */
    private Integer changeCount;

    /**
     * 变更日期ID
     */
    private String changeDateId;

    /**
     * 股份变更ID
     */
    private String shareManageId;
}
