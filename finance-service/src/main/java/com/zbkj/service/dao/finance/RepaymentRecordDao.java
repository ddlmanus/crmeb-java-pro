package com.zbkj.service.dao.finance;

import com.zbkj.common.model.finance.RepaymentRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 还款记录Mapper接口
 */
@Mapper
public interface RepaymentRecordDao extends BaseMapper<RepaymentRecord> {
} 