package com.zbkj.service.service.impl.finance;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.finance.LivestockSyncConfig;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.vo.finance.LivestockInventoryRequestVO;
import com.zbkj.common.vo.finance.LivestockInventoryResponseVO;
import com.zbkj.service.dao.finance.LivestockSyncConfigDao;
import com.zbkj.service.service.finance.FarmBreedTypeService;
import com.zbkj.service.service.finance.LivestockSyncService;
import com.zbkj.service.service.finance.MumaIntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 存栏数据同步服务实现
 */
@Slf4j
@Service
public class LivestockSyncServiceImpl extends ServiceImpl<LivestockSyncConfigDao, LivestockSyncConfig> implements LivestockSyncService {

    @Autowired
    private MumaIntegrationService mumaIntegrationService;

    @Autowired
    private FarmBreedTypeService farmBreedTypeService;

    @Override
    public IPage<LivestockSyncConfig> pageList(PageParamRequest pageParamRequest, String farmCode) {
        LambdaQueryWrapper<LivestockSyncConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LivestockSyncConfig::getDeleteFlag, 0);
        
        if (StrUtil.isNotBlank(farmCode)) {
            wrapper.like(LivestockSyncConfig::getFarmCode, farmCode);
        }
        
        wrapper.orderByDesc(LivestockSyncConfig::getCreateTime);
        
        Page<LivestockSyncConfig> page = new Page<>(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addConfig(LivestockSyncConfig config) {
        // 检查养殖场编码是否已存在
        LambdaQueryWrapper<LivestockSyncConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LivestockSyncConfig::getFarmCode, config.getFarmCode());
        wrapper.eq(LivestockSyncConfig::getDeleteFlag, 0);
        
        if (this.count(wrapper) > 0) {
            throw new CrmebException("该养殖场已存在同步配置");
        }
        
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        config.setDeleteFlag(0);
        config.setSyncStatus(0); // 初始状态
        
        return this.save(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateConfig(LivestockSyncConfig config) {
        LivestockSyncConfig existConfig = this.getById(config.getId());
        if (existConfig == null || existConfig.getDeleteFlag() == 1) {
            throw new CrmebException("同步配置不存在");
        }
        
        // 检查养殖场编码是否被其他记录使用
        if (!existConfig.getFarmCode().equals(config.getFarmCode())) {
            LambdaQueryWrapper<LivestockSyncConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LivestockSyncConfig::getFarmCode, config.getFarmCode());
            wrapper.eq(LivestockSyncConfig::getDeleteFlag, 0);
            wrapper.ne(LivestockSyncConfig::getId, config.getId());
            
            if (this.count(wrapper) > 0) {
                throw new CrmebException("该养殖场已存在同步配置");
            }
        }
        
        config.setUpdateTime(new Date());
        return this.updateById(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteConfig(Integer id) {
        LivestockSyncConfig config = this.getById(id);
        if (config == null || config.getDeleteFlag() == 1) {
            throw new CrmebException("同步配置不存在");
        }
        
        config.setDeleteFlag(1);
        config.setUpdateTime(new Date());
        
        return this.updateById(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean toggleSync(Integer id, Integer enableSync) {
        LivestockSyncConfig config = this.getById(id);
        if (config == null || config.getDeleteFlag() == 1) {
            throw new CrmebException("同步配置不存在");
        }
        
        config.setEnableSync(enableSync);
        config.setUpdateTime(new Date());
        
        return this.updateById(config);
    }

    @Override
    public List<LivestockSyncConfig> getEnabledConfigs() {
        LambdaQueryWrapper<LivestockSyncConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LivestockSyncConfig::getEnableSync, 1);
        wrapper.eq(LivestockSyncConfig::getDeleteFlag, 0);
        wrapper.orderByDesc(LivestockSyncConfig::getCreateTime);
        
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean syncFarmData(String farmCode) {
        try {
            log.info("开始同步养殖场{}的存栏数据", farmCode);
            
            // 调用牧码通接口获取数据
            LivestockInventoryRequestVO request = new LivestockInventoryRequestVO();
            request.setFarmCode(farmCode);
            
            LivestockInventoryResponseVO.LivestockInventoryData data = mumaIntegrationService.getLivestockInventory(request);
            
            if (data != null && data.getRowData() != null && !data.getRowData().isEmpty()) {
                // 同步数据到数据库
                Boolean result = farmBreedTypeService.syncLivestockInventoryData(farmCode, data.getRowData());
                
                if (result) {
                    log.info("养殖场{}存栏数据同步成功，共{}条数据", farmCode, data.getRowData().size());
                    return true;
                } else {
                    log.error("养殖场{}存栏数据同步失败", farmCode);
                    return false;
                }
            } else {
                log.warn("养殖场{}未获取到存栏数据", farmCode);
                return false;
            }
        } catch (Exception e) {
            log.error("同步养殖场{}存栏数据异常", farmCode, e);
            return false;
        }
    }

    @Override
    public void syncAllEnabledFarms() {
        List<LivestockSyncConfig> configs = getEnabledConfigs();
        log.info("开始执行定时同步任务，共{}个养殖场", configs.size());
        
        for (LivestockSyncConfig config : configs) {
            try {
                // 更新同步状态为进行中
                updateSyncStatus(config.getId(), 2, "正在同步中...", null);
                
                // 执行同步
                Boolean result = syncFarmData(config.getFarmCode());
                
                if (result) {
                    // 获取同步的数据条数
                    List<com.zbkj.common.model.finance.FarmBreedType> breedTypes = 
                        farmBreedTypeService.getByFarmCode(config.getFarmCode());
                    
                    updateSyncStatus(config.getId(), 1, "同步成功", breedTypes.size());
                } else {
                    updateSyncStatus(config.getId(), 0, "同步失败", null);
                }
            } catch (Exception e) {
                log.error("同步养殖场{}数据异常", config.getFarmCode(), e);
                updateSyncStatus(config.getId(), 0, "同步异常：" + e.getMessage(), null);
            }
        }
        
        log.info("定时同步任务执行完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean manualSync(Integer id) {
        LivestockSyncConfig config = this.getById(id);
        if (config == null || config.getDeleteFlag() == 1) {
            throw new CrmebException("同步配置不存在");
        }
        
        try {
            // 更新同步状态为进行中
            updateSyncStatus(id, 2, "手动同步中...", null);
            
            // 执行同步
            Boolean result = syncFarmData(config.getFarmCode());
            
            if (result) {
                // 获取同步的数据条数
                List<com.zbkj.common.model.finance.FarmBreedType> breedTypes = 
                    farmBreedTypeService.getByFarmCode(config.getFarmCode());
                
                updateSyncStatus(id, 1, "手动同步成功", breedTypes.size());
                return true;
            } else {
                updateSyncStatus(id, 0, "手动同步失败", null);
                return false;
            }
        } catch (Exception e) {
            log.error("手动同步养殖场{}数据异常", config.getFarmCode(), e);
            updateSyncStatus(id, 0, "手动同步异常：" + e.getMessage(), null);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSyncStatus(Integer id, Integer status, String message, Integer dataCount) {
        LivestockSyncConfig config = this.getById(id);
        if (config != null) {
            config.setSyncStatus(status);
            config.setSyncMessage(message);
            config.setLastSyncTime(new Date());
            if (dataCount != null) {
                config.setSyncDataCount(dataCount);
            }
            config.setUpdateTime(new Date());
            this.updateById(config);
        }
    }
} 