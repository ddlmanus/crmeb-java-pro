package com.zbkj.pc.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.GroupConfigConstants;
import com.zbkj.common.constants.ProductConstants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.product.*;
import com.zbkj.common.model.record.BrowseRecord;
import com.zbkj.common.model.system.GroupConfig;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CouponProductSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductFrontSearchRequest;
import com.zbkj.common.request.SystemCouponProductSearchRequest;
import com.zbkj.common.request.merchant.MerchantProductSearchRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.pc.service.PcProductService;
import com.zbkj.pc.service.PcSeckillService;
import com.zbkj.service.service.*;
import com.zbkj.service.service.groupbuy.GroupBuyActivitySkuService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PC端商品服务实现类
 * @author: crmeb
 * @date: 2024-01-01
 */
@Slf4j
@Service
public class PcProductServiceImpl implements PcProductService {

    private static final Logger logger = LoggerFactory.getLogger(PcProductService.class);

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductReplyService productReplyService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductRelationService productRelationService;
    @Autowired
    private ProductAttrService productAttrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private PayComponentProductService componentProductService;
    @Autowired
    private UserMerchantCollectService userMerchantCollectService;
    @Autowired
    private ProductGuaranteeService productGuaranteeService;
    @Autowired
    private PcSeckillService seckillService;
    @Autowired
    private ActivityStyleService activityStyleService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private BrowseRecordService browseRecordService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private ProductBrandService productBrandService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private GroupConfigService groupConfigService;
    @Autowired
    private ProductTagService productTagService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private GroupBuyActivitySkuService groupBuyActivitySkuService;

    @Override
    public CommonPage<Map<String, Object>> getProductList(Map<String, Object> params) {
        String keyword = (String) params.get("keyword");
        Integer categoryId = (Integer) params.get("categoryId");
        Integer brandId = (Integer) params.get("brandId");
        String priceRange = (String) params.get("priceRange");
        String sortType = (String) params.get("sortType");
        Integer page = (Integer) params.get("page");
        Integer limit = (Integer) params.get("limit");

        // 模拟商品数据
        List<Map<String, Object>> allProducts = generateMockProducts();
        
        // 应用过滤条件
        List<Map<String, Object>> filteredProducts = allProducts.stream()
            .filter(product -> {
                if (keyword != null && !keyword.isEmpty()) {
                    String name = (String) product.get("name");
                    return name.toLowerCase().contains(keyword.toLowerCase());
                }
                return true;
            })
            .filter(product -> {
                if (categoryId != null && categoryId > 0) {
                    return Objects.equals(product.get("categoryId"), categoryId);
                }
                return true;
            })
            .filter(product -> {
                if (brandId != null && brandId > 0) {
                    return Objects.equals(product.get("brandId"), brandId);
                }
                return true;
            })
            .filter(product -> {
                if (priceRange != null && !priceRange.isEmpty()) {
                    // 处理价格范围过滤
                    return true; // 简化处理
                }
                return true;
            })
            .collect(Collectors.toList());

        // 应用排序
        switch (sortType) {
            case "price_asc":
                filteredProducts.sort((a, b) -> ((BigDecimal) a.get("price")).compareTo((BigDecimal) b.get("price")));
                break;
            case "price_desc":
                filteredProducts.sort((a, b) -> ((BigDecimal) b.get("price")).compareTo((BigDecimal) a.get("price")));
                break;
            case "sales":
                filteredProducts.sort((a, b) -> ((Integer) b.get("sales")).compareTo((Integer) a.get("sales")));
                break;
            case "new":
                // 按创建时间排序
                break;
            default:
                // 默认排序
                break;
        }

        // 分页处理
        int total = filteredProducts.size();
        int start = (page - 1) * limit;
        int end = Math.min(start + limit, total);
        List<Map<String, Object>> pageData = filteredProducts.subList(start, end);

        CommonPage<Map<String, Object>> result = new CommonPage<>();
        result.setList(pageData);
        result.setTotal((long) total);
        result.setPage(page);
        result.setLimit(limit);
        result.setTotalPage((total + limit - 1) / limit);

        return result;
    }

