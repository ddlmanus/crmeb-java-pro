package com.zbkj.pc.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.seckill.SeckillProduct;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.CreateOrderRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.PreOrderDetailRequest;
import com.zbkj.common.request.SeckillProductFrontSearchRequest;
import com.zbkj.common.response.OrderNoResponse;
import com.zbkj.common.response.ProductDetailResponse;
import com.zbkj.common.response.SeckillFrontTimeResponse;
import com.zbkj.common.response.SeckillProductFrontResponse;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.PreMerchantOrderVo;
import com.zbkj.common.vo.PreOrderInfoVo;

import java.util.List;

public interface PcSeckillService {
    /**
     * 获取首页秒杀信息
     * @return
     */
    List<SeckillProduct> getIndexInfo();

    /**
     * 秒杀时段信息
     */
    List<SeckillFrontTimeResponse> activityTimeInfo();

    /**
     * 秒杀商品列表
     * @param request 搜索参数
     * @param pageRequest 分页参数
     * @return
     */
    PageInfo<SeckillProductFrontResponse> getProductList(SeckillProductFrontSearchRequest request, PageParamRequest pageRequest);

    /**
     * 获取秒杀商品详情
     * @param id 秒杀商品ID
     * @return 秒杀商品详情
     */
    ProductDetailResponse getProductDetail(Integer id);

    /**
     * 秒杀预下单校验
     * @param detailRequest 商品参数
     * @return PreMerchantOrderVo
     */
    PreMerchantOrderVo validatePreOrderSeckill(PreOrderDetailRequest detailRequest);

    /**
     * 秒杀创建订单库存校验
     * @param orderInfoVo 预下单信息
     * @return
     */
    MyRecord validateCreateOrderProductStock(PreOrderInfoVo orderInfoVo);

    /**
     * 创建秒杀订单
     * @param orderRequest 下单请求对象
     * @param orderInfoVo 预下单缓存对象
     * @param user 用户信息
     * @return
     */
    OrderNoResponse createOrder(CreateOrderRequest orderRequest, PreOrderInfoVo orderInfoVo, User user);
}
