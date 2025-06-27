package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 存栏数据同步配置模型
 */
@Data
@TableName("eb_livestock_sync_config")
@ApiModel(value = "LivestockSyncConfig", description = "存栏数据同步配置")
public class LivestockSyncConfig {

    @ApiModelProperty(value = "配置ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "养殖场编码")
    private String farmCode;

    @ApiModelProperty(value = "养殖场名称")
    private String farmName;

    @ApiModelProperty(value = "是否启用同步")
    private Integer enableSync;

    @ApiModelProperty(value = "同步频率（cron表达式）")
    private String cronExpression;

    @ApiModelProperty(value = "最后同步时间")
    private Date lastSyncTime;

    @ApiModelProperty(value = "同步状态：0-失败，1-成功，2-进行中")
    private Integer syncStatus;

    @ApiModelProperty(value = "同步结果消息")
    private String syncMessage;

    @ApiModelProperty(value = "同步数据条数")
    private Integer syncDataCount;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "删除标识")
    private Integer deleteFlag;
} 