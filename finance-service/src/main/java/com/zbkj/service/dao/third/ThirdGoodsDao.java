package com.zbkj.service.dao.third;

import com.zbkj.common.thirdVO.ThirdDbGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 三牧优选商品数据访问接口
 */
@Mapper
public interface ThirdGoodsDao {

    /**
     * 分页查询商品列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @param lastUpdateTime 最后更新时间
     * @return 商品列表
     */
    @Select("SELECT * FROM li_goods WHERE delete_flag = 0 " +
            "AND (#{lastUpdateTime} IS NULL OR update_time > #{lastUpdateTime}) " +
            "ORDER BY update_time ASC LIMIT #{offset}, #{limit}")
    List<ThirdDbGoods> selectGoodsList(@Param("offset") int offset, 
                                       @Param("limit") int limit, 
                                       @Param("lastUpdateTime") Date lastUpdateTime);

    /**
     * 根据ID查询商品
     * @param id 商品ID
     * @return 商品信息
     */
    @Select("SELECT * FROM li_goods WHERE id = #{id} AND delete_flag = 0")
    ThirdDbGoods selectGoodsById(@Param("id") Long id);

    /**
     * 查询商品总数
     * @param lastUpdateTime 最后更新时间
     * @return 商品总数
     */
    @Select("SELECT COUNT(*) FROM li_goods WHERE delete_flag = 0 " +
            "AND (#{lastUpdateTime} IS NULL OR update_time > #{lastUpdateTime})")
    int countGoods(@Param("lastUpdateTime") Date lastUpdateTime);

    /**
     * 查询最新更新时间
     * @return 最新更新时间
     */
    @Select("SELECT MAX(update_time) FROM li_goods WHERE delete_flag = 0")
    Date selectMaxUpdateTime();
} 