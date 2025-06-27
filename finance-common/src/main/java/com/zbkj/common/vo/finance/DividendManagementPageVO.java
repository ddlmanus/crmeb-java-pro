package com.zbkj.common.vo.finance;

import com.github.pagehelper.page.PageParams;
import com.zbkj.common.request.PageParamRequest;
import lombok.Data;

/**
 * 分红管理分页查询VO
 */
@Data
public class DividendManagementPageVO  extends PageParamRequest {

    /**
     * 分红时间
     */
    private String dividendDate;
    /**
     * 分红标题
     */
    private String dividendTitle;
} 