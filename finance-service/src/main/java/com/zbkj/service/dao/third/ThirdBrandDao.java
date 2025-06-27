package com.zbkj.service.dao.third;

import com.zbkj.common.thirdVO.ThirdDbGoodsBrand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 三牧优选品牌数据访问接口
 */
@Mapper
public interface ThirdBrandDao {

    /**
     * 查询所有品牌
     * @param lastUpdateTime 最后更新时间
     * @return 品牌列表
     */
    @Select("SELECT * FROM li_brand WHERE delete_flag = 0 " +
            "AND (#{lastUpdateTime} IS NULL OR update_time > #{lastUpdateTime}) " +
            "ORDER BY update_time ASC")
    List<ThirdDbGoodsBrand> selectBrandList(@Param("lastUpdateTime") Date lastUpdateTime);

    /**
     * 根据ID查询品牌
     * @param id 品牌ID
     * @return 品牌信息
     */
    @Select("SELECT * FROM li_brand WHERE id = #{id} AND delete_flag = 0")
    ThirdDbGoodsBrand selectBrandById(@Param("id") Long id);

    /**
     * 查询品牌总数
     * @param lastUpdateTime 最后更新时间
     * @return 品牌总数
     */
    @Select("SELECT COUNT(*) FROM li_brand WHERE delete_flag = 0 " +
            "AND (#{lastUpdateTime} IS NULL OR update_time > #{lastUpdateTime})")
    int countBrand(@Param("lastUpdateTime") Date lastUpdateTime);

    /**
     * 查询最新更新时间
     * @return 最新更新时间
     */
    @Select("SELECT MAX(update_time) FROM li_brand WHERE delete_flag = 0")
    Date selectMaxUpdateTime();
} 