    @Override
    public CommonPage<Map<String, Object>> searchProducts(String keyword, Integer page, Integer limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("page", page);
        params.put("limit", limit);
        return getProductList(params);
    }

    @Override
    public Map<String, Object> getProductDetail(Integer id) {
        Map<String, Object> product = new HashMap<>();
        product.put("id", id);
        product.put("name", "商品名称 " + id);
        product.put("subTitle", "商品副标题 " + id);
        product.put("price", new BigDecimal("199.00"));
        product.put("originalPrice", new BigDecimal("299.00"));
        product.put("stock", 100);
        product.put("sales", 1250);
        product.put("rating", 4.8);
        product.put("reviewCount", 328);
        product.put("isCollect", false);
        
        // 商品图片
        List<String> images = Arrays.asList(
            "https://picsum.photos/400/400?random=" + id,
            "https://picsum.photos/400/400?random=" + (id + 1),
            "https://picsum.photos/400/400?random=" + (id + 2),
            "https://picsum.photos/400/400?random=" + (id + 3)
        );
        product.put("images", images);
        product.put("image", images.get(0));
        
        // 商品详情
        product.put("description", "这是一个优质的商品，具有优秀的品质和合理的价格。");
        product.put("content", "<div><h3>商品详情</h3><p>详细的商品描述内容...</p></div>");
        
        // 规格信息
        product.put("specs", getProductSku(id));
        
        // 服务信息
        List<String> services = Arrays.asList(
            "7天无理由退货", "15天免费换货", "全国包邮", "正品保证"
        );
        product.put("services", services);
        
        // 品牌信息
        Map<String, Object> brand = new HashMap<>();
        brand.put("id", 1);
        brand.put("name", "优质品牌");
        brand.put("logo", "https://picsum.photos/100/50?random=brand");
        product.put("brand", brand);
        
        return product;
    }

    @Override
    public Map<String, Object> getProductSku(Integer id) {
        Map<String, Object> skuData = new HashMap<>();
        
        // 规格名称
        List<Map<String, Object>> specNames = new ArrayList<>();
        Map<String, Object> colorSpec = new HashMap<>();
        colorSpec.put("name", "颜色");
        colorSpec.put("values", Arrays.asList("红色", "蓝色", "黑色"));
        specNames.add(colorSpec);
        
        Map<String, Object> sizeSpec = new HashMap<>();
        sizeSpec.put("name", "尺寸");
        sizeSpec.put("values", Arrays.asList("S", "M", "L", "XL"));
        specNames.add(sizeSpec);
        
        skuData.put("specNames", specNames);
        
        // SKU列表
        List<Map<String, Object>> skuList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Map<String, Object> sku = new HashMap<>();
            sku.put("id", "sku_" + id + "_" + i);
            sku.put("price", new BigDecimal("199.00").add(new BigDecimal(i * 10)));
            sku.put("stock", 50 - i * 2);
            sku.put("image", "https://picsum.photos/100/100?random=" + (id + i + 100));
            sku.put("specs", Arrays.asList(
                (i % 3 == 0) ? "红色" : (i % 3 == 1) ? "蓝色" : "黑色",
                (i % 4 == 0) ? "S" : (i % 4 == 1) ? "M" : (i % 4 == 2) ? "L" : "XL"
            ));
            skuList.add(sku);
        }
        skuData.put("skuList", skuList);
        
