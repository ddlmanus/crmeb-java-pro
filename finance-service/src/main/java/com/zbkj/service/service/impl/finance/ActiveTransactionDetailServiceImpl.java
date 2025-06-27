package com.zbkj.service.service.impl.finance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.finance.ActiveTransactionDetail;
import com.zbkj.service.dao.finance.ActiveTransactionDetailDao;
import com.zbkj.service.service.finance.ActiveTransactionDetailService;
import org.springframework.stereotype.Service;

@Service
public class ActiveTransactionDetailServiceImpl extends ServiceImpl<ActiveTransactionDetailDao, ActiveTransactionDetail> implements ActiveTransactionDetailService {
    @Override
    public ActiveTransactionDetail getbyActiveID(String id) {
        LambdaQueryWrapper<ActiveTransactionDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActiveTransactionDetail::getActiveTransactionId, id);
        return this.getOne(queryWrapper);
    }
}
