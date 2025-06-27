package com.zbkj.service.dao.finance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.finance.Organization;
import org.apache.ibatis.annotations.Mapper;

/**
 * 机构管理 Mapper 接口
 */
@Mapper
public interface OrganizationDao extends BaseMapper<Organization> {
    
} 