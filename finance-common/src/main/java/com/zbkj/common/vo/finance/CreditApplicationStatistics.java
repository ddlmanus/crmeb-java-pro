package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 授信申请统计信息VO
 */
@Data
@ApiModel(value = "CreditApplicationStatistics", description = "授信申请统计信息")
public class CreditApplicationStatistics {
    
    @ApiModelProperty(value = "总申请数")
    private Long totalCount = 0L;
    
    @ApiModelProperty(value = "待审核数量")
    private Long pendingCount = 0L;
    
    @ApiModelProperty(value = "已通过数量")
    private Long approvedCount = 0L;
    
    @ApiModelProperty(value = "已拒绝数量")
    private Long rejectedCount = 0L;
    
    @ApiModelProperty(value = "总授信金额")
    private BigDecimal totalCreditAmount = BigDecimal.ZERO;
    
    @ApiModelProperty(value = "已使用授信金额")
    private BigDecimal usedCreditAmount = BigDecimal.ZERO;
    
    @ApiModelProperty(value = "可用授信金额")
    private BigDecimal availableCreditAmount = BigDecimal.ZERO;
    
    @ApiModelProperty(value = "申请总金额")
    private BigDecimal totalApplicationAmount = BigDecimal.ZERO;
    
    @ApiModelProperty(value = "通过金额")
    private BigDecimal approvedAmount = BigDecimal.ZERO;
    
    @ApiModelProperty(value = "通过率")
    private Double approvalRate = 0.0;
    
    /**
     * 月度统计数据
     */
    @ApiModelProperty(value = "月度统计数据")
    private List<MonthlyStatistics> monthlyStats;
    
    /**
     * 申请类型统计
     */
    @ApiModelProperty(value = "申请类型统计")
    private List<TypeStatistics> typeStats;
    
    /**
     * 月度统计信息
     */
    @Data
    public static class MonthlyStatistics {
        @ApiModelProperty(value = "月份")
        private String month;
        
        @ApiModelProperty(value = "申请总数")
        private Long totalCount = 0L;
        
        @ApiModelProperty(value = "待审核数量")
        private Long pendingCount = 0L;
        
        @ApiModelProperty(value = "已通过数量")
        private Long approvedCount = 0L;
        
        @ApiModelProperty(value = "已拒绝数量")
        private Long rejectedCount = 0L;
        
        @ApiModelProperty(value = "申请总金额")
        private BigDecimal totalAmount = BigDecimal.ZERO;
        
        @ApiModelProperty(value = "通过金额")
        private BigDecimal approvedAmount = BigDecimal.ZERO;
        
        @ApiModelProperty(value = "通过率")
        private Double approvalRate = 0.0;
    }
    
    /**
     * 类型统计信息
     */
    @Data
    public static class TypeStatistics {
        @ApiModelProperty(value = "类型名称")
        private String typeName;
        
        @ApiModelProperty(value = "类型值")
        private Integer typeValue;
        
        @ApiModelProperty(value = "数量")
        private Long count = 0L;
    }
} 