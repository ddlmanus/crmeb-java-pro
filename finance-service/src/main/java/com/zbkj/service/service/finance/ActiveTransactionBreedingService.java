package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.finance.ActiveTransactionBreeding;
import com.zbkj.common.vo.finance.ActiveTransactionVO;

import java.util.List;

public interface ActiveTransactionBreedingService extends IService<ActiveTransactionBreeding> {
    List<ActiveTransactionBreeding> getByActiveTransactionId(String id);
}
