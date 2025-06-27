package com.zbkj.common.vo.finance;

import com.zbkj.common.request.PageParamRequest;
import lombok.Data;

/**
 * 股份管理分页查询VO
 */
@Data
public class ShareManagementPageVO extends PageParamRequest {

    /**
     * 变更时间
     */
    private String changeTime;

    /**
     * 社员名称
     */
    private String userName;
    /**
     * 社员ID
     */
    private Integer userId;
} 