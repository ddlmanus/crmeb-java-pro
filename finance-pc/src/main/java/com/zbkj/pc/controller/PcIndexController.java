package com.zbkj.pc.controller;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.article.Article;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.seckill.SeckillProduct;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductFrontSearchRequest;
import com.zbkj.common.request.SeckillProductSearchRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.PcHomeBannerVo;
import com.zbkj.common.vo.ProCategoryCacheVo;
import com.zbkj.pc.service.PcIndexService;
import com.zbkj.pc.service.PcShoppingService;
import com.zbkj.service.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PC端首页控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/pc/index")
@Api(tags = "PC端首页相关接口")
public class PcIndexController {

    @Autowired
    private SystemGroupDataService systemGroupDataService;
    
    @Autowired
    private ProductCategoryService productCategoryService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private SeckillProductService seckillProductService;
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @Autowired
    private ArticleService articleService;

    @Autowired
    private PcShoppingService pcShoppingService;
    @Autowired
    private PcIndexService indexService;

    @ApiOperation(value = "获取首页banner")
    @RequestMapping(value = "/get/banner", method = RequestMethod.GET)
    public CommonResult<List<PcHomeBannerVo>> getHomeBanner() {
        return CommonResult.success(pcShoppingService.getHomeBanner());
    }

    @ApiOperation("获取快速导航")
    @GetMapping("/quick-nav")
    public CommonResult<List<HashMap<String, Object>>> getQuickNav() {
        try {
            log.info("获取快速导航");
            // 使用系统组合数据服务获取首页金刚区
            List<HashMap<String, Object>> menuList = systemGroupDataService.getListMapByGid(2); // GROUP_DATA_ID_INDEX_MENU = 2
            return CommonResult.success(menuList);
        } catch (Exception e) {
            log.error("获取快速导航失败", e);
            return CommonResult.failed("获取快速导航失败");
        }
    }

    @ApiOperation(value = "热门搜索")
    @RequestMapping(value = "/search/keyword", method = RequestMethod.GET)
    public CommonResult<List<HashMap<String, Object>>> hotKeywords() {
        return CommonResult.success(indexService.hotKeywords());
    }
    @ApiOperation(value = "获取底部导航信息")
    @RequestMapping(value = "/get/bottom/navigation", method = RequestMethod.GET)
    public CommonResult<PageLayoutBottomNavigationResponse> getBottomNavigation() {
        return CommonResult.success(indexService.getBottomNavigationInfo());
    }

    @ApiOperation("获取商品分类")
    @GetMapping("/categories")
    public CommonResult<List<ProCategoryCacheVo>> getCategories() {
        try {
            log.info("获取商品分类");
            // 获取前台一级分类
            List<ProCategoryCacheVo> categoryList = productCategoryService.getFrontFirstCategory();
            return CommonResult.success(categoryList);
        } catch (Exception e) {
            log.error("获取商品分类失败", e);
            return CommonResult.failed("获取商品分类失败");
        }
    }

    @ApiOperation("获取推荐商品")
    @GetMapping("/recommend-products")
    public CommonResult<CommonPage<RecommendProductResponse>> getRecommendProducts(@RequestParam(defaultValue = "12") Integer limit) {
        try {
            log.info("获取推荐商品，数量: {}", limit);
            PageParamRequest pageParamRequest = new PageParamRequest();
            pageParamRequest.setPage(1);
            pageParamRequest.setLimit(limit);
            
            // 使用ProductService的推荐商品方法
            PageInfo<RecommendProductResponse> pageInfo = productService.findRecommendPage(pageParamRequest);
            return CommonResult.success(CommonPage.restPage(pageInfo.getList()));
        } catch (Exception e) {
            log.error("获取推荐商品失败", e);
            return CommonResult.failed("获取推荐商品失败");
        }
    }

    @ApiOperation("获取热销商品")
    @GetMapping("/hot-products")
    public CommonResult<CommonPage<ProductFrontResponse>> getHotProducts(@RequestParam(defaultValue = "12") Integer limit) {
        try {
            log.info("获取热销商品，数量: {}", limit);
            ProductFrontSearchRequest request = new ProductFrontSearchRequest();
            request.setSalesOrder("desc"); // 按销量降序排序
            PageParamRequest pageParamRequest = new PageParamRequest();
            pageParamRequest.setPage(1);
            pageParamRequest.setLimit(limit);
            
            // 使用ProductService的H5商品列表方法，按销量排序
            PageInfo<ProductFrontResponse> pageInfo = productService.findH5List(request, pageParamRequest);
            return CommonResult.success(CommonPage.restPage(pageInfo.getList()));
        } catch (Exception e) {
            log.error("获取热销商品失败", e);
            return CommonResult.failed("获取热销商品失败");
        }
    }

