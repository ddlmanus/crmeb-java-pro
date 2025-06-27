package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.finance.LivestockSyncConfig;
import com.zbkj.common.request.PageParamRequest;

import java.util.List;

/**
 * 存栏数据同步服务接口
 */
public interface LivestockSyncService extends IService<LivestockSyncConfig> {

    /**
     * 分页查询同步配置
     * @param pageParamRequest 分页参数
     * @param farmCode 养殖场编码
     * @return 分页结果
     */
    IPage<LivestockSyncConfig> pageList(PageParamRequest pageParamRequest, String farmCode);

    /**
     * 添加同步配置
     * @param config 配置信息
     * @return 是否成功
     */
    Boolean addConfig(LivestockSyncConfig config);

    /**
     * 更新同步配置
     * @param config 配置信息
     * @return 是否成功
     */
    Boolean updateConfig(LivestockSyncConfig config);

    /**
     * 删除同步配置
     * @param id 配置ID
     * @return 是否成功
     */
    Boolean deleteConfig(Integer id);

    /**
     * 启用/禁用同步配置
     * @param id 配置ID
     * @param enableSync 是否启用
     * @return 是否成功
     */
    Boolean toggleSync(Integer id, Integer enableSync);

    /**
     * 获取所有启用的同步配置
     * @return 配置列表
     */
    List<LivestockSyncConfig> getEnabledConfigs();

    /**
     * 执行单个养殖场的数据同步
     * @param farmCode 养殖场编码
     * @return 是否成功
     */
    Boolean syncFarmData(String farmCode);

    /**
     * 执行所有启用的养殖场数据同步
     * @return 同步结果
     */
    void syncAllEnabledFarms();

    /**
     * 手动触发指定养殖场的数据同步
     * @param id 配置ID
     * @return 是否成功
     */
    Boolean manualSync(Integer id);

    /**
     * 更新同步状态
     * @param id 配置ID
     * @param status 同步状态
     * @param message 同步消息
     * @param dataCount 数据条数
     */
    void updateSyncStatus(Integer id, Integer status, String message, Integer dataCount);
} 