        return skuData;
    }

    @Override
    public CommonPage<Map<String, Object>> getProductReviews(Integer id, Integer page, Integer limit) {
        List<Map<String, Object>> reviews = new ArrayList<>();
        
        for (int i = 1; i <= 20; i++) {
            Map<String, Object> review = new HashMap<>();
            review.put("id", i);
            review.put("userName", "用户" + i);
            review.put("userAvatar", "https://picsum.photos/50/50?random=" + (i + 200));
            review.put("rating", 4 + (i % 2));
            review.put("content", "这个商品很不错，质量很好，值得推荐！");
            review.put("images", Arrays.asList(
                "https://picsum.photos/100/100?random=" + (i + 300),
                "https://picsum.photos/100/100?random=" + (i + 400)
            ));
            review.put("createTime", LocalDateTime.now().minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            review.put("specs", "红色 M");
            reviews.add(review);
        }
        
        // 分页处理
        int total = reviews.size();
        int start = (page - 1) * limit;
        int end = Math.min(start + limit, total);
        List<Map<String, Object>> pageData = reviews.subList(start, end);

        CommonPage<Map<String, Object>> result = new CommonPage<>();
        result.setList(pageData);
        result.setTotal((long) total);
        result.setPage(page);
        result.setLimit(limit);
        result.setTotalPage((total + limit - 1) / limit);

        return result;
    }

    @Override
    public List<Map<String, Object>> getRelatedProducts(Integer id) {
        List<Map<String, Object>> relatedProducts = new ArrayList<>();
        
        for (int i = 1; i <= 8; i++) {
            Map<String, Object> product = new HashMap<>();
            product.put("id", id + i + 1000);
            product.put("name", "相关商品 " + i);
            product.put("image", "https://picsum.photos/200/200?random=" + (id + i + 500));
            product.put("price", new BigDecimal("150.00").add(new BigDecimal(i * 20)));
            product.put("originalPrice", new BigDecimal("250.00").add(new BigDecimal(i * 20)));
            product.put("sales", i * 30);
            product.put("rating", 4.0 + (i % 5) * 0.2);
            relatedProducts.add(product);
        }
        
        return relatedProducts;
    }

    @Override
    public List<Map<String, Object>> getCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();
        
        String[] categoryNames = {
            "手机数码", "电脑办公", "家用电器", "服饰内衣", 
            "家居家装", "美妆护肤", "运动健康", "食品生鲜"
        };
        
        for (int i = 0; i < categoryNames.length; i++) {
            Map<String, Object> category = new HashMap<>();
            category.put("id", i + 1);
            category.put("name", categoryNames[i]);
            category.put("image", "https://picsum.photos/100/100?random=" + (i + 600));
            category.put("productCount", 100 + i * 50);
            categories.add(category);
        }
        
        return categories;
    }

    @Override
    public List<Map<String, Object>> getBrands(Integer categoryId) {
        List<Map<String, Object>> brands = new ArrayList<>();
        
        String[] brandNames = {
            "Apple", "华为", "小米", "OPPO", "vivo", "三星"
        };
        
        for (int i = 0; i < brandNames.length; i++) {
            Map<String, Object> brand = new HashMap<>();
            brand.put("id", i + 1);
            brand.put("name", brandNames[i]);
            brand.put("logo", "https://picsum.photos/80/40?random=" + (i + 700));
            brand.put("productCount", 50 + i * 20);
            brands.add(brand);
        }
        
        return brands;
    }

    @Override
    public void toggleCollect(Integer id, Integer type) {
        // 模拟收藏操作
        log.info("商品收藏操作：商品ID={}, 类型={}", id, type);
    }

    @Override
    public Map<String, Object> getProductStock(Integer id, String sku) {
        Map<String, Object> stockInfo = new HashMap<>();
        stockInfo.put("productId", id);
        stockInfo.put("sku", sku);
        stockInfo.put("stock", 88);
        stockInfo.put("price", new BigDecimal("199.00"));
        stockInfo.put("available", true);
        return stockInfo;
    }

    @Override
    public Map<String, Object> getShareInfo(Integer id) {
        Map<String, Object> shareInfo = new HashMap<>();
        shareInfo.put("title", "商品名称 " + id);
        shareInfo.put("desc", "优质商品，值得拥有");
        shareInfo.put("image", "https://picsum.photos/300/300?random=" + (id + 800));
        shareInfo.put("url", "http://localhost:8080/product/" + id);
        return shareInfo;
    }

    private List<Map<String, Object>> generateMockProducts() {
        List<Map<String, Object>> products = new ArrayList<>();
        
        String[] productNames = {
            "iPhone 15 Pro", "华为 Mate 60 Pro", "小米 14 Ultra", "OPPO Find X7",
            "MacBook Air M3", "联想 ThinkPad X1", "戴尔 XPS 13", "华硕 ROG",
            "AirPods Pro 3", "索尼 WH-1000XM5", "Bose QC45", "森海塞尔 HD800S",
            "Canon EOS R6", "尼康 Z9", "索尼 A7R5", "富士 X-T5"
        };
        
        for (int i = 0; i < 100; i++) {
            Map<String, Object> product = new HashMap<>();
            product.put("id", i + 1);
            product.put("name", productNames[i % productNames.length] + " " + (i / productNames.length + 1));
            product.put("image", "https://picsum.photos/200/200?random=" + (i + 1000));
            product.put("price", new BigDecimal("100.00").add(new BigDecimal(i * 50)));
            product.put("originalPrice", new BigDecimal("200.00").add(new BigDecimal(i * 50)));
            product.put("sales", 100 + i * 10);
            product.put("rating", 4.0 + (i % 10) * 0.1);
            product.put("categoryId", (i % 8) + 1);
            product.put("brandId", (i % 6) + 1);
            product.put("isCollect", false);
            product.put("stock", 50 + i);
            products.add(product);
        }
        
        return products;
    }

    /**
     * 商品列表
     *
     * @return List<ProductFrontResponse>
     */
    @Override
    public PageInfo<ProductFrontResponse> getList(ProductFrontSearchRequest request, PageParamRequest pageRequest) {
        return productService.findH5List(request, pageRequest);
    }

    /**
     * 获取商品详情V1.7
     *
     * @param id            商品id
     * @param type          基础类型：0=普通商品,1-积分商品,2-虚拟商品,4=视频号,5-云盘商品,6-卡密商品
     * @param marketingType 营销类型：0=基础商品,1=秒杀,2=拼团
     * @param groupActivityId 拼团活动id
     * @return 商品详情信息
     */
    @Override
    public ProductDetailResponse getDetail_V1_7(Integer id, Integer type, Integer marketingType, Integer groupActivityId, Integer groupRecordId) {

        if (type < 0 || type > 6) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品类型未知");
        }
        if (marketingType < 0 || marketingType > 2) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "营销类型未知");
        }
        if (type == 4 && marketingType == 0) {
            return componentProductService.getH5Detail(id);
        }
        if (marketingType == 1) {
            return seckillService.getProductDetail(id);
        }
        if (marketingType == 2) {
            return groupBuyActivitySkuService.getGroupBuyDetail(id, groupActivityId, groupRecordId);
        }
        return getBaseDetail(id);
    }

    /**
     * 获取基础商品详情
     * @param id 商品ID
     */
    private ProductDetailResponse getBaseDetail(Integer id) {
        // 查询普通商品
        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        Product product = productService.getH5Detail(id);
        productDetailResponse.setProductInfo(product);
        if (StrUtil.isNotBlank(product.getGuaranteeIds())) {
            productDetailResponse.setGuaranteeList(productGuaranteeService.findByIdList(CrmebUtil.stringToArray(product.getGuaranteeIds())));
        }
        // 获取商品规格
        List<ProductAttr> attrList = productAttrService.getListByProductIdAndType(product.getId(), product.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        // 根据制式设置attr属性
        productDetailResponse.setProductAttr(attrList);
        // 根据制式设置sku属性
        LinkedHashMap<String, ProductAttrValueResponse> skuMap = new LinkedHashMap<>();
        List<ProductAttrValue> productAttrValueList = productAttrValueService.getListByProductIdAndType(product.getId(), product.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        for (ProductAttrValue productAttrValue : productAttrValueList) {
            ProductAttrValueResponse atr = new ProductAttrValueResponse();
            BeanUtils.copyProperties(productAttrValue, atr);
            skuMap.put(atr.getSku(), atr);
        }
        productDetailResponse.setProductValue(skuMap);

        Integer userId = userService.getUserId();

        if (!product.getType().equals(ProductConstants.PRODUCT_TYPE_INTEGRAL)) {
            // 获取商户信息
            Merchant merchant = merchantService.getById(product.getMerId());
            ProductMerchantResponse merchantResponse = new ProductMerchantResponse();
            BeanUtils.copyProperties(merchant, merchantResponse);
            merchantResponse.setCollectNum(userMerchantCollectService.getCountByMerId(merchant.getId()));
            // 获取商户推荐商品
            List<ProMerchantProductResponse> merchantProductResponseList = productService.getRecommendedProductsByMerId(merchant.getId(), 6);
            merchantResponse.setProList(merchantProductResponseList);

            // 获取用户
            productDetailResponse.setUserCollect(false);
            if (userId > 0) {
                merchantResponse.setIsCollect(userMerchantCollectService.isCollect(userId, merchant.getId()));
                // 查询用户是否收藏收藏
                productDetailResponse.setUserCollect(productRelationService.existCollectByUser(userId, product.getId()));
            }
            productDetailResponse.setMerchantInfo(merchantResponse);
            // 添加活动背景
            productDetailResponse.getProductInfo().setActivityStyle(activityStyleService.makeActivityBackgroundStyle(productDetailResponse.getProductInfo()));
        }

        // 异步调用进行数据统计
        asyncService.productDetailStatistics(product.getId(), userId);
        // 获取优惠券（商户）数据2条
        productDetailResponse.setCouponList(couponService.findProductDetailLimit(product.getId(), 4));

        return productDetailResponse;
    }

    /**
     * 商品评论列表
     *
     * @param proId            商品编号
     * @param type             评价等级|0=全部,1=好评,2=中评,3=差评
     * @param pageParamRequest 分页参数
     * @return List<ProductReplyResponse>
     */
    @Override
    public PageInfo<ProductReplyResponse> getReplyList(Integer proId, Integer type, PageParamRequest pageParamRequest) {
        return productReplyService.getH5List(proId, type, pageParamRequest);
    }

    /**
     * 产品评价数量和好评度
     *
     * @return StoreProductReplayCountResponse
     */
    @Override
    public ProductReplayCountResponse getReplyCount(Integer id) {
        return productReplyService.getH5Count(id);
    }

    /**
     * 商品列表转为首页商品格式
     *
     * @param productList 商品列表
     */
    private List<ProductCommonResponse> productToIndexProduct(List<Product> productList) {
        List<ProductCommonResponse> productResponseArrayList = new ArrayList<>();
        for (Product product : productList) {
            ProductCommonResponse productResponse = new ProductCommonResponse();
            BeanUtils.copyProperties(product, productResponse);
            // 评论总数
            Integer sumCount = productReplyService.getCountByScore(product.getId(), ProductConstants.PRODUCT_REPLY_TYPE_ALL);
            // 好评总数
            Integer goodCount = productReplyService.getCountByScore(product.getId(), ProductConstants.PRODUCT_REPLY_TYPE_GOOD);
            String replyChance = "0";
            if (sumCount > 0 && goodCount > 0) {
                replyChance = String.format("%.2f", ((goodCount.doubleValue() / sumCount.doubleValue())));
            }
            productResponse.setReplyNum(sumCount);
            productResponse.setPositiveRatio(replyChance);
            productResponse.setSales(product.getSales() + product.getFicti());
            // 设置商品标签
            ProductTagsFrontResponse productTagsFrontResponse = productTagService.setProductTagByProductTagsRules(product.getId(), product.getBrandId(), product.getMerId(), product.getCategoryId(), productResponse.getProductTags());
            productResponse.setProductTags(productTagsFrontResponse);
            productResponseArrayList.add(productResponse);
        }
        return productResponseArrayList;
    }

    /**
     * 商品详情评论
     *
     * @param id 商品id
     * @return ProductDetailReplyResponse
     * 评论只有一条，图文
     * 评价总数
     * 好评率
     */
    @Override
    public ProductDetailReplyResponse getProductReply(Integer id) {
        return productReplyService.getH5ProductReply(id);
    }

    /**
     * 商户商品列表
     *
     * @param request          搜索参数
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public PageInfo<ProductCommonResponse> getMerchantProList(MerchantProductSearchRequest request, PageParamRequest pageParamRequest) {
        PageInfo<Product> pageInfo = productService.findMerchantProH5List(request, pageParamRequest);
        List<Product> productList = pageInfo.getList();
        if (CollUtil.isEmpty(productList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        productList = activityStyleService.makeActivityBorderStyle(productList);
        List<ProductCommonResponse> responseList = productToIndexProduct(productList);
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 优惠券商品列表
     *
     * @param request 搜索参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductFrontResponse> getCouponProList(CouponProductSearchRequest request) {
        PageInfo<Product> pageInfo = productService.getCouponProList(request);
        List<Product> productList = pageInfo.getList();
        if (CollUtil.isEmpty(productList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        List<Integer> merIdList = productList.stream().map(Product::getMerId).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        List<ProductFrontResponse> frontResponseList = productList.stream().map(product -> {
            ProductFrontResponse response = new ProductFrontResponse();
            BeanUtils.copyProperties(product, response);
            Merchant merchant = merchantMap.get(product.getMerId());
            response.setMerName(merchant.getName());
            response.setMerCategoryId(merchant.getCategoryId());
            response.setMerTypeId(merchant.getTypeId());
            response.setSales(product.getSales() + product.getFicti());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, frontResponseList);
    }

    /**
     * 获取已购商品列表
     *
     * @param pageParamRequest 分页参数
     */
    @Override
    public PageInfo<ProductSimpleResponse> findPurchasedList(PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserIdException();
        PageInfo<OrderDetail> pageInfo = orderDetailService.findPurchasedList(userId, pageParamRequest);
        List<OrderDetail> detailList = pageInfo.getList();
        if (CollUtil.isEmpty(detailList)) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        List<Integer> proIdList = detailList.stream().map(OrderDetail::getProductId).distinct().collect(Collectors.toList());
        Map<Integer, Product> productMap = productService.getMapByIdList(proIdList);
        List<ProductSimpleResponse> responseList = detailList.stream().map(detail -> {
            Product product = productMap.get(detail.getProductId());
            ProductSimpleResponse response = new ProductSimpleResponse();
            response.setProductId(product.getId());
            response.setName(product.getName());
            response.setImage(product.getImage());
            response.setPrice(product.getPrice());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 足迹商品列表
     *
     * @param pageParamRequest 分页参数
     */
    @Override
    public PageInfo<ProductSimpleResponse> findBrowseList(PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserIdException();
        PageInfo<BrowseRecord> pageInfo = browseRecordService.findPageByUserId(userId, pageParamRequest);
        List<BrowseRecord> browseRecordList = pageInfo.getList();
        if (CollUtil.isEmpty(browseRecordList)) {
            return CommonPage.copyPageInfo(pageInfo, new ArrayList<>());
        }
        List<Integer> proIdList = browseRecordList.stream().map(BrowseRecord::getProductId).distinct().collect(Collectors.toList());
        Map<Integer, Product> productMap = productService.getMapByIdList(proIdList);
        List<ProductSimpleResponse> responseList = browseRecordList.stream().map(detail -> {
            Product product = productMap.get(detail.getProductId());
            ProductSimpleResponse response = new ProductSimpleResponse();
            response.setProductId(product.getId());
            response.setName(product.getName());
            response.setImage(product.getImage());
            response.setPrice(product.getPrice());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 系统优惠券商品列表
     *
     * @param request 搜索参数
     */
    @Override
    public PageInfo<ProductFrontResponse> findCouponProductList(SystemCouponProductSearchRequest request) {
        Coupon coupon = couponService.getById(request.getCouponId());
        if (ObjectUtil.isNull(coupon) || coupon.getIsDel()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "优惠券不存在");
        }
        if (!coupon.getStatus()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请重新选择优惠券");
        }
        PageInfo<Product> pageInfo = productService.findCouponProductList(coupon.getId(), coupon.getCategory(), coupon.getLinkedData(), request);
        List<Product> productList = pageInfo.getList();
        if (CollUtil.isEmpty(productList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        List<Integer> merIdList = productList.stream().map(Product::getMerId).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        List<ProductFrontResponse> frontResponseList = productList.stream().map(product -> {
            ProductFrontResponse response = new ProductFrontResponse();
            BeanUtils.copyProperties(product, response);
            Merchant merchant = merchantMap.get(product.getMerId());
            response.setMerName(merchant.getName());
            response.setMerCategoryId(merchant.getCategoryId());
            response.setMerTypeId(merchant.getTypeId());
            response.setReplyNum(productReplyService.getCountByScore(product.getId(), ProductConstants.PRODUCT_REPLY_TYPE_ALL));
            response.setSales(product.getSales() + product.getFicti());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, frontResponseList);
    }

    /**
     * 推荐商品分页列表
     *
     * @param pageRequest 分页参数
     */
    @Override
    public PageInfo<RecommendProductResponse> findRecommendPage(PageParamRequest pageRequest) {
        return productService.findRecommendPage(pageRequest);
    }

    /**
     * 商品列表搜索前置接口
     */
    @Override
    public ProductSearchBeforeResponse getListBefore(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return null;
        }
        List<Integer> productBrandIdList = productService.findProductBrandIdByKeyword(keyword);
        if (CollUtil.isEmpty(productBrandIdList)) {
            return null;
        }
        List<ProductBrand> brandList = productBrandService.findByIdList(productBrandIdList);
        List<Integer> productCategoryIdList = productService.findProductCategoryIdByKeyword(keyword);
        List<ProductCategory> categoryList = productCategoryService.findByIdList(productCategoryIdList);

        ProductSearchBeforeResponse response = new ProductSearchBeforeResponse();
        response.setBrandList(brandList);
        response.setCategoryList(categoryList);
        response.setProductTagList(productTagService.getStatusOnProductTagList());
        return response;
    }

    /**
     * PC店铺推荐商品
     *
     * @param merId 商户ID
     */
    @Override
    public List<PcMerchantRecommendProductResponse> getRecommendProductByMerId(Integer merId) {
        GroupConfig recommendProductConfig = groupConfigService.getOneByTagAndMerId(GroupConfigConstants.TAG_MERCHANT_PC_RECOMMEND_PRODUCT, merId);
        List<PcMerchantRecommendProductResponse> responseList = new ArrayList<>();
        if (ObjectUtil.isNull(recommendProductConfig) || StrUtil.isBlank(recommendProductConfig.getValue())) {
            return responseList;
        }
        List<Integer> proIdList = CrmebUtil.stringToArray(recommendProductConfig.getValue());
        List<Product> productList = productService.findByIds(proIdList, "front");
        if (CollUtil.isEmpty(productList)) {
            return responseList;
        }
        Map<Integer, Product> productMap = productList.stream().collect(Collectors.toMap(Product::getId, Product -> Product));
        for (int i = 0; i < proIdList.size(); i++) {
            Integer pid = proIdList.get(i);
            if (!productMap.containsKey(pid)) {
                continue;
            }
            Product product = productMap.get(pid);
            PcMerchantRecommendProductResponse response = new PcMerchantRecommendProductResponse();
            response.setId(product.getId());
            response.setName(product.getName());
            response.setImage(product.getImage());
            response.setPrice(product.getPrice());
            response.setSales(product.getSales() + product.getFicti());
            response.setIsPaidMember(product.getIsPaidMember());
            response.setVipPrice(product.getVipPrice());
            responseList.add(response);
        }
        return responseList;
    }


    /**
     * 根据商品id集合 加载对应商品
     *
     * @param proIdList id集合
     * @return id集合对应的商品列表
     */
    @Override
    public List<ProductFrontResponse> getProductByIds(List<Integer> proIdList) {
        List<Product> byIdsAndLabel = productService.findByIds(proIdList, "front");
        List<ProductFrontResponse> productFrontResponses = byIdsAndLabel.stream().map(productItem -> {
            ProductFrontResponse response = new ProductFrontResponse();
            BeanUtils.copyProperties(productItem, response);
            return response;
        }).collect(Collectors.toList());
        productFrontResponses.forEach(e -> {
            // 评论总数
            Integer sumCount = productReplyService.getCountByScore(e.getId(), ProductConstants.PRODUCT_REPLY_TYPE_ALL);
            // 好评总数
            Integer goodCount = productReplyService.getCountByScore(e.getId(), ProductConstants.PRODUCT_REPLY_TYPE_GOOD);
            // 设置商品标签
            ProductTagsFrontResponse productTagsFrontResponse = productTagService.setProductTagByProductTagsRules(e.getId(), e.getBrandId(), e.getMerId(), e.getCategoryId(), e.getProductTags());
            e.setProductTags(productTagsFrontResponse);

            String replyChance = "0";
            if (sumCount > 0 && goodCount > 0) {
                replyChance = String.format("%.2f", ((goodCount.doubleValue() / sumCount.doubleValue())));
            }
            e.setReplyNum(sumCount);
            e.setPositiveRatio(replyChance);
            e.setSales(e.getSales() + e.getFicti());
        });

        // 查询活动边框配置信息, 并赋值给商品response 重复添加的商品数据会根据数据添加持续覆盖后的为准
        List<Product> products = new ArrayList<>();
        productFrontResponses.forEach(response -> {
            Product product = new Product();
            BeanUtils.copyProperties(response, product);
            products.add(product);
        });
        List<Product> makeProductList = activityStyleService.makeActivityBorderStyle(products);

        makeProductList.forEach(p -> {
            productFrontResponses.stream().map(resProduct -> {
                if (p.getId().equals(resProduct.getId())) {
                    resProduct.setActivityStyle(p.getActivityStyle());
                }
                return resProduct;
            }).collect(Collectors.toList());
        });
        return productFrontResponses;
    }

    /**
     * 会员商品分页列表
     */
    @Override
    public PageInfo<RecommendProductResponse> findMemberPage(PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserId();
        if (userId <= 0) {
            return new PageInfo<>();
        }
        String proSwitch = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_PAID_MEMBER_PRODUCT_SWITCH);
        if (!proSwitch.equals("1")) {
            return new PageInfo<>();
        }
        User user = userService.getById(userId);
        if (!user.getIsPaidMember()) {
            String paidMemberPriceDisplay = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_PAID_MEMBER_PRICE_DISPLAY);
            if (StrUtil.isBlank(paidMemberPriceDisplay) || !paidMemberPriceDisplay.equals("all")) {
                return new PageInfo<>();
            }
        }
        return productService.findMemberPage(pageParamRequest);
    }
}

