package com.zbkj.common.vo.finance;

import lombok.Data;

/**
 * 股份分红记录分页查询VO
 */
@Data
public class DividendRecordPageVO {
    
    /**
     * 页码
     */
    private Integer pageNumber = 1;
    
    /**
     * 每页数量
     */
    private Integer pageSize = 10;
    
    /**
     * 分红批次号
     */
    private String batchNo;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 分红状态：0-待发放，1-已发放，2-已取消
     */
    private Integer dividendStatus;
    
    /**
     * 分红类型：0-年度分红，1-季度分红，2-月度分红，3-特别分红
     */
    private Integer dividendType;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
} 