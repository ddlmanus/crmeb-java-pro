package com.zbkj.common.vo.finance;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 股份管理导出VO
 */
@Data
public class ShareManagementExportVO {
    
    /**
     * ID
     */
    @ExcelProperty("ID")
    private String id;
    
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
    
    /**
     * 所属机构
     */
    @ExcelProperty("所属机构")
    private String organizationId;
} 