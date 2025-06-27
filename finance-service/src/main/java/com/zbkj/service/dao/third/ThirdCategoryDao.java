package com.zbkj.service.dao.third;

import com.zbkj.common.thirdVO.ThirdDbCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 三牧优选分类数据访问接口
 */
@Mapper
public interface ThirdCategoryDao {

    /**
     * 查询所有分类
     * @param lastUpdateTime 最后更新时间
     * @return 分类列表
     */
    @Select("SELECT * FROM li_category WHERE delete_flag = 0 " +
            "AND (#{lastUpdateTime} IS NULL OR update_time > #{lastUpdateTime}) " +
            "ORDER BY level ASC, sort_order ASC")
    List<ThirdDbCategory> selectCategoryList(@Param("lastUpdateTime") Date lastUpdateTime);

    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return 分类信息
     */
    @Select("SELECT * FROM li_category WHERE id = #{id} AND delete_flag = 0")
    ThirdDbCategory selectCategoryById(@Param("id") Long id);

    /**
     * 根据父ID查询子分类
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @Select("SELECT * FROM li_category WHERE parent_id = #{parentId} AND delete_flag = 0 ORDER BY sort_order ASC")
    List<ThirdDbCategory> selectCategoryByParentId(@Param("parentId") Long parentId);

    /**
     * 查询分类总数
     * @param lastUpdateTime 最后更新时间
     * @return 分类总数
     */
    @Select("SELECT COUNT(*) FROM li_category WHERE delete_flag = 0 " +
            "AND (#{lastUpdateTime} IS NULL OR update_time > #{lastUpdateTime})")
    int countCategory(@Param("lastUpdateTime") Date lastUpdateTime);

    /**
     * 查询最新更新时间
     * @return 最新更新时间
     */
    @Select("SELECT MAX(update_time) FROM li_category WHERE delete_flag = 0")
    Date selectMaxUpdateTime();
} 