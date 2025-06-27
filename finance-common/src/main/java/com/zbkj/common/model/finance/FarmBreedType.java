package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 养殖品种类型管理模型
 */
@Data
@TableName("eb_farm_breed_type")
public class FarmBreedType {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 养殖场名称
     *
     */
    @ApiModelProperty(value = "养殖场名称")
    private String farmName;

    /**
     * 养殖场ID
     */
    @ApiModelProperty(value = "养殖场ID")
    private String farmId;

    /**
     * 养殖场code
     */
    @ApiModelProperty(value = "养殖场code")
    private String farmCode;
    /**
     * 养殖品种名称
     */
    @ApiModelProperty(value = "养殖品种名称")
    private String breedName;

    /**
     * 养殖品种ID
     */
    @ApiModelProperty(value = "养殖品种ID")
    private String breedId;

    /**
     * 养殖品种类型
     */
    @ApiModelProperty(value = "养殖品种类型")
    private String breedType;

    /**
     * 存栏量
     */
    @ApiModelProperty(value = "存栏量")
    private Integer stockQuantity;
    /**
     * 生长阶段
     */
    @ApiModelProperty(value = "生长阶段")
    private String growthStage;
    /**
     * 创建时间
     * */
    private String createTime;
    /**
     * 更新时间
     * */
    private String updateTime;
    private Integer deleteFlag;
}
