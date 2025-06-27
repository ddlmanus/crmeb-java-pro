package com.zbkj.service.dao.finance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.finance.ActiveTransaction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 活体交易记录Mapper接口
 */
@Mapper
public interface ActiveTransactionDao extends BaseMapper<ActiveTransaction> {
} 