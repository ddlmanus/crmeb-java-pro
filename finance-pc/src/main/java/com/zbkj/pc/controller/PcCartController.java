package com.zbkj.pc.controller;

import com.zbkj.common.request.CartDeleteRequest;
import com.zbkj.common.request.CartNumRequest;
import com.zbkj.common.request.CartRequest;
import com.zbkj.common.request.CartUpdateNumRequest;
import com.zbkj.common.response.CartMerchantResponse;
import com.zbkj.common.response.CartPriceResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.CartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * PC端购物车控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/pc/cart")
@Api(tags = "PC端购物车接口")
public class PcCartController {

    @Autowired
    private CartService cartService;

    @ApiOperation("获取购物车列表")
    @GetMapping("/list")
    public CommonResult<List<CartMerchantResponse>> getCartList(@RequestParam(defaultValue = "true") Boolean isValid) {
        return CommonResult.success(cartService.getList(isValid));
    }

    @ApiOperation("添加商品到购物车")
    @PostMapping("/add")
    public CommonResult<String> addToCart(@RequestBody @Validated CartRequest request) {
        if (cartService.add(request)) {
            return CommonResult.success("添加成功");
        }
        return CommonResult.failed("添加失败");
    }

    @ApiOperation("更新购物车商品数量")
    @PostMapping("/update/{cartId}")
    public CommonResult<String> updateQuantity(@PathVariable Integer cartId, @RequestParam Integer quantity) {
        if (cartService.updateCartNum(cartId, quantity)) {
            return CommonResult.success("更新成功");
        }
        return CommonResult.failed("更新失败");
    }

    @ApiOperation("删除购物车商品")
    @PostMapping("/delete")
    public CommonResult<String> removeFromCart(@RequestBody @Validated CartDeleteRequest request) {
        if (cartService.deleteCartByIds(request.getIds())) {
            return CommonResult.success("删除成功");
        }
        return CommonResult.failed("删除失败");
    }

    @ApiOperation("获取购物车商品数量")
    @GetMapping("/count")
    public CommonResult<Map<String, Integer>> getCartCount(@Validated CartNumRequest request) {
        return CommonResult.success(cartService.getUserCount(request));
    }

    @ApiOperation("计算购物车价格")
    @PostMapping("/calculate")
    public CommonResult<CartPriceResponse> calculatePrice(@RequestBody @Validated CartDeleteRequest request) {
        return CommonResult.success(cartService.calculatePrice(request.getIds()));
    }
} 