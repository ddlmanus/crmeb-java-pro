package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.finance.CreditApplicationAuditHistory;

public interface CreditApplicationAuditHistoryService extends IService<CreditApplicationAuditHistory> {
    CreditApplicationAuditHistory getByAppId(String id);
}
