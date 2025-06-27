package com.zbkj.service.dao.finance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.finance.OrgCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 组织分类DAO
 */
@Mapper
public interface OrgCategoryDao extends BaseMapper<OrgCategory> {
}
