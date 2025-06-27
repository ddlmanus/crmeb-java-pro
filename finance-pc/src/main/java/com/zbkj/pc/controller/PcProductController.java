package com.zbkj.pc.controller;

import com.zbkj.common.model.product.Product;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.request.merchant.MerchantProductSearchRequest;
import com.zbkj.common.request.CouponProductSearchRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.vo.ProCategoryCacheVo;
import com.zbkj.pc.service.PcProductService;
import com.zbkj.service.service.CategoryService;
import com.zbkj.service.service.ProductCategoryService;
import com.zbkj.service.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PC端商品控制器
 * @author: crmeb
 * @date: 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/pc/product")
@Api(tags = "PC端商品接口")
public class PcProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private PcProductService pcProductService;

    @ApiOperation("获取商品列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<ProductFrontResponse>> getList(ProductFrontSearchRequest request, PageParamRequest pageParamRequest) {
        try {
            log.info("获取商品列表请求: {}", request);
            List<ProductFrontResponse> productList = productService.findH5List(request, pageParamRequest).getList();
            log.info("获取到商品数量: {}", productList.size());
            return CommonResult.success(CommonPage.restPage(productList));
        } catch (Exception e) {
            log.error("获取商品列表失败", e);
            return CommonResult.success(CommonPage.restPage(new ArrayList<>()));
        }
    }

    @ApiOperation("获取商品详情")
    @GetMapping("/detail/{id}")
    public CommonResult<ProductDetailResponse> getProductDetail(@PathVariable Integer id) {
        try {
            log.info("获取商品详情，商品ID: {}", id);
            ProductDetailResponse productDetail = pcProductService.getDetail_V1_7(id, 0, 0, 0, 0);
            return CommonResult.success(productDetail);
        } catch (Exception e) {
            log.error("获取商品详情失败，商品ID: {}", id, e);
            return CommonResult.failed("获取商品详情失败");
        }
    }

    @ApiOperation("搜索商品")
    @GetMapping("/search")
    public CommonResult<CommonPage<ProductFrontResponse>> search(ProductFrontSearchRequest request, PageParamRequest pageParamRequest) {
        try {
            log.info("搜索商品请求: {}", request);
            List<ProductFrontResponse> productList = productService.findH5List(request, pageParamRequest).getList();
            return CommonResult.success(CommonPage.restPage(productList));
        } catch (Exception e) {
            log.error("搜索商品失败", e);
            return CommonResult.success(CommonPage.restPage(new ArrayList<>()));
        }
    }

    @ApiOperation("根据分类获取商品")
    @GetMapping("/category/{categoryId}")
    public CommonResult<CommonPage<ProductFrontResponse>> getByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit) {
        try {
            log.info("根据分类获取商品，分类ID: {}, 页码: {}, 每页数量: {}", categoryId, page, limit);
            ProductFrontSearchRequest request = new ProductFrontSearchRequest();
            request.setCid(categoryId.toString()); // 转换为字符串
            PageParamRequest pageParamRequest = new PageParamRequest();
            pageParamRequest.setPage(page);
            pageParamRequest.setLimit(limit);
            List<ProductFrontResponse> productList = productService.findH5List(request, pageParamRequest).getList();
            return CommonResult.success(CommonPage.restPage(productList));
        } catch (Exception e) {
            log.error("根据分类获取商品失败，分类ID: {}", categoryId, e);
            return CommonResult.success(CommonPage.restPage(new ArrayList<>()));
        }
    }

    @ApiOperation("获取推荐商品")
    @GetMapping("/recommend")
    public CommonResult<List<ProductFrontResponse>> getRecommend(@RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取推荐商品，数量: {}", limit);
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setLimit(limit);
        List<RecommendProductResponse> recommendList = pcProductService.findRecommendPage(pageParamRequest).getList();
        // 转换为ProductFrontResponse
        List<ProductFrontResponse> productList = new ArrayList<>();
        // TODO: 这里需要根据实际的转换逻辑来实现
        return CommonResult.success(productList);

    }

    @ApiOperation("获取热销商品")
    @GetMapping("/hot")
    public CommonResult<List<ProductFrontResponse>> getHot(@RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取热销商品，数量: {}", limit);
        ProductFrontSearchRequest request = new ProductFrontSearchRequest();
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setLimit(limit);
        // 通过销量排序获取热销商品
        request.setSalesOrder("desc");
        List<ProductFrontResponse> productList = productService.findH5List(request, pageParamRequest).getList();
        return CommonResult.success(productList);
    }

    @ApiOperation("获取新品商品")
    @GetMapping("/new")
    public CommonResult<List<ProductFrontResponse>> getNew(@RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取新品商品，数量: {}", limit);
        ProductFrontSearchRequest request = new ProductFrontSearchRequest();
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setLimit(limit);
        // 这里可以通过其他方式获取新品，比如按创建时间排序等
        List<ProductFrontResponse> productList = productService.findH5List(request, pageParamRequest).getList();
        return CommonResult.success(productList);
    }

    @ApiOperation(value = "商品分页列表前置信息")
    @RequestMapping(value = "/list/before", method = RequestMethod.GET)
    public CommonResult<ProductSearchBeforeResponse> getListBefore(@Validated ProductFrontSearchRequest request) {
        return CommonResult.success(pcProductService.getListBefore(request.getKeyword()));
    }

    @ApiOperation(value = "根据商品id集合查询对应商品")
    @RequestMapping(value = "/byids/{ids}", method = RequestMethod.GET)
    public CommonResult<List<ProductFrontResponse>> getProductByIds(@PathVariable String ids) {
        return CommonResult.success(pcProductService.getProductByIds(CrmebUtil.stringToArray(ids)));
    }

    @ApiOperation("获取商品评论列表")
    @GetMapping("/reply/{proId}")
    public CommonResult<CommonPage<ProductReplyResponse>> getReplyList(
            @PathVariable Integer proId,
            @RequestParam(defaultValue = "0") Integer type,
            PageParamRequest pageParamRequest) {
        try {
            log.info("获取商品评论列表，商品ID: {}, 类型: {}", proId, type);
            List<ProductReplyResponse> replyList = pcProductService.getReplyList(proId, type, pageParamRequest).getList();
            return CommonResult.success(CommonPage.restPage(replyList));
        } catch (Exception e) {
            log.error("获取商品评论列表失败", e);
            return CommonResult.success(CommonPage.restPage(new ArrayList<>()));
        }
    }

    @ApiOperation("获取商品评论数量")
    @GetMapping("/reply/count/{id}")
    public CommonResult<ProductReplayCountResponse> getReplyCount(@PathVariable Integer id) {
        try {
            log.info("获取商品评论数量，商品ID: {}", id);
            ProductReplayCountResponse count = pcProductService.getReplyCount(id);
            return CommonResult.success(count);
        } catch (Exception e) {
            log.error("获取商品评论数量失败", e);
            return CommonResult.failed("获取商品评论数量失败");
        }
    }

    @ApiOperation("获取商品详情评论")
    @GetMapping("/reply/detail/{id}")
    public CommonResult<ProductDetailReplyResponse> getProductReply(@PathVariable Integer id) {
        try {
            log.info("获取商品详情评论，商品ID: {}", id);
            ProductDetailReplyResponse reply = pcProductService.getProductReply(id);
            return CommonResult.success(reply);
        } catch (Exception e) {
            log.error("获取商品详情评论失败", e);
            return CommonResult.failed("获取商品详情评论失败");
        }
    }

    @ApiOperation(value = "商户商品列表")
    @RequestMapping(value = "/merchant/pro/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductCommonResponse>> getMerchantProList(@ModelAttribute @Validated MerchantProductSearchRequest request,
                                                                              @ModelAttribute @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(pcProductService.getMerchantProList(request, pageParamRequest)));
    }

    @ApiOperation(value = "我的优惠券商品列表")
    @RequestMapping(value = "/coupon/pro/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductFrontResponse>> getCouponProList(@ModelAttribute @Validated CouponProductSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(pcProductService.getCouponProList(request)));
    }

    @ApiOperation(value = "已购商品列表")
    @RequestMapping(value = "/purchased/list", method = RequestMethod.GET)
    public CommonPage<ProductSimpleResponse> getPurchasedList(@ModelAttribute @Validated PageParamRequest pageParamRequest) {
        return CommonPage.restPage(pcProductService.findPurchasedList(pageParamRequest));
    }

    @ApiOperation(value = "足迹商品列表")
    @RequestMapping(value = "/browse/list", method = RequestMethod.GET)
    public CommonPage<ProductSimpleResponse> getBrowseList(@ModelAttribute @Validated PageParamRequest pageParamRequest) {
        return CommonPage.restPage(pcProductService.findBrowseList(pageParamRequest));
    }

    @ApiOperation(value = "系统优惠券商品列表")
    @RequestMapping(value = "/system/coupon/pro/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductFrontResponse>> findCouponProductList(@Validated SystemCouponProductSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(pcProductService.findCouponProductList(request)));
    }

    @ApiOperation(value = "推荐商品分页列表")
    @RequestMapping(value = "/recommend/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<RecommendProductResponse>> findRecommendPage(@ModelAttribute @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(pcProductService.findRecommendPage(pageParamRequest)));
    }

    @ApiOperation(value = "会员商品分页列表")
    @RequestMapping(value = "/member/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<RecommendProductResponse>> findMemberPage(@ModelAttribute @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(pcProductService.findMemberPage(pageParamRequest)));
    }
} 