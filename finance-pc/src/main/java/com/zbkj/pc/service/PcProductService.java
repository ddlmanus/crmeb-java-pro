package com.zbkj.pc.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.CouponProductSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductFrontSearchRequest;
import com.zbkj.common.request.SystemCouponProductSearchRequest;
import com.zbkj.common.request.merchant.MerchantProductSearchRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.page.CommonPage;

import java.util.List;
import java.util.Map;

/**
 * ProductService 接口
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface PcProductService {

    /**
     * 商品列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页参数
     * @return List
     */
    PageInfo<ProductFrontResponse> getList(ProductFrontSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 获取商品详情V1.7
     *
     * @param  id 商品id
     * @param type 基础类型：0=普通商品,1-积分商品,2-虚拟商品,4=视频号,5-云盘商品,6-卡密商品
     * @param marketingType 营销类型：0=基础商品,1=秒杀,2=拼团
     * @param groupActivityId 拼团活动id
     * @param groupRecordId 拼团记录id
     * @return 商品详情信息
     */
    ProductDetailResponse getDetail_V1_7(Integer id, Integer type, Integer marketingType, Integer groupActivityId, Integer groupRecordId);

    /**
     * 商品评论列表
     * @param proId 商品编号
     * @param type 评价等级|0=全部,1=好评,2=中评,3=差评
     * @param pageParamRequest 分页参数
     * @return List<ProductReplyResponse>
     */
    PageInfo<ProductReplyResponse> getReplyList(Integer proId, Integer type, PageParamRequest pageParamRequest);

    /**
     * 商品评论数量
     * @param id 商品id
     * @return StoreProductReplayCountResponse
     */
    ProductReplayCountResponse getReplyCount(Integer id);

    /**
     * 商品详情评论
     * @param id 商品id
     * @return ProductDetailReplyResponse
     */
    ProductDetailReplyResponse getProductReply(Integer id);

    /**
     * 商户商品列表
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return List
     */
    PageInfo<ProductCommonResponse> getMerchantProList(MerchantProductSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 优惠券商品列表
     * @param request 搜索参数
     * @return PageInfo
     */
    PageInfo<ProductFrontResponse> getCouponProList(CouponProductSearchRequest request);

    /**
     * 获取已购商品列表
     * @param pageParamRequest 分页参数
     */
    PageInfo<ProductSimpleResponse> findPurchasedList(PageParamRequest pageParamRequest);

    /**
     * 足迹商品列表
     * @param pageParamRequest 分页参数
     */
    PageInfo<ProductSimpleResponse> findBrowseList(PageParamRequest pageParamRequest);

    /**
     * 系统优惠券商品列表
     * @param request 搜索参数
     */
    PageInfo<ProductFrontResponse> findCouponProductList(SystemCouponProductSearchRequest request);

    /**
     * 推荐商品分页列表
     *
     * @param pageRequest 分页参数
     */
    PageInfo<RecommendProductResponse> findRecommendPage(PageParamRequest pageRequest);

    /**
     * 商品列表搜索前置接口
     */
    ProductSearchBeforeResponse getListBefore(String keyword);

    /**
     * PC店铺推荐商品
     *
     * @param merId 商户ID
     */
    List<PcMerchantRecommendProductResponse> getRecommendProductByMerId(Integer merId);

    /**
     * 根据商品id集合 加载对应商品
     * @param proIdList id集合
     * @return id集合对应的商品列表
     */
    List<ProductFrontResponse> getProductByIds(List<Integer> proIdList);

    /**
     * 会员商品分页列表
     */
    PageInfo<RecommendProductResponse> findMemberPage(PageParamRequest pageParamRequest);

    /**
     * 获取商品列表
     * @param params 查询参数
     * @return 商品列表
     */
    CommonPage<Map<String, Object>> getProductList(Map<String, Object> params);

    /**
     * 搜索商品
     * @param keyword 关键词
     * @param page 页码
     * @param limit 每页数量
     * @return 商品列表
     */
    CommonPage<Map<String, Object>> searchProducts(String keyword, Integer page, Integer limit);

    /**
     * 获取商品详情
     * @param id 商品ID
     * @return 商品详情
     */
    Map<String, Object> getProductDetail(Integer id);

    /**
     * 获取商品规格信息
     * @param id 商品ID
     * @return 规格信息
     */
    Map<String, Object> getProductSku(Integer id);

    /**
     * 获取商品评价列表
     * @param id 商品ID
     * @param page 页码
     * @param limit 每页数量
     * @return 评价列表
     */
    CommonPage<Map<String, Object>> getProductReviews(Integer id, Integer page, Integer limit);

    /**
     * 获取相关商品推荐
     * @param id 商品ID
     * @return 相关商品列表
     */
    List<Map<String, Object>> getRelatedProducts(Integer id);

    /**
     * 获取商品分类
     * @return 分类列表
     */
    List<Map<String, Object>> getCategories();

    /**
     * 获取品牌列表
     * @param categoryId 分类ID
     * @return 品牌列表
     */
    List<Map<String, Object>> getBrands(Integer categoryId);

    /**
     * 商品收藏/取消收藏
     * @param id 商品ID
     * @param type 类型：1收藏，0取消收藏
     */
    void toggleCollect(Integer id, Integer type);

    /**
     * 获取商品库存
     * @param id 商品ID
     * @param sku SKU
     * @return 库存信息
     */
    Map<String, Object> getProductStock(Integer id, String sku);

    /**
     * 获取商品分享信息
     * @param id 商品ID
     * @return 分享信息
     */
    Map<String, Object> getShareInfo(Integer id);
}
