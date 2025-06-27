package com.zbkj.common.vo.finance;

import lombok.Data;

/**
 * 分红批次分页查询VO
 */
@Data
public class DividendBatchPageVO {
    
    /**
     * 页码
     */
    private Integer pageNumber = 1;
    
    /**
     * 每页数量
     */
    private Integer pageSize = 10;
    
    /**
     * 批次号
     */
    private String batchNo;
    
    /**
     * 分红名称
     */
    private String batchName;
    
    /**
     * 分红类型：0-年度分红，1-季度分红，2-月度分红，3-特别分红
     */
    private Integer dividendType;
    
    /**
     * 分红状态：0-未分配，1-已分配，2-已发放，3-已取消
     */
    private Integer batchStatus;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
} 