package com.zbkj.service.dao.third;

import com.zbkj.common.thirdVO.ThirdDbGoodsSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 三牧优选商品SKU数据访问接口
 */
@Mapper
public interface ThirdGoodsSkuDao {

    /**
     * 根据商品ID查询SKU列表
     * @param goodsId 商品ID
     * @return SKU列表
     */
    @Select("SELECT * FROM li_goods_sku WHERE goods_id = #{goodsId} AND delete_flag = 0 ORDER BY create_time ASC")
    List<ThirdDbGoodsSku> selectSkuListByGoodsId(@Param("goodsId") String goodsId);

    /**
     * 分页查询SKU列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @param lastUpdateTime 最后更新时间
     * @return SKU列表
     */
    @Select("SELECT * FROM li_goods_sku WHERE delete_flag = 0 " +
            "AND (#{lastUpdateTime} IS NULL OR update_time > #{lastUpdateTime}) " +
            "ORDER BY update_time ASC LIMIT #{offset}, #{limit}")
    List<ThirdDbGoodsSku> selectSkuList(@Param("offset") int offset, 
                                        @Param("limit") int limit, 
                                        @Param("lastUpdateTime") Date lastUpdateTime);

    /**
     * 根据ID查询SKU
     * @param id SKU ID
     * @return SKU信息
     */
    @Select("SELECT * FROM li_goods_sku WHERE id = #{id} AND delete_flag = 0")
    ThirdDbGoodsSku selectSkuById(@Param("id") Long id);

    /**
     * 查询SKU总数
     * @param lastUpdateTime 最后更新时间
     * @return SKU总数
     */
    @Select("SELECT COUNT(*) FROM li_goods_sku WHERE delete_flag = 0 " +
            "AND (#{lastUpdateTime} IS NULL OR update_time > #{lastUpdateTime})")
    int countSku(@Param("lastUpdateTime") Date lastUpdateTime);

    /**
     * 查询最新更新时间
     * @return 最新更新时间
     */
    @Select("SELECT MAX(update_time) FROM li_goods_sku WHERE delete_flag = 0")
    Date selectMaxUpdateTime();

    /**
     * 根据商品ID列表查询SKU列表
     * @param goodsIds 商品ID列表
     * @return SKU列表
     */
    @Select("<script>" +
            "SELECT * FROM li_goods_sku WHERE delete_flag = 0 " +
            "AND goods_id IN " +
            "<foreach collection='goodsIds' item='goodsId' open='(' separator=',' close=')'>" +
            "#{goodsId}" +
            "</foreach>" +
            " ORDER BY goods_id, create_time ASC" +
            "</script>")
    List<ThirdDbGoodsSku> selectSkuListByGoodsIds(@Param("goodsIds") List<String> goodsIds);
} 