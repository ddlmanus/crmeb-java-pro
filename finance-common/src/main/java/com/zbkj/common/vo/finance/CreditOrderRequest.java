package com.zbkj.common.vo.finance;

import com.zbkj.common.request.PageParamRequest;
import lombok.Data;

@Data
public class CreditOrderRequest  extends PageParamRequest {
    private String creditOrderNo;
    private Integer userId;
    private Integer repaymentStatus;
}
