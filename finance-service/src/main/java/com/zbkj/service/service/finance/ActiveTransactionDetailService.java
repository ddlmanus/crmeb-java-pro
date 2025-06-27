package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.finance.ActiveTransactionDetail;

public interface ActiveTransactionDetailService extends IService<ActiveTransactionDetail> {
    ActiveTransactionDetail getbyActiveID(String id);
}
