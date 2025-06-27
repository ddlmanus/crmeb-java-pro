package com.zbkj.common.model.sync;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 数据同步记录表
 * </p>
 *
 * @author HZW
 * @since 2025-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_sync_record")
@ApiModel(value="SyncRecord对象", description="数据同步记录表")
public class SyncRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "同步类型：goods-商品，sku-商品SKU，brand-品牌，category-分类")
    private String syncType;

    @ApiModelProperty(value = "同步状态：0-进行中，1-成功，2-失败")
    private Integer syncStatus;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "最后同步时间")
    private Date lastSyncTime;

    @ApiModelProperty(value = "同步数量")
    private Integer syncCount;

    @ApiModelProperty(value = "成功数量")
    private Integer successCount;

    @ApiModelProperty(value = "失败数量")
    private Integer failCount;

    @ApiModelProperty(value = "错误信息")
    private String errorMessage;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
} 