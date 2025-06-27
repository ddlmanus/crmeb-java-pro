package com.zbkj.common.vo.finance;

import com.zbkj.common.model.finance.ActiveTransactionBreeding;
import com.zbkj.common.model.finance.ActiveTransactionDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 活体交易记录VO
 */
@Data
public class ActiveTransactionVO extends ActiveTransactionDetail {

    @ApiModelProperty(value = "活体交易记录详情列表")
    private List<ActiveTransactionBreeding> activeTransactionBreedingList;
} 