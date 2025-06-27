package com.zbkj.common.vo.finance;

import com.zbkj.common.model.finance.ActiveTransactionBreeding;
import com.zbkj.common.model.finance.ActiveTransactionDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ActiveTransactionRequestVO extends ActiveTransactionDetail {

   @ApiModelProperty(value = "活体交易记录IdList")
    private String activeTransactionIds;

    @ApiModelProperty(value = "活体交易记录详情列表")
    private List<ActiveTransactionBreeding> activeTransactionBreedingList;
}
