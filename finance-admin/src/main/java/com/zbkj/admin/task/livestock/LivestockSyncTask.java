package com.zbkj.admin.task.livestock;

import com.zbkj.common.model.finance.FarmInstitution;
import com.zbkj.service.service.finance.FarmBreedTypeService;
import com.zbkj.service.service.finance.FarmInstitutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 存栏数据同步定时任务
 */
@Slf4j
@Component("LivestockSyncTask")
public class LivestockSyncTask {

    @Autowired
    private FarmInstitutionService farmInstitutionService;

    @Autowired
    private FarmBreedTypeService farmBreedTypeService;

    /**
     * 定时同步存栏数据
     * 每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void syncLivestockData() {
        log.info("开始执行存栏数据定时同步任务");
        try {
            // 从养殖机构表获取所有的养殖场编码
            List<FarmInstitution> farmInstitutions = farmInstitutionService.getAllList();
            log.info("获取到{}个养殖场机构，开始同步存栏数据", farmInstitutions.size());
            
            int successCount = 0;
            int failCount = 0;
            
            for (FarmInstitution institution : farmInstitutions) {
                try {
                    boolean result = farmBreedTypeService.syncLivestockDataByFarmCode(institution.getFarmCode());
                    if (result) {
                        successCount++;
                        log.info("养殖场{}({})存栏数据同步成功", institution.getFarmName(), institution.getFarmCode());
                    } else {
                        failCount++;
                        log.warn("养殖场{}({})存栏数据同步失败", institution.getFarmName(), institution.getFarmCode());
                    }
                } catch (Exception e) {
                    failCount++;
                    log.error("养殖场{}({})存栏数据同步异常", institution.getFarmName(), institution.getFarmCode(), e);
                }
            }
            
            log.info("存栏数据定时同步任务执行完成，成功：{}个，失败：{}个", successCount, failCount);
        } catch (Exception e) {
            log.error("存栏数据定时同步任务执行异常", e);
        }
    }

    /**
     * 每小时执行一次同步（可根据需要调整）
     * 如果需要更频繁的同步，可以启用此方法
     */
    // @Scheduled(cron = "0 0 * * * ?")
    public void syncLivestockDataHourly() {
        log.info("开始执行存栏数据小时同步任务");
        try {
            // 从养殖机构表获取所有的养殖场编码
            List<FarmInstitution> farmInstitutions = farmInstitutionService.getAllList();
            log.info("获取到{}个养殖场机构，开始同步存栏数据", farmInstitutions.size());
            
            for (FarmInstitution institution : farmInstitutions) {
                try {
                    farmBreedTypeService.syncLivestockDataByFarmCode(institution.getFarmCode());
                } catch (Exception e) {
                    log.error("养殖场{}({})存栏数据同步异常", institution.getFarmName(), institution.getFarmCode(), e);
                }
            }
            
            log.info("存栏数据小时同步任务执行完成");
        } catch (Exception e) {
            log.error("存栏数据小时同步任务执行异常", e);
        }
    }
} 