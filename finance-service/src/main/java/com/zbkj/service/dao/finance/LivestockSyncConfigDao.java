package com.zbkj.service.dao.finance;

import com.zbkj.common.model.finance.LivestockSyncConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 存栏数据同步配置Mapper接口
 */
@Mapper
public interface LivestockSyncConfigDao extends BaseMapper<LivestockSyncConfig> {
} 