    @ApiOperation("获取新品商品")
    @GetMapping("/new-products")
    public CommonResult<CommonPage<ProductFrontResponse>> getNewProducts(@RequestParam(defaultValue = "12") Integer limit) {
        try {
            log.info("获取新品商品，数量: {}", limit);
            ProductFrontSearchRequest request = new ProductFrontSearchRequest();
            request.setPriceOrder("desc"); // 按创建时间降序排序
            PageParamRequest pageParamRequest = new PageParamRequest();
            pageParamRequest.setPage(1);
            pageParamRequest.setLimit(limit);
            
            // 使用ProductService的H5商品列表方法，按时间排序
            PageInfo<ProductFrontResponse> pageInfo = productService.findH5List(request, pageParamRequest);
            return CommonResult.success(CommonPage.restPage(pageInfo.getList()));
        } catch (Exception e) {
            log.error("获取新品商品失败", e);
            return CommonResult.failed("获取新品商品失败");
        }
    }

    @ApiOperation(value = "首页秒杀信息")
    @RequestMapping(value = "/seckill/info", method = RequestMethod.GET)
    public CommonResult<List<SeckillProduct>> getIndexSeckillInfo() {
        return CommonResult.success(indexService.getIndexSeckillInfo());
    }

    @ApiOperation("获取首页头条新闻")
    @GetMapping("/headline")
    public CommonResult<List<Article>> getHeadline() {
        try {
            log.info("获取首页头条新闻");
            // 使用文章服务获取首页头条
            List<Article> headlineList = articleService.getIndexHeadline();
            return CommonResult.success(headlineList);
        } catch (Exception e) {
            log.error("获取首页头条新闻失败", e);
            return CommonResult.failed("获取首页头条新闻失败");
        }
    }

    @ApiOperation("获取搜索建议")
    @GetMapping("/search-suggestions")
    public CommonResult<List<String>> getSearchSuggestions(@RequestParam String keyword) {
        try {
            log.info("获取搜索建议，关键词: {}", keyword);
            // 这里可以根据实际需求实现搜索建议逻辑
            // 比如从商品表中查找匹配的商品名称作为建议
            List<String> suggestions = new ArrayList<>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                // 简单实现：返回一些固定的建议（实际项目中应该从数据库查询）
                suggestions.add(keyword + " 推荐");
                suggestions.add(keyword + " 热销");
                suggestions.add(keyword + " 新品");
            }
            return CommonResult.success(suggestions);
        } catch (Exception e) {
            log.error("获取搜索建议失败", e);
            return CommonResult.failed("获取搜索建议失败");
        }
    }

    @ApiOperation("获取首页完整数据")
    @GetMapping("/data")
    public CommonResult<Map<String, Object>> getIndexData() {
        try {
            log.info("获取首页完整数据");
            Map<String, Object> result = new HashMap<>();
            
            // 轮播图
            result.put("banners",pcShoppingService.getHomeBanner());
            
            // 快速导航
            result.put("quickNav", systemGroupDataService.getListMapByGid(2));
            
            // 热门搜索
            result.put("hotKeywords", systemGroupDataService.getListMapByGid(3));
            
            // 商品分类
            result.put("categories", productCategoryService.getFrontFirstCategory());
            
            // 推荐商品 - 获取8个
            PageParamRequest recommendPageRequest = new PageParamRequest();
            recommendPageRequest.setPage(1);
            recommendPageRequest.setLimit(8);
            result.put("recommendProducts", productService.findRecommendPage(recommendPageRequest).getList());
            
            // 热销商品 - 获取8个
            ProductFrontSearchRequest hotRequest = new ProductFrontSearchRequest();
            hotRequest.setSalesOrder("desc");
            PageParamRequest hotPageRequest = new PageParamRequest();
            hotPageRequest.setPage(1);
            hotPageRequest.setLimit(8);
            result.put("hotProducts", productService.findH5List(hotRequest, hotPageRequest).getList());
            
            // 新品商品 - 获取8个
            ProductFrontSearchRequest newRequest = new ProductFrontSearchRequest();
            newRequest.setPriceOrder("desc");
            PageParamRequest newPageRequest = new PageParamRequest();
            newPageRequest.setPage(1);
            newPageRequest.setLimit(8);
            result.put("newProducts", productService.findH5List(newRequest, newPageRequest).getList());
            
            // 秒杀商品 - 获取6个
            SeckillProductSearchRequest seckillPageRequest = new SeckillProductSearchRequest();
            seckillPageRequest.setPage(1);
            seckillPageRequest.setLimit(6);
            result.put("seckillProducts", seckillProductService.getSeckillProductPage(seckillPageRequest).getList());
            
            // 头条新闻
            result.put("headline", articleService.getIndexHeadline());
            
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("获取首页完整数据失败", e);
            return CommonResult.failed("获取首页数据失败");
        }
    }
} 