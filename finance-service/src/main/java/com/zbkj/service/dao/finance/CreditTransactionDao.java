package com.zbkj.service.dao.finance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.finance.CreditTransaction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 授信交易记录Mapper接口
 */
@Mapper
public interface CreditTransactionDao extends BaseMapper<CreditTransaction> {
}
