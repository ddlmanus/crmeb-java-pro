package com.zbkj.pc.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.seckill.SeckillProduct;
import com.zbkj.common.model.system.SystemConfig;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PC端首页服务接口
 * @author: crmeb
 * @date: 2024-01-01
 */
public interface PcIndexService {
    /**
     * 首页信息
     * @return IndexInfoResponse
     */
    IndexInfoResponse getIndexInfo();

    /**
     * 热门搜索
     * @return List
     */
    List<HashMap<String, Object>> hotKeywords();

    /**
     * 获取首页商品列表
     * @param pageParamRequest 分页参数
     * @param cid 一级商品分类id，全部传0
     * @return List
     */
    PageInfo<ProductCommonResponse> findIndexProductList(Integer cid, PageParamRequest pageParamRequest);

    /**
     * 获取颜色配置
     * @return SystemConfig
     */
    SystemConfig getColorConfig();

    /**
     * 获取全局本地图片域名
     * @return String
     */
    String getImageDomain();

    /**
     * 首页商户列表
     * @param recomdProdsNum 推荐商品数量
     */
    List<IndexMerchantResponse> findIndexMerchantListByRecomdNum(Integer recomdProdsNum);

    /**
     * 根据商户id集合查询对应商户信息
     * @param ids id集合
     * @return 商户id集合
     */
    List<IndexMerchantResponse> findIndexMerchantListByIds(String ids);

    /**
     * 获取公司版权图片
     */
    String getCopyrightCompanyImage();

    /**
     * 获取首页秒杀信息
     */
    List<SeckillProduct> getIndexSeckillInfo();

    /**
     * 获取首页优惠券信息
     * @param limit 优惠券数量
     */
    List<Coupon> getIndexCouponInfo(Integer limit);

    /**
     * 获取底部导航信息
     */
    PageLayoutBottomNavigationResponse getBottomNavigationInfo();

    /**
     * 获取版本信息
     * @return AppVersionResponse
     */
    AppVersionResponse getVersion();

    /**
     * 获取公司版权图片
     */
    CopyrightConfigInfoResponse getCopyrightInfo();

    /**
     * 获取移动端域名
     */
    String getFrontDomain();

    /**
     * 获取平台客服
     */
    CustomerServiceResponse getPlatCustomerService();

    /**
     * 全局配置信息
     */
    FrontGlobalConfigResponse getGlobalConfigInfo();

    /**
     * 获取首页配置信息
     * @return 配置信息
     */
    Map<String, Object> getHomeConfig();

    /**
     * 获取首页轮播图
     * @return 轮播图列表
     */
    List<Map<String, Object>> getHomeBanner();

    /**
     * 获取商品分类
     * @return 分类列表
     */
    List<Map<String, Object>> getCategory();

    /**
     * 获取快捷导航
     * @return 快捷导航列表
     */
    List<Map<String, Object>> getQuickNav();

    /**
     * 获取秒杀商品
     * @return 秒杀数据
     */
    Map<String, Object> getSeckillProducts();

    /**
     * 获取推荐商品
     * @param type 推荐类型
     * @return 推荐商品数据
     */
    Map<String, Object> getRecommendProducts(String type);

    /**
     * 获取品牌列表
     * @return 品牌列表
     */
    List<Map<String, Object>> getBrands();

    /**
     * 获取楼层数据
     * @return 楼层数据列表
     */
    List<Map<String, Object>> getFloors();

    /**
     * 获取热门搜索关键词
     * @return 关键词列表
     */
    List<String> getHotKeywords();
}
