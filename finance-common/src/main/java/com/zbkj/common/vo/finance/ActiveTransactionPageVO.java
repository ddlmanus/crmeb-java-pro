package com.zbkj.common.vo.finance;

import com.zbkj.common.request.PageParamRequest;
import lombok.Data;

/**
 * 活体交易记录分页查询VO
 */
@Data
public class ActiveTransactionPageVO  extends PageParamRequest {

    /**
     * 交易类型
     */
    private Integer transactionType;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
} 