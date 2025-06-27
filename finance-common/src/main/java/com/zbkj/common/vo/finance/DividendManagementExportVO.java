package com.zbkj.common.vo.finance;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 分红管理导出VO
 */
@Data
public class DividendManagementExportVO {
    
    /**
     * ID
     */
    @ExcelProperty("ID")
    private String id;
    
    /**
     * 分红标题
     */
    @ExcelProperty("分红标题")
    private String dividendTitle;
    
    /**
     * 分红总金额
     */
    @ExcelProperty("分红总金额")
    private BigDecimal totalAmount;
    
    /**
     * 分红日期
     */
    @ExcelProperty("分红日期")
    private String dividendDate;
    
    /**
     * 创建用户ID
     */
    @ExcelProperty("创建用户ID")
    private Integer userId;
    
    /**
     * 机构ID
     */
    @ExcelProperty("机构ID")
    private String organizationId;
    
    /**
     * 状态
     */
    @ExcelProperty("状态")
    private Integer status;
    
    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    private String createTime;
    
    /**
     * 更新时间
     */
    @ExcelProperty("更新时间")
    private String updateTime;
    
    /**
     * 删除标志
     */
    @ExcelProperty("删除标志")
    private Integer deleteFlag;
} 