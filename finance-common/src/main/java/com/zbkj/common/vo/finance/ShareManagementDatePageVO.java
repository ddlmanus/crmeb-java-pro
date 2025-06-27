package com.zbkj.common.vo.finance;

import com.zbkj.common.request.PageParamRequest;
import lombok.Data;

@Data
public class ShareManagementDatePageVO extends PageParamRequest {
    //变更日期
    private String changeTime;
}
