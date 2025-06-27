package com.zbkj.service.service.impl.finance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.finance.ActiveTransactionBreeding;
import com.zbkj.common.vo.finance.ActiveTransactionVO;
import com.zbkj.service.dao.finance.ActiveTransactionBreedingDao;
import com.zbkj.service.service.finance.ActiveTransactionBreedingService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ActiveTransactionBreedingServiceImpl extends ServiceImpl<ActiveTransactionBreedingDao, ActiveTransactionBreeding> implements ActiveTransactionBreedingService {
    @Override
    public List<ActiveTransactionBreeding> getByActiveTransactionId(String id) {
        LambdaQueryWrapper<ActiveTransactionBreeding> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActiveTransactionBreeding::getActiveTransactionId, id);
        return this.list(queryWrapper);
    }
}
