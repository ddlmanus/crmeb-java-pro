package com.zbkj.common.vo.finance;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 分红明细导出VO
 */
@Data
public class DividendDetailExportVO {
    
    /**
     * ID
     */
    @ExcelProperty("ID")
    private String id;
    
    /**
     * 分红管理ID
     */
    @ExcelProperty("分红管理ID")
    private String dividendId;
    
    /**
     * 用户ID
     */
    @ExcelProperty("用户ID")
    private Integer userId;
    
    /**
     * 社员名称
     */
    @ExcelProperty("社员名称")
    private String memberName;
    
    /**
     * 持有比例
     */
    @ExcelProperty("持有比例")
    private BigDecimal holdingRatio;
    
    /**
     * 分红金额
     */
    @ExcelProperty("分红金额")
    private BigDecimal dividendAmount;
    
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