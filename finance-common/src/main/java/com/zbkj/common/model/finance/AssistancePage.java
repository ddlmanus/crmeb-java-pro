package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 互助首页实体类
 *
 * @author Chopper
 * @since 2023/2/24 10:06 下午
 */
@Data
@TableName("eb_assistance_page")
@ApiModel(value = "互助首页")
public class AssistancePage {

    @TableId
    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "互助类型：养殖户互助，养殖企业互助，合作社互助，经纪人互助，供应商互助")
    private String assistanceType;

    @ApiModelProperty(value = "可用额度")
    private Double availableAmount;

    @ApiModelProperty(value = "消费记录")
    private String consumptionRecord;

    @ApiModelProperty(value = "还款记录")
    private String repaymentRecord;

    @ApiModelProperty(value = "快捷入口")
    private String quickAccess;

    @ApiModelProperty(value = "创建时间", hidden = true)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "创建者", hidden = true)
    @CreatedBy
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "更新时间", hidden = true)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "更新者", hidden = true)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
} 