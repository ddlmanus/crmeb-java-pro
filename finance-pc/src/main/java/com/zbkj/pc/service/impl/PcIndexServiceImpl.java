package com.zbkj.pc.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.GroupDataConstants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.constants.VisitRecordConstants;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.seckill.SeckillProduct;
import com.zbkj.common.model.system.SystemConfig;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.pc.service.PcIndexService;
import com.zbkj.pc.service.PcSeckillService;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * IndexServiceImpl 接口实现
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Service
public class PcIndexServiceImpl implements PcIndexService {

    @Autowired
    private SystemGroupDataService systemGroupDataService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private PcSeckillService seckillService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private ProductTagService productTagService;
    @Autowired
    private UserMerchantCollectService userMerchantCollectService;

    /**
     * 首页数据
     * 首页banner
     * 新闻头条-文章标题列表
     * 金刚区
     * 秒杀
     * 店铺街
     * 推荐商品
     */
    @Override
    public IndexInfoResponse getIndexInfo() {
        IndexInfoResponse indexInfoResponse = new IndexInfoResponse();
        indexInfoResponse.setBanner(systemGroupDataService.getListMapByGid(GroupDataConstants.GROUP_DATA_ID_INDEX_BANNER)); //首页banner滚动图
        indexInfoResponse.setMenus(systemGroupDataService.getListMapByGid(GroupDataConstants.GROUP_DATA_ID_INDEX_MENU)); //首页金刚区
        indexInfoResponse.setLogoUrl(systemAttachmentService.getCdnUrl());// 移动端顶部logo 1.3版本 DIY 已经替代
        indexInfoResponse.setWechatBrowserVisit(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_WECHAT_BROWSER_VISIT));// 是否开启微信公众号授权登录
        // 客服部分
        indexInfoResponse.setConsumerType(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_CONSUMER_TYPE));
        switch (indexInfoResponse.getConsumerType()) {
            case SysConfigConstants.CONSUMER_TYPE_H5:
                indexInfoResponse.setConsumerH5Url(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_CONSUMER_H5_URL));
            case SysConfigConstants.CONSUMER_TYPE_HOTLINE:
                indexInfoResponse.setConsumerHotline(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_CONSUMER_HOTLINE));
        }
        // 新闻头条
        indexInfoResponse.setHeadline(articleService.getIndexHeadline());

        // 店铺街开关
        indexInfoResponse.setShopStreetSwitch(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_SHOP_STREET_SWITCH));

        // 保存用户访问记录
        Integer userId = userService.getUserId();
        if (userId > 0) {
            asyncService.saveUserVisit(userId, VisitRecordConstants.VISIT_TYPE_INDEX);
        }
        return indexInfoResponse;
    }

    /**
     * 热门搜索
     * @return List<HashMap<String, String>>
     */
    @Override
    public List<HashMap<String, Object>> hotKeywords() {
        return systemGroupDataService.getListMapByGid(GroupDataConstants.GROUP_DATA_ID_INDEX_KEYWORDS);
    }

    /**
     * 获取首页商品列表
     * @param pageParamRequest 分页参数
     * @param cid 一级商品分类id，全部传0
     * @return List
     */
    @Override
    public PageInfo<ProductCommonResponse> findIndexProductList(Integer cid, PageParamRequest pageParamRequest) {
        PageInfo<Product> pageInfo = productService.getIndexProduct(cid, pageParamRequest);
        List<Product> productList = pageInfo.getList();
        if(CollUtil.isEmpty(productList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        List<Integer> merIdList = productList.stream().map(Product::getMerId).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        List<ProductCommonResponse> productResponseArrayList = new ArrayList<>();
        for (Product product : productList) {
            ProductCommonResponse productResponse = new ProductCommonResponse();
            BeanUtils.copyProperties(product, productResponse);
            productResponse.setIsSelf(merchantMap.get(product.getMerId()).getIsSelf());
            productResponse.setSales(product.getSales() + product.getFicti());
            // 根据条件加载商品标签
            ProductTagsFrontResponse productTagsFrontResponse = productTagService.setProductTagByProductTagsRules(product.getId(), product.getBrandId(), product.getMerId(), product.getCategoryId(), productResponse.getProductTags());
            productResponse.setProductTags(productTagsFrontResponse);
            productResponseArrayList.add(productResponse);
        }
        return CommonPage.copyPageInfo(pageInfo, productResponseArrayList);
    }

    /**
     * 获取颜色配置
     * @return SystemConfig
     */
    @Override
    public SystemConfig getColorConfig() {
        return systemConfigService.getColorConfig();
    }

    /**
     * 获取全局本地图片域名
     * @return String
     */
    @Override
    public String getImageDomain() {
        String localUploadUrl = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_LOCAL_UPLOAD_URL);
        return StrUtil.isBlank(localUploadUrl) ? "" : localUploadUrl;
    }

    /**
     * 首页商户列表
     */
    @Override
    public List<IndexMerchantResponse> findIndexMerchantListByRecomdNum(Integer recomdProdsNum) {
        return merchantService.findIndexList(recomdProdsNum);
    }

    /**
     * 根据商户id集合查询对应商户信息
     * @param ids id集合
     * @return 商户id集合
     */
    @Override
    public List<IndexMerchantResponse> findIndexMerchantListByIds(String ids) {
        List<Merchant> listByIdList = merchantService.getListByIdList(CrmebUtil.stringToArray(ids));
        List<IndexMerchantResponse> responseList = new ArrayList<>();
        for (Merchant merchant : listByIdList) {
            IndexMerchantResponse response = new IndexMerchantResponse();
            BeanUtils.copyProperties(merchant, response);
            // 获取商户推荐商品
            List<ProMerchantProductResponse> merchantProductResponseList = productService.getRecommendedProductsByMerId(merchant.getId(), 3);
            // 店铺关注人数
            Integer followerNum = userMerchantCollectService.getCountByMerId(merchant.getId());
            response.setFollowerNum(followerNum);
            response.setProList(merchantProductResponseList);
            responseList.add(response);
        }
        return responseList;
    }

    /**
     * 获取公司版权图片
     */
    @Override
    public String getCopyrightCompanyImage() {
        return systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_IMAGE);
    }

    /**
     * 获取首页秒杀信息
     */
    @Override
    public List<SeckillProduct> getIndexSeckillInfo() {
        return seckillService.getIndexInfo();
    }

    /**
     * 获取首页优惠券信息
     *
     * @param limit 优惠券数量
     */
    @Override
    public List<Coupon> getIndexCouponInfo(Integer limit) {
        return couponService.getCouponListForDiyPageHome(limit);
    }

    /**
     * 获取底部导航信息
     */
    @Override
    public PageLayoutBottomNavigationResponse getBottomNavigationInfo() {
        String isCustom = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_BOTTOM_NAVIGATION_IS_CUSTOM);
        List<HashMap<String, Object>> bnList = systemGroupDataService.getListMapByGid(GroupDataConstants.GROUP_DATA_ID_BOTTOM_NAVIGATION);
        PageLayoutBottomNavigationResponse response = new PageLayoutBottomNavigationResponse();
        response.setIsCustom(isCustom);
        response.setBottomNavigationList(bnList);
        return response;
    }

    /**
     * 获取版本信息
     * @return AppVersionResponse
     */
    @Override
    public AppVersionResponse getVersion() {
        AppVersionResponse response = new AppVersionResponse();
        response.setAppVersion(systemConfigService.getValueByKey(Constants.CONFIG_APP_VERSION));
        response.setAndroidAddress(systemConfigService.getValueByKey(Constants.CONFIG_APP_ANDROID_ADDRESS));
        response.setIosAddress(systemConfigService.getValueByKey(Constants.CONFIG_APP_IOS_ADDRESS));
        response.setOpenUpgrade(systemConfigService.getValueByKey(Constants.CONFIG_APP_OPEN_UPGRADE));
        return response;
    }

    /**
     * 获取公司版权图片
     */
    @Override
    public CopyrightConfigInfoResponse getCopyrightInfo() {
        String copyrightCompanyImage = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_IMAGE);
        String copyrightCompanyName = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_COPYRIGHT_COMPANY_INFO);
        CopyrightConfigInfoResponse response = new CopyrightConfigInfoResponse();
        response.setCompanyName(copyrightCompanyName);
        response.setCompanyImage(copyrightCompanyImage);
        return response;
    }

    /**
     * 获取移动端域名
     */
    @Override
    public String getFrontDomain() {
        return systemConfigService.getFrontDomain();
    }

    /**
     * 获取平台客服
     */
    @Override
    public CustomerServiceResponse getPlatCustomerService() {
        CustomerServiceResponse response = new CustomerServiceResponse();
        response.setConsumerType(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_CONSUMER_TYPE));
        switch (response.getConsumerType()) {
            case SysConfigConstants.CONSUMER_TYPE_H5:
                response.setConsumerH5Url(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_CONSUMER_H5_URL));
            case SysConfigConstants.CONSUMER_TYPE_HOTLINE:
                response.setConsumerHotline(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_CONSUMER_HOTLINE));
        }
        return response;
    }

    /**
     * 全局配置信息
     */
    @Override
    public FrontGlobalConfigResponse getGlobalConfigInfo() {
        FrontGlobalConfigResponse response = new FrontGlobalConfigResponse();
        Integer userId = userService.getUserId();
        if (userId > 0) {
            User user = userService.getById(userId);
            response.setUserIsPaidMember(user.getIsPaidMember());
        }

        String paidMemberPriceDisplay = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_PAID_MEMBER_PRICE_DISPLAY);
        response.setPaidMemberPriceDisplay(paidMemberPriceDisplay);

        response.setChangeColorConfig(systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_CHANGE_COLOR_CONFIG));

        response.setImageDomain(getImageDomain());

        response.setCopyrightCompanyImage(getCopyrightCompanyImage());

        response.setFrontDomain(getFrontDomain());

        response.setSiteName(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_SITE_NAME));

        response.setMerchantApplySwitch(systemConfigService.getValueByKey(SysConfigConstants.MERCHANT_APPLY_SWITCH));

        return response;
    }

    @Override
    public Map<String, Object> getHomeConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("siteName", "CRMEB商城");
        config.put("siteDesc", "专业的商城系统解决方案");
        config.put("copyright", "© 2024 CRMEB. All rights reserved.");
        config.put("icp", "京ICP备12345678号-1");
        config.put("hotline", "400-123-4567");
        config.put("workTime", "9:00-18:00");
        return config;
    }

    @Override
    public List<Map<String, Object>> getHomeBanner() {
        List<Map<String, Object>> banners = new ArrayList<>();
        
        // 模拟轮播图数据
        Map<String, Object> banner1 = new HashMap<>();
        banner1.put("id", 1);
        banner1.put("title", "春季新品上市");
        banner1.put("desc", "全场8折优惠，限时抢购");
        banner1.put("image", "https://picsum.photos/800/400?random=1");
        banner1.put("link", "/product/list?category=new");
        banner1.put("sort", 1);
        banners.add(banner1);

        Map<String, Object> banner2 = new HashMap<>();
        banner2.put("id", 2);
        banner2.put("title", "品质生活");
        banner2.put("desc", "精选好物，品质保证");
        banner2.put("image", "https://picsum.photos/800/400?random=2");
        banner2.put("link", "/product/list?category=quality");
        banner2.put("sort", 2);
        banners.add(banner2);

        Map<String, Object> banner3 = new HashMap<>();
        banner3.put("id", 3);
        banner3.put("title", "数码专区");
        banner3.put("desc", "最新科技产品，享受智能生活");
        banner3.put("image", "https://picsum.photos/800/400?random=3");
        banner3.put("link", "/product/list?category=digital");
        banner3.put("sort", 3);
        banners.add(banner3);

        return banners;
    }

    @Override
    public List<Map<String, Object>> getCategory() {
        List<Map<String, Object>> categories = new ArrayList<>();
        
        String[] categoryNames = {
            "手机数码", "电脑办公", "家用电器", "服饰内衣", 
            "家居家装", "美妆护肤", "运动健康", "食品生鲜",
            "母婴用品", "图书音像", "汽车用品", "礼品鲜花"
        };
        
        String[] categoryIcons = {
            "📱", "💻", "🏠", "👕", 
            "🛋️", "💄", "🏃", "🍎",
            "👶", "📚", "🚗", "🎁"
        };

        for (int i = 0; i < categoryNames.length; i++) {
            Map<String, Object> category = new HashMap<>();
            category.put("id", i + 1);
            category.put("name", categoryNames[i]);
            category.put("icon", categoryIcons[i]);
            category.put("image", "https://picsum.photos/100/100?random=" + (i + 10));
            category.put("link", "/product/list?cid=" + (i + 1));
            categories.add(category);
        }

        return categories;
    }

    @Override
    public List<Map<String, Object>> getQuickNav() {
        List<Map<String, Object>> quickNavs = new ArrayList<>();
        
        Map<String, Object> nav1 = new HashMap<>();
        nav1.put("id", 1);
        nav1.put("title", "签到有礼");
        nav1.put("icon", "signin");
        nav1.put("color", "#ff4757");
        nav1.put("link", "/signin");
        quickNavs.add(nav1);

        Map<String, Object> nav2 = new HashMap<>();
        nav2.put("id", 2);
        nav2.put("title", "新人专享");
        nav2.put("icon", "newuser");
        nav2.put("color", "#ffa502");
        nav2.put("link", "/newuser");
        quickNavs.add(nav2);

        Map<String, Object> nav3 = new HashMap<>();
        nav3.put("id", 3);
        nav3.put("title", "积分商城");
        nav3.put("icon", "points");
        nav3.put("color", "#2ed573");
        nav3.put("link", "/points");
        quickNavs.add(nav3);

        Map<String, Object> nav4 = new HashMap<>();
        nav4.put("id", 4);
        nav4.put("title", "优惠券");
        nav4.put("icon", "coupon");
        nav4.put("color", "#1e90ff");
        nav4.put("link", "/coupon");
        quickNavs.add(nav4);

        Map<String, Object> nav5 = new HashMap<>();
        nav5.put("id", 5);
        nav5.put("title", "限时拼团");
        nav5.put("icon", "group");
        nav5.put("color", "#ff6b7a");
        nav5.put("link", "/group");
        quickNavs.add(nav5);

        Map<String, Object> nav6 = new HashMap<>();
        nav6.put("id", 6);
        nav6.put("title", "每日签到");
        nav6.put("icon", "checkin");
        nav6.put("color", "#a4b0be");
        nav6.put("link", "/checkin");
        quickNavs.add(nav6);

        return quickNavs;
    }

    @Override
    public Map<String, Object> getSeckillProducts() {
        Map<String, Object> seckillData = new HashMap<>();
        
        // 设置秒杀时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 2);
        seckillData.put("endTime", calendar.getTimeInMillis());
        seckillData.put("title", "限时秒杀");
        seckillData.put("desc", "每日精选，限量抢购");
        
        // 模拟秒杀商品
        List<Map<String, Object>> products = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Map<String, Object> product = new HashMap<>();
            product.put("id", i);
            product.put("name", "秒杀商品" + i);
            product.put("image", "https://picsum.photos/200/200?random=" + (i + 20));
            product.put("price", new BigDecimal("99.00"));
            product.put("originalPrice", new BigDecimal("199.00"));
            product.put("stock", 100 - i * 10);
            product.put("sales", i * 50);
            products.add(product);
        }
        seckillData.put("products", products);
        
        return seckillData;
    }

    @Override
    public Map<String, Object> getRecommendProducts(String type) {
        Map<String, Object> recommendData = new HashMap<>();
        recommendData.put("type", type);
        
        String title;
        switch (type) {
            case "hot":
                title = "热销商品";
                break;
            case "new":
                title = "新品推荐";
                break;
            case "sale":
                title = "特价商品";
                break;
            default:
                title = "为您推荐";
        }
        recommendData.put("title", title);
        
        // 模拟推荐商品
        List<Map<String, Object>> products = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Map<String, Object> product = new HashMap<>();
            product.put("id", i + 100);
            product.put("name", title + " " + i);
            product.put("image", "https://picsum.photos/200/200?random=" + (i + 30));
            product.put("price", new BigDecimal(String.valueOf(99 + i * 10)));
            product.put("originalPrice", new BigDecimal(String.valueOf(199 + i * 10)));
            product.put("sales", i * 20);
            product.put("rating", 4.5 + (i % 5) * 0.1);
            product.put("isCollect", false);
            products.add(product);
        }
        recommendData.put("products", products);
        
        return recommendData;
    }

    @Override
    public List<Map<String, Object>> getBrands() {
        List<Map<String, Object>> brands = new ArrayList<>();
        
        String[] brandNames = {
            "Apple", "华为", "小米", "OPPO", "vivo", "三星",
            "联想", "戴尔", "惠普", "华硕", "索尼", "佳能"
        };
        
        for (int i = 0; i < brandNames.length; i++) {
            Map<String, Object> brand = new HashMap<>();
            brand.put("id", i + 1);
            brand.put("name", brandNames[i]);
            brand.put("logo", "https://picsum.photos/120/60?random=" + (i + 40));
            brand.put("link", "/brand/" + (i + 1));
            brands.add(brand);
        }
        
        return brands;
    }

    @Override
    public List<Map<String, Object>> getFloors() {
        List<Map<String, Object>> floors = new ArrayList<>();
        
        String[] floorNames = {"数码专区", "家电专区", "服装专区", "美妆专区"};
        
        for (int i = 0; i < floorNames.length; i++) {
            Map<String, Object> floor = new HashMap<>();
            floor.put("id", i + 1);
            floor.put("name", floorNames[i]);
            floor.put("icon", "https://picsum.photos/40/40?random=" + (i + 50));
            floor.put("banner", "https://picsum.photos/300/200?random=" + (i + 60));
            floor.put("bannerLink", "/product/list?floor=" + (i + 1));
            floor.put("link", "/product/list?floor=" + (i + 1));
            
            // 楼层商品
            List<Map<String, Object>> products = new ArrayList<>();
            for (int j = 1; j <= 8; j++) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", i * 10 + j + 200);
                product.put("name", floorNames[i] + "商品" + j);
                product.put("image", "https://picsum.photos/200/200?random=" + (i * 10 + j + 70));
                product.put("price", new BigDecimal(String.valueOf(50 + j * 20)));
                product.put("originalPrice", new BigDecimal(String.valueOf(100 + j * 20)));
                products.add(product);
            }
            floor.put("products", products);
            
            floors.add(floor);
        }
        
        return floors;
    }

    @Override
    public List<String> getHotKeywords() {
        return Arrays.asList(
            "手机", "电脑", "耳机", "充电器", "数据线",
            "键盘", "鼠标", "音响", "摄像头", "移动电源"
        );
    }
}

