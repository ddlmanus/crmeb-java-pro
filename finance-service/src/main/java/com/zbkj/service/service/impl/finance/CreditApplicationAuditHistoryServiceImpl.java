package com.zbkj.service.service.impl.finance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.finance.CreditApplicationAuditHistory;
import com.zbkj.service.dao.finance.CreditApplicationAuditHistoryDao;
import com.zbkj.service.service.finance.CreditApplicationAuditHistoryService;
import org.springframework.stereotype.Service;

@Service
public class CreditApplicationAuditHistoryServiceImpl extends ServiceImpl<CreditApplicationAuditHistoryDao, CreditApplicationAuditHistory> implements CreditApplicationAuditHistoryService {
    @Override
    public CreditApplicationAuditHistory getByAppId(String id) {
        LambdaQueryWrapper<CreditApplicationAuditHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CreditApplicationAuditHistory::getApplicationId, id);
        //取最新的一条
        queryWrapper.orderByDesc(CreditApplicationAuditHistory::getCreateTime);
        queryWrapper.last("LIMIT 1");
        CreditApplicationAuditHistory creditApplicationAuditHistory = this.getOne(queryWrapper);
        return creditApplicationAuditHistory;
    }
}
