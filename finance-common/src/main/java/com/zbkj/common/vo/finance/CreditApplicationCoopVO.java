package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 合作社授信申请请求VO
 */
@Data
public class CreditApplicationCoopVO {
    
    /**
     * 申请金额
     */
    @ApiModelProperty(value = "申请金额")
    private BigDecimal applyAmount;

    /**
     * 申请凭证
     */
    @ApiModelProperty(value = "申请凭证")
    private String applyCredentials;

}
