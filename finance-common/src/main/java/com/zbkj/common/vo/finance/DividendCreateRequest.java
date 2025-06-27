package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 分红创建请求VO
 */
@Data
@ApiModel(value = "DividendCreateRequest", description = "分红创建请求")
public class DividendCreateRequest {

    @ApiModelProperty(value = "分红标题", required = true)
    @NotBlank(message = "分红标题不能为空")
    private String dividendTitle;

    @ApiModelProperty(value = "分红总金额", required = true)
    @NotNull(message = "分红总金额不能为空")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "分红明细列表", required = true)
    @NotNull(message = "分红明细不能为空")
    @Valid
    private List<DividendDetailRequest> dividendDetails;

    @Data
    @ApiModel(value = "DividendDetailRequest", description = "分红明细请求")
    public static class DividendDetailRequest {
        
        @ApiModelProperty(value = "用户ID", required = true)
        @NotNull(message = "用户ID不能为空")
        private Integer userId;
        
        @ApiModelProperty(value = "社员名称", required = true)
        @NotBlank(message = "社员名称不能为空")
        private String memberName;
        
        @ApiModelProperty(value = "持有比例", required = true)
        @NotNull(message = "持有比例不能为空")
        private BigDecimal holdingRatio;
        
        @ApiModelProperty(value = "分红金额", required = true)
        @NotNull(message = "分红金额不能为空")
        private BigDecimal dividendAmount;
    }
} 