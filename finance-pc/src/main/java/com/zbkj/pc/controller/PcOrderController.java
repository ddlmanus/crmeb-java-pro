package com.zbkj.pc.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.vo.LogisticsResultVo;
import com.zbkj.pc.service.PcOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * PC端订单控制器
 * @author: crmeb
 * @date: 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("api/pc/order")
@Api(tags = "PC端 -- 订单")
public class PcOrderController {

    @Autowired
    private PcOrderService orderService;

    @ApiOperation(value = "预下单")
    @RequestMapping(value = "/pre/order", method = RequestMethod.POST)
    public com.zbkj.common.result.CommonResult<OrderNoResponse> preOrder(@RequestBody @Validated PreOrderRequest request) {
        return com.zbkj.common.result.CommonResult.success(orderService.preOrder_V1_7(request));
    }

    @ApiOperation(value = "加载预下单")
    @RequestMapping(value = "load/pre/{preOrderNo}", method = RequestMethod.GET)
    public com.zbkj.common.result.CommonResult<PreOrderResponse> loadPreOrder(@PathVariable String preOrderNo) {
        return com.zbkj.common.result.CommonResult.success(orderService.loadPreOrder(preOrderNo));
    }

    @ApiOperation(value = "计算订单价格")
    @RequestMapping(value = "/computed/price", method = RequestMethod.POST)
    public com.zbkj.common.result.CommonResult<ComputedOrderPriceResponse> computedPrice(@Validated @RequestBody OrderComputedPriceRequest request) {
        return com.zbkj.common.result.CommonResult.success(orderService.computedOrderPrice(request));
    }

    @ApiOperation(value = "创建订单")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public com.zbkj.common.result.CommonResult<OrderNoResponse> createOrder(@Validated @RequestBody CreateOrderRequest orderRequest) {
        return com.zbkj.common.result.CommonResult.success(orderService.createOrder(orderRequest));
    }

    @ApiOperation(value = "订单列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public com.zbkj.common.result.CommonResult<CommonPage<OrderFrontDataResponse>> orderList(@ModelAttribute @Validated OrderFrontListRequest request) {
        return com.zbkj.common.result.CommonResult.success(CommonPage.restPage(orderService.list_v1_4(request)));
    }

    @ApiOperation(value = "订单详情")
    @RequestMapping(value = "/detail/{orderNo}", method = RequestMethod.GET)
    public com.zbkj.common.result.CommonResult<OrderFrontDetailResponse> orderDetail(@PathVariable String orderNo) {
        return com.zbkj.common.result.CommonResult.success(orderService.frontDetail(orderNo));
    }

    @ApiOperation(value = "订单取消")
    @RequestMapping(value = "/cancel/{orderNo}", method = RequestMethod.POST)
    public com.zbkj.common.result.CommonResult<Boolean> cancel(@PathVariable(value = "orderNo") String orderNo) {
        if (orderService.cancel(orderNo)) {
            return com.zbkj.common.result.CommonResult.success();
        }
        return com.zbkj.common.result.CommonResult.failed();
    }

    @ApiOperation(value = "订单商品评论列表")
    @RequestMapping(value = "/reply/list", method = RequestMethod.GET)
    public com.zbkj.common.result.CommonResult<CommonPage<InfoReplyResponse>> replyList(@ModelAttribute PageParamRequest pageRequest) {
        return com.zbkj.common.result.CommonResult.success(CommonPage.restPage(orderService.replyList(pageRequest)));
    }

    @ApiOperation(value = "评价订单商品")
    @RequestMapping(value = "/reply/product", method = RequestMethod.POST)
    public com.zbkj.common.result.CommonResult<Boolean> replyProduct(@RequestBody @Validated OrderProductReplyRequest request) {
        if (orderService.replyProduct(request)) {
            return com.zbkj.common.result.CommonResult.success();
        }
        return com.zbkj.common.result.CommonResult.failed();
    }

    @ApiOperation(value = "删除订单")
    @RequestMapping(value = "/delete/{orderNo}", method = RequestMethod.POST)
    public com.zbkj.common.result.CommonResult<Boolean> delete(@PathVariable(value = "orderNo") String orderNo) {
        if (orderService.delete(orderNo)) {
            return com.zbkj.common.result.CommonResult.success();
        }
        return com.zbkj.common.result.CommonResult.failed();
    }

    @ApiOperation(value = "订单收货")
    @RequestMapping(value = "/take/delivery/{orderNo}", method = RequestMethod.POST)
    public com.zbkj.common.result.CommonResult<String> take(@PathVariable(value = "orderNo") String orderNo) {
        if (orderService.takeDelivery(orderNo)) {
            return com.zbkj.common.result.CommonResult.success("订单收货成功");
        }
        return com.zbkj.common.result.CommonResult.failed("订单收货失败");
    }

    @ApiOperation(value = "获取订单发货单列表")
    @RequestMapping(value = "/{orderNo}/invoice/list", method = RequestMethod.GET)
    public com.zbkj.common.result.CommonResult<OrderInvoiceFrontResponse> getInvoiceList(@PathVariable(value = "orderNo") String orderNo) {
        return com.zbkj.common.result.CommonResult.success(orderService.getInvoiceList(orderNo));
    }

    @ApiOperation(value = "物流信息查询")
    @RequestMapping(value = "/logistics/{invoiceId}", method = RequestMethod.GET)
    public com.zbkj.common.result.CommonResult<LogisticsResultVo> getLogisticsInfo(@PathVariable(value = "invoiceId") Integer invoiceId) {
        return com.zbkj.common.result.CommonResult.success(orderService.getLogisticsInfo(invoiceId));
    }

    @ApiOperation(value = "获取订单状态图")
    @RequestMapping(value = "/status/image", method = RequestMethod.GET)
    public com.zbkj.common.result.CommonResult<List<HashMap<String, Object>>> getOrderStatusImage() {
        return com.zbkj.common.result.CommonResult.success(orderService.getOrderStatusImage());
    }
} 