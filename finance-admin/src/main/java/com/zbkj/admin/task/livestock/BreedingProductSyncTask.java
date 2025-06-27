package com.zbkj.admin.task.livestock;

import com.zbkj.common.model.finance.FarmInstitution;
import com.zbkj.service.service.finance.BreedingProductService;
import com.zbkj.service.service.finance.FarmInstitutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 养殖品种同步定时任务
 */
@Slf4j
@Component("BreedingProductSyncTask")
@ConditionalOnProperty(name = "breeding-product.sync.enabled", havingValue = "true", matchIfMissing = true)
public class BreedingProductSyncTask {

    @Autowired
    private FarmInstitutionService farmInstitutionService;

    @Autowired
    private BreedingProductService breedingProductService;

    @Value("${breeding-product.sync.api-interval:1000}")
    private long apiInterval;

    /**
     * 定时同步养殖品种数据
     * 每天凌晨2点执行（避免与存栏数据同步任务冲突）
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void syncBreedingProductData() {
        log.info("开始执行养殖品种数据定时同步任务");
        long startTime = System.currentTimeMillis();
        
        try {
            // 获取所有养殖机构
            List<FarmInstitution> farmInstitutions = farmInstitutionService.getAllList();
            if (farmInstitutions == null || farmInstitutions.isEmpty()) {
                log.warn("未找到任何养殖机构，同步任务结束");
                return;
            }
            
            log.info("获取到{}个养殖场机构，开始同步养殖品种数据", farmInstitutions.size());
            
            int successCount = 0;
            int failCount = 0;
            int totalSyncedProducts = 0;
            
            for (FarmInstitution institution : farmInstitutions) {
                try {
                    log.info("开始同步养殖场{}({})的养殖品种数据", institution.getFarmName(), institution.getFarmCode());
                    
                    // 调用同步方法
                    Integer syncedCount = breedingProductService.syncBreedingProductsByFarmCode(
                        institution.getFarmCode(), 
                        institution.getFarmName()
                    );
                    
                    if (syncedCount != null && syncedCount >= 0) {
                        successCount++;
                        totalSyncedProducts += syncedCount;
                        log.info("养殖场{}({})养殖品种数据同步成功，同步了{}个品种", 
                            institution.getFarmName(), institution.getFarmCode(), syncedCount);
                    } else {
                        failCount++;
                        log.warn("养殖场{}({})养殖品种数据同步失败", 
                            institution.getFarmName(), institution.getFarmCode());
                    }
                } catch (Exception e) {
                    failCount++;
                    log.error("养殖场{}({})养殖品种数据同步异常", 
                        institution.getFarmName(), institution.getFarmCode(), e);
                }
                
                // 每同步一个养殖场后，短暂休息避免API频率限制
                try {
                    Thread.sleep(apiInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("定时任务被中断");
                    break;
                }
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            log.info("养殖品种数据定时同步任务执行完成，耗时{}ms，成功：{}个养殖场，失败：{}个养殖场，总计同步：{}个品种", 
                duration, successCount, failCount, totalSyncedProducts);
        } catch (Exception e) {
            log.error("养殖品种数据定时同步任务执行异常", e);
        }
    }

    /**
     * 每4小时执行一次同步（可根据需要调整）
     * 如果需要更频繁的同步，可以启用此方法
     */
    // @Scheduled(cron = "0 0 */4 * * ?")
    public void syncBreedingProductDataFrequently() {
        log.info("开始执行养殖品种数据频繁同步任务");
        try {
            // 获取所有养殖机构
            List<FarmInstitution> farmInstitutions = farmInstitutionService.getAllList();
            if (farmInstitutions == null || farmInstitutions.isEmpty()) {
                log.warn("未找到任何养殖机构，频繁同步任务结束");
                return;
            }
            
            log.info("获取到{}个养殖场机构，开始频繁同步养殖品种数据", farmInstitutions.size());
            
            for (FarmInstitution institution : farmInstitutions) {
                try {
                    breedingProductService.syncBreedingProductsByFarmCode(
                        institution.getFarmCode(), 
                        institution.getFarmName()
                    );
                    
                    // 频繁同步时休息时间稍短
                    Thread.sleep(apiInterval / 2);
                } catch (Exception e) {
                    log.error("养殖场{}({})养殖品种数据频繁同步异常", 
                        institution.getFarmName(), institution.getFarmCode(), e);
                }
            }
            
            log.info("养殖品种数据频繁同步任务执行完成");
        } catch (Exception e) {
            log.error("养殖品种数据频繁同步任务执行异常", e);
        }
    }

    /**
     * 手动触发同步指定养殖场的品种数据
     * @param farmCode 养殖场编码
     */
    public void manualSyncByFarmCode(String farmCode) {
        log.info("手动触发同步养殖场{}的品种数据", farmCode);
        try {
            FarmInstitution institution = farmInstitutionService.getFarmInstitutionByFarmCode(farmCode);
            if (institution == null) {
                log.warn("未找到养殖场编码为{}的机构信息", farmCode);
                return;
            }
            
            Integer syncedCount = breedingProductService.syncBreedingProductsByFarmCode(
                institution.getFarmCode(), 
                institution.getFarmName()
            );
            
            log.info("手动同步养殖场{}({})完成，同步了{}个品种", 
                institution.getFarmName(), institution.getFarmCode(), syncedCount);
        } catch (Exception e) {
            log.error("手动同步养殖场{}的品种数据异常", farmCode, e);
        }
    }

    /**
     * 手动触发同步所有养殖场的品种数据
     */
    public void manualSyncAll() {
        log.info("手动触发同步所有养殖场的品种数据");
        syncBreedingProductData();
    }
} 