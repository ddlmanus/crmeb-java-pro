package com.zbkj.common.vo.finance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 牧码通存栏数据VO
 */
@Data
@ApiModel(value = "LivestockInventoryDataVO", description = "牧码通存栏数据")
public class LivestockInventoryDataVO {

    @ApiModelProperty(value = "省份编码")
    private String province;

    @ApiModelProperty(value = "城市编码")
    private String city;

    @ApiModelProperty(value = "县区编码")
    private String county;

    @ApiModelProperty(value = "乡镇编码")
    private String township;

    @ApiModelProperty(value = "养殖场编码")
    @JsonProperty("farm_code")
    private String farmCode;

    @ApiModelProperty(value = "养殖场名称")
    @JsonProperty("farm_name")
    private String farmName;

    @ApiModelProperty(value = "畜禽种类")
    @JsonProperty("livestock_type")
    private String livestockType;

    @ApiModelProperty(value = "品种")
    private String breed;

    @ApiModelProperty(value = "饲养阶段")
    @JsonProperty("breeding_stage")
    private String breedingStage;

    @ApiModelProperty(value = "上期期末存栏")
    @JsonProperty("last_period_stock")
    private String lastPeriodStock;

    @ApiModelProperty(value = "本期减少")
    @JsonProperty("current_period_out")
    private String currentPeriodOut;

    @ApiModelProperty(value = "本期增加")
    @JsonProperty("current_period_add")
    private String currentPeriodAdd;

    @ApiModelProperty(value = "本期期末存栏")
    @JsonProperty("current_period_stock")
    private String currentPeriodStock;

    @ApiModelProperty(value = "本期出栏")
    @JsonProperty("current_OFF_PEN")
    private String currentOffPen;

    @ApiModelProperty(value = "本期转出")
    @JsonProperty("current_TRANSFER_OUT")
    private String currentTransferOut;

    @ApiModelProperty(value = "本期检疫")
    @JsonProperty("current_QUARANTINE")
    private String currentQuarantine;

    @ApiModelProperty(value = "本期死亡")
    @JsonProperty("current_DEATH")
    private String currentDeath;

    @ApiModelProperty(value = "本期淘汰")
    @JsonProperty("current_PHASE_OUT")
    private String currentPhaseOut;

    @ApiModelProperty(value = "本期屠宰")
    @JsonProperty("current_KILL")
    private String currentKill;

    @ApiModelProperty(value = "创建时间")
    @JsonProperty("create_time")
    private String createTime;
} 