package com.zbkj.admin.task.activetransaction;

import com.zbkj.common.model.finance.FarmInstitution;
import com.zbkj.service.service.finance.ActiveTransactionService;
import com.zbkj.service.service.finance.FarmInstitutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Slf4j
@Component("ActiveTransactionTask")
public class ActiveTransactionTask {

    @Autowired
    private ActiveTransactionService activeTransactionService;
    @Autowired
    private FarmInstitutionService farmInstitutionService;
    public void activeTransaction(){
        log.info("同步牧码通活体交易记录进厂数据");
        List<FarmInstitution> farmInstitutionNoAdmin = farmInstitutionService.getAllList();
        for (FarmInstitution farmInstitution : farmInstitutionNoAdmin) {
            //同步近一年到现在的数据
            activeTransactionService.syncEntryDataToTransaction(farmInstitution.getFarmCode(), "2021-01-01", new SimpleDateFormat("YYYY-mm-dd").format(new Date()));
            activeTransactionService.syncExitDataToTransaction(farmInstitution.getFarmCode(), "2021-01-01", new SimpleDateFormat("YYYY-mm-dd").format(new Date()));
        }
        log.info("同步牧码通活体交易记录进厂数据完成");
    }
}
