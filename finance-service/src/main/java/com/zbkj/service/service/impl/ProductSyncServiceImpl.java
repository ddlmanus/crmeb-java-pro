package com.zbkj.service.service.impl;

import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.product.ProductAttr;
import com.zbkj.common.model.product.ProductBrand;
import com.zbkj.common.model.product.ProductCategory;
import com.zbkj.common.request.ProductAddRequest;
import com.zbkj.common.request.ProductAttrAddRequest;
import com.zbkj.common.request.ProductAttrValueAddRequest;
import com.zbkj.common.request.ProductSyncRequest;
import com.zbkj.common.request.ProductCategoryRequest;
import com.zbkj.common.request.ProductBrandRequest;
import com.zbkj.common.response.ProductSyncResponse;
import com.zbkj.service.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * 商品同步服务实现类
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
@Slf4j
@Service
public class ProductSyncServiceImpl implements ProductSyncService {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductCategoryService categoryService;
    
    @Autowired
    private ProductBrandService brandService;
    @Autowired
    private ProductAttrService productAttrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private MerchantService merchantService;

    // 三牧优选数据库连接信息
    private static final String SANMU_DB_URL = "jdbc:mysql://xumuyouxuan.com:3306/myx-shop?useUnicode=true&characterEncoding=utf-8&useSSL=false&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
    private static final String SANMU_DB_USERNAME = "root";
    private static final String SANMU_DB_PASSWORD = "yangzhi123456";

    @Override
    public ProductSyncResponse syncProducts(ProductSyncRequest request, SystemAdmin admin) {
        ProductSyncResponse response = new ProductSyncResponse();
        response.setTotalCount(0);
        response.setSuccessCount(0);
        response.setFailedCount(0);
        response.setSuccess(false);

        Connection connection = null;

        try {
            // 建立数据库连接
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(SANMU_DB_URL, SANMU_DB_USERNAME, SANMU_DB_PASSWORD);

            log.info("开始商品同步流程，商户：{}", request.getStoreName());

            // 第一步：同步商品分类
            Map<Long, Integer> categoryIdMapping = syncCategoriesWithMapping(connection);
            log.info("分类同步完成，共同步{}个分类", categoryIdMapping.size());

            // 第二步：同步商品品牌
            Map<String, Integer> brandIdMapping = syncBrandsWithMapping(connection);
            log.info("品牌同步完成，共同步{}个品牌", brandIdMapping.size());
            if(StringUtils.isEmpty(request.getStoreName())){
                //获取所有的店铺
                List<Merchant> list = merchantService.list();
                for (Merchant merchant : list) {
                    // 第三步：同步商品基本信息
                    List<ProductAddRequest> productList = getProductsWithDetails(connection, merchant.getName(), categoryIdMapping, brandIdMapping);
                    response.setTotalCount(productList.size());
                    log.info("查询到{}个商品需要同步", productList.size());

                                    // 第四步：保存或更新商品到当前系统
                for (ProductAddRequest productAddRequest : productList) {
                    try {
                        // 检查商品是否已存在（根据三牧优选商品ID）
                        Product existingProduct = productService.getBySanmuGoodsId(productAddRequest.getSanmuGoodsId());
                        
                        Boolean result;
                        if (existingProduct != null) {
                            // 商品已存在，执行更新
                            result = productService.updateForSync(productAddRequest);
                            if (result) {
                                response.setSuccessCount(response.getSuccessCount() + 1);
                                log.info("商品更新成功：{} (三牧ID: {})", productAddRequest.getName(), productAddRequest.getSanmuGoodsId());
                            } else {
                                response.setFailedCount(response.getFailedCount() + 1);
                                log.warn("商品更新失败：{} (三牧ID: {})", productAddRequest.getName(), productAddRequest.getSanmuGoodsId());
                            }
                        } else {
                            // 商品不存在，执行新增
                            result = productService.save(productAddRequest);
                            if (result) {
                                response.setSuccessCount(response.getSuccessCount() + 1);
                                log.info("商品新增成功：{} (三牧ID: {})", productAddRequest.getName(), productAddRequest.getSanmuGoodsId());
                            } else {
                                response.setFailedCount(response.getFailedCount() + 1);
                                log.warn("商品新增失败：{} (三牧ID: {})", productAddRequest.getName(), productAddRequest.getSanmuGoodsId());
                            }
                        }
                    } catch (Exception e) {
                        log.error("保存/更新商品失败: {} (三牧ID: {})", productAddRequest.getName(), productAddRequest.getSanmuGoodsId(), e);
                        response.setFailedCount(response.getFailedCount() + 1);
                    }
                }

                    response.setSuccess(response.getSuccessCount() > 0);
                    response.setMessage(String.format("同步完成：分类%d个，品牌%d个，商品总计%d个，成功%d个，失败%d个",
                            categoryIdMapping.size(), brandIdMapping.size(), response.getTotalCount(), response.getSuccessCount(), response.getFailedCount()));
                }
            }else{
                // 第三步：同步商品基本信息
                List<ProductAddRequest> productList = getProductsWithDetails(connection, request.getStoreName(), categoryIdMapping, brandIdMapping);
                response.setTotalCount(productList.size());
                log.info("查询到{}个商品需要同步", productList.size());

                // 第四步：保存或更新商品到当前系统
                for (ProductAddRequest productAddRequest : productList) {
                    try {
                        // 检查商品是否已存在（根据三牧优选商品ID）
                        Product existingProduct = productService.getBySanmuGoodsId(productAddRequest.getSanmuGoodsId());
                        
                        Boolean result;
                        if (existingProduct != null) {
                            // 商品已存在，执行更新
                            result = productService.updateForSync(productAddRequest);
                            if (result) {
                                response.setSuccessCount(response.getSuccessCount() + 1);
                                log.info("商品更新成功：{} (三牧ID: {})", productAddRequest.getName(), productAddRequest.getSanmuGoodsId());
                            } else {
                                response.setFailedCount(response.getFailedCount() + 1);
                                log.warn("商品更新失败：{} (三牧ID: {})", productAddRequest.getName(), productAddRequest.getSanmuGoodsId());
                            }
                        } else {
                            // 商品不存在，执行新增
                            result = productService.save(productAddRequest);
                            if (result) {
                                response.setSuccessCount(response.getSuccessCount() + 1);
                                log.info("商品新增成功：{} (三牧ID: {})", productAddRequest.getName(), productAddRequest.getSanmuGoodsId());
                            } else {
                                response.setFailedCount(response.getFailedCount() + 1);
                                log.warn("商品新增失败：{} (三牧ID: {})", productAddRequest.getName(), productAddRequest.getSanmuGoodsId());
                            }
                        }
                    } catch (Exception e) {
                        log.error("保存/更新商品失败: {} (三牧ID: {})", productAddRequest.getName(), productAddRequest.getSanmuGoodsId(), e);
                        response.setFailedCount(response.getFailedCount() + 1);
                    }
                }

                response.setSuccess(response.getSuccessCount() > 0);
                response.setMessage(String.format("同步完成：分类%d个，品牌%d个，商品总计%d个，成功%d个，失败%d个",
                        categoryIdMapping.size(), brandIdMapping.size(), response.getTotalCount(), response.getSuccessCount(), response.getFailedCount()));
            }

        } catch (Exception e) {
            log.error("商品同步失败: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("同步失败：" + e.getMessage());
        } finally {
            // 关闭数据库连接
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                log.error("关闭数据库连接失败: {}", e.getMessage(), e);
            }
        }

        return response;
    }

    @Override
    public ProductSyncResponse syncCategories() {
        ProductSyncResponse response = new ProductSyncResponse();
        response.setTotalCount(0);
        response.setSuccessCount(0);
        response.setFailedCount(0);
        response.setSuccess(false);

        Connection connection = null;

        try {
            // 建立数据库连接
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(SANMU_DB_URL, SANMU_DB_USERNAME, SANMU_DB_PASSWORD);

            // 第一步：同步商品分类
            Map<Long, Integer> categoryIdMapping = syncCategoriesWithMapping(connection);
            log.info("分类同步完成，共同步{}个分类", categoryIdMapping.size());

            // 第二步：同步商品品牌
            Map<String, Integer> brandIdMapping = syncBrandsWithMapping(connection);
            log.info("品牌同步完成，共同步{}个品牌", brandIdMapping.size());

            response.setSuccess(response.getSuccessCount() > 0);
            response.setMessage(String.format("同步完成：分类%d个，品牌%d个，商品总计%d个，成功%d个，失败%d个",
                    categoryIdMapping.size(), brandIdMapping.size(), response.getTotalCount(), response.getSuccessCount(), response.getFailedCount()));

        } catch (Exception e) {
            log.error("商品同步失败: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("同步失败：" + e.getMessage());
        } finally {
            // 关闭数据库连接
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                log.error("关闭数据库连接失败: {}", e.getMessage(), e);
            }
        }

        return response;
    }

    /**
     * 同步商品分类（按层级顺序）
     */
    private Map<Long, Integer> syncCategoriesWithMapping(Connection connection) throws SQLException {
        log.info("开始同步商品分类...");
        Map<Long, Integer> categoryIdMapping = new HashMap<>();
        
        // 按层级顺序同步：三牧优选level=0,1,2 对应 当前系统level=1,2,3
        for (int sanmuLevel = 0; sanmuLevel <= 2; sanmuLevel++) {
            int localLevel = sanmuLevel + 1; // 转换层级：三牧优选level + 1 = 当前系统level
            log.info("开始同步三牧优选{}级分类(对应当前系统{}级)", sanmuLevel, localLevel);
            
            String sql = "SELECT * FROM li_category WHERE level = ? AND delete_flag = 0 ORDER BY parent_id, sort_order";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, sanmuLevel);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        try {
                            Long sanmuCategoryId = rs.getLong("id");
                            String categoryName = rs.getString("name");
                            Long sanmuParentId = rs.getLong("parent_id");
                            
                            // 检查分类是否已存在（根据名称和转换后的层级）
                            if (!categoryExistsByNameAndLevel(categoryName, localLevel)) {
                                // 转换父级ID
                                Integer localParentId = convertParentId(sanmuParentId, localLevel, categoryIdMapping);
                                
                                // 保存分类并获取新的本地ID
                                Integer localCategoryId = saveCategoryAndGetId(rs, localParentId, localLevel);
                                if (localCategoryId != null) {
                                    // 建立ID映射关系
                                    categoryIdMapping.put(sanmuCategoryId, localCategoryId);
                                    log.info("分类同步成功：SanmuLevel={}, LocalLevel={}, SanmuID={}, LocalID={}, Name={}", 
                                            sanmuLevel, localLevel, sanmuCategoryId, localCategoryId, categoryName);
                                }
                            } else {
                                // 如果分类已存在，仍需要建立映射关系
                                Integer existingLocalId = getLocalCategoryIdByNameAndLevel(categoryName, localLevel);
                                if (existingLocalId != null) {
                                    categoryIdMapping.put(sanmuCategoryId, existingLocalId);
                                }
                                log.debug("分类已存在，跳过：SanmuLevel={}, LocalLevel={}, SanmuID={}, Name={}", 
                                        sanmuLevel, localLevel, sanmuCategoryId, categoryName);
                            }
                        } catch (Exception e) {
                            log.error("同步分类失败：SanmuLevel={}, LocalLevel={}, ID={}", sanmuLevel, localLevel, rs.getLong("id"), e);
                        }
                    }
                }
            }
            
            log.info("分类同步完成：SanmuLevel={}, LocalLevel={}", sanmuLevel, localLevel);
        }

        log.info("分类同步完成，共同步{}个分类", categoryIdMapping.size());
        return categoryIdMapping;
    }

    /**
     * 检查分类是否存在（根据名称和层级）
     */
    private boolean categoryExistsByNameAndLevel(String categoryName, int level) {
        try {
            // 根据名称和层级查询分类是否存在
            List<ProductCategory> categories = categoryService.getAdminList();
            for (ProductCategory category : categories) {
                if (category.getName().equals(categoryName) && category.getLevel().equals(level)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.debug("检查分类是否存在时出错：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 根据名称和层级获取本地分类ID
     */
    private Integer getLocalCategoryIdByNameAndLevel(String categoryName, int level) {
        try {
            List<ProductCategory> categories = categoryService.getAdminList();
            for (ProductCategory category : categories) {
                if (category.getName().equals(categoryName) && category.getLevel().equals(level)) {
                    return category.getId();
                }
            }
            return null;
        } catch (Exception e) {
            log.error("根据名称和层级查询本地分类ID失败：Name={}, Level={}", categoryName, level, e);
            return null;
        }
    }

    /**
     * 转换父级ID（三牧优选父级ID → 当前系统父级ID）
     */
    private Integer convertParentId(Long sanmuParentId, int localLevel, Map<Long, Integer> categoryIdMapping) {
        if (localLevel == 1) {
            // 当前系统一级分类的父级ID为0
            return 0;
        } else {
            // 二级和三级分类需要根据映射关系转换父级ID
            if (sanmuParentId != null && categoryIdMapping.containsKey(sanmuParentId)) {
                return categoryIdMapping.get(sanmuParentId);
            } else {
                log.warn("未找到父级分类映射：SanmuParentID={}, LocalLevel={}, 使用默认父级ID=0", sanmuParentId, localLevel);
                return 0;
            }
        }
    }

    /**
     * 保存分类并返回新生成的ID
     */
    private Integer saveCategoryAndGetId(ResultSet rs, Integer localParentId, int localLevel) throws SQLException {
        try {
            // 从三牧优选数据映射到当前系统
            String name = rs.getString("name");
            Integer sortOrder = rs.getInt("sort_order");
            String image = rs.getString("image");
            
            // 创建分类保存请求对象
            ProductCategoryRequest categoryRequest = new ProductCategoryRequest();
            categoryRequest.setName(name != null ? name : "未知分类");
            categoryRequest.setLevel(localLevel);
            categoryRequest.setPid(localParentId);
            categoryRequest.setSort(sortOrder != null ? sortOrder : 999);
            categoryRequest.setIcon(image);
            
            if (categoryService.add(categoryRequest)) {
                // 获取刚刚添加的分类ID
                Integer newCategoryId = getLocalCategoryIdByNameAndLevel(name, localLevel);
                if (newCategoryId != null) {
                    log.info("分类保存成功：Name={}, Level={}, LocalParentID={}, NewID={}", 
                            name, localLevel, localParentId, newCategoryId);
                    return newCategoryId;
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("保存分类失败", e);
            return null;
        }
    }

    /**
     * 同步商品品牌并返回映射关系
     */
    private Map<String, Integer> syncBrandsWithMapping(Connection connection) throws SQLException {
        log.info("开始同步商品品牌...");
        Map<String, Integer> brandIdMapping = new HashMap<>();

        // 查询三牧优选的所有品牌
        String sql = "SELECT * FROM li_brand WHERE delete_flag = 0 ORDER BY id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                try {
                    String brandId = rs.getString("id");
                    String brandName = rs.getString("name");
                    String brandLogo = rs.getString("logo");
                    
                    if (brandId != null && !brandId.isEmpty() && brandName != null && !brandName.isEmpty()) {
                        // 检查品牌是否已存在
                        if (!brandExists(brandName)) {
                            // 保存品牌
                            if (saveBrand(brandId, brandName, brandLogo)) {
                                Integer localBrandId = getLocalBrandIdByName(brandName);
                                if (localBrandId != null) {
                                    brandIdMapping.put(brandId, localBrandId);
                                }
                                log.info("品牌同步成功：ID={}, Name={}", brandId, brandName);
                            }
                        } else {
                            // 如果品牌已存在，仍需要建立映射关系
                            Integer existingLocalId = getLocalBrandIdByName(brandName);
                            if (existingLocalId != null) {
                                brandIdMapping.put(brandId, existingLocalId);
                            }
                            log.debug("品牌已存在，跳过：ID={}, Name={}", brandId, brandName);
                        }
                    }
                } catch (Exception e) {
                    log.error("同步品牌失败：ID={}", rs.getString("id"), e);
                }
            }
        }

        log.info("品牌同步完成，共同步{}个品牌", brandIdMapping.size());
        return brandIdMapping;
    }

    /**
     * 检查品牌是否存在（根据名称）
     */
    private boolean brandExists(String brandName) {
        try {
            // 根据名称查询品牌是否存在
            ProductBrand productBrand = brandService.getByName(brandName);
            if (productBrand != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            log.debug("检查品牌是否存在时出错：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 保存品牌到当前系统
     */
    private boolean saveBrand(String brandId, String brandName, String brandLogo) {
        try {
            // 检查品牌是否已存在（根据名称）
            if (brandExists(brandName)) {
                log.debug("品牌已存在，跳过：Name={}", brandName);
                return true;
            }
            
            // 创建品牌保存请求对象
            ProductBrandRequest brandRequest = new ProductBrandRequest();
            brandRequest.setName(brandName != null ? brandName : ("品牌" + brandId));
            brandRequest.setSort(999);
            brandRequest.setIcon(brandLogo);
            brandRequest.setCategoryIds(""); // 暂不关联分类
            
            // 调用品牌服务保存
            if (brandService.add(brandRequest)) {
                log.info("品牌保存成功：SanmuID={}, Name={}", brandId, brandName);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("保存品牌失败", e);
            return false;
        }
    }

    /**
     * 获取商户的商品及详细信息
     */
    private List<ProductAddRequest> getProductsWithDetails(Connection connection, String storeName, Map<Long, Integer> categoryIdMapping, Map<String, Integer> brandIdMapping) throws SQLException {
        List<ProductAddRequest> productList = new ArrayList<>();
        
        // 查询商品基本信息
        String sql = "SELECT g.*, s.store_name, s.member_name " +
                    "FROM li_goods g " +
                    "LEFT JOIN li_store s ON g.store_id = s.id " +
                    "WHERE s.store_name = ? AND g.delete_flag = 0";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, storeName);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    try {
                        ProductAddRequest productAddRequest = convertToProductAddRequest(connection, resultSet, categoryIdMapping, brandIdMapping);
                        productList.add(productAddRequest);
                    } catch (Exception e) {
                        log.error("转换商品数据失败: {}", e.getMessage(), e);
                    }
                }
            }
        }
        
        return productList;
    }

    /**
     * 将ResultSet转换为ProductAddRequest，包含完整的分类、品牌、SKU信息
     */
    private ProductAddRequest convertToProductAddRequest(Connection connection, ResultSet rs, Map<Long, Integer> categoryIdMapping, Map<String, Integer> brandIdMapping) throws SQLException {
        ProductAddRequest request = new ProductAddRequest();

        // 设置三牧优选商品ID
        String sanmuGoodsId = rs.getString("id");
        request.setSanmuGoodsId(sanmuGoodsId);

        // 基本商品信息
        request.setName(rs.getString("goods_name"));
        request.setIntro(rs.getString("selling_point") != null ? rs.getString("selling_point") : "");
        request.setKeyword(rs.getString("selling_point"));
        
        // 处理商品单位（动态检查和创建）
        String goodsUnit = rs.getString("goods_unit");
        String unitName = ensureProductUnitExists(connection, goodsUnit);
        request.setUnitName(unitName != null ? unitName : "件");
        
        // 处理商品轮播图 - 确保格式为 ["url"] 这样的JSON数组格式
        String small = rs.getString("original");
        List<String> sliderImages = parseSliderImages(small);
        
        // 将轮播图列表转换为JSON数组格式 ["url1", "url2", ...]
        request.setSliderImage(formatSliderImagesToJson(sliderImages));
        request.setImage(rs.getString("small") != null ? rs.getString("small") : "");
        request.setContent(rs.getString("intro"));
        request.setType(0); // 普通商品

        // 映射分类信息（使用预先建立的ID映射关系）
        String categoryPath = rs.getString("category_path");
        mapCategoryInfo(connection, categoryPath, request, categoryIdMapping);

        // 映射品牌信息（使用预先建立的ID映射关系）
        String brandId = rs.getString("brand_id");
        mapBrandInfo(connection, brandId, request, brandIdMapping);

        // 检查是否有多规格
        String goodsId = sanmuGoodsId;
        boolean hasMultipleSpecs = checkMultipleSpecs(connection, goodsId);
        request.setSpecType(hasMultipleSpecs);

        // 同步SKU信息
        syncSkuInfo(connection, goodsId, request);

        // 其他默认设置
        request.setTempId(1); // 默认运费模板ID
        request.setSort(0);
        request.setIsSub(false); // 不单独分佣
        request.setIsPaidMember(false); // 不是付费会员商品
        request.setDeliveryMethod("1"); // 商家配送

        return request;
    }

    /**
     * 确保商品单位存在，如果不存在则创建
     */
    private String ensureProductUnitExists(Connection connection, String goodsUnit) {
        try {
            if (goodsUnit == null || goodsUnit.isEmpty()) {
                return "件"; // 默认单位
            }

            // 根据三牧优选的goods_unit查询对应的单位名称
            String unitName = getUnitNameFromSanmu(connection, goodsUnit);
            if (unitName == null) {
                unitName = goodsUnit; // 如果查不到，直接使用goods_unit作为单位名称
            }

            // 检查当前系统中是否已存在这个单位
            if (!unitExistsInLocalSystem(unitName)) {
                // 如果不存在，创建这个单位属性
                createProductUnit(unitName);
                log.info("创建商品单位：{}", unitName);
            }

            return unitName;
        } catch (Exception e) {
            log.error("处理商品单位失败: goodsUnit={}", goodsUnit, e);
            return "件"; // 出错时返回默认单位
        }
    }

    /**
     * 从三牧优选li_goods_unit表查询单位名称
     */
    private String getUnitNameFromSanmu(Connection connection, String goodsUnit) {
        try {
            // 如果goodsUnit是ID，从li_goods_unit表查询名称
            String sql = "SELECT name FROM li_goods_unit WHERE id = ? AND delete_flag = 0";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, goodsUnit);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("name");
                    }
                }
            }
            
            // 如果不是ID或查不到，再尝试按名称查询
            String sql2 = "SELECT name FROM li_goods_unit WHERE name = ? AND delete_flag = 0";
            try (PreparedStatement stmt2 = connection.prepareStatement(sql2)) {
                stmt2.setString(1, goodsUnit);
                try (ResultSet rs2 = stmt2.executeQuery()) {
                    if (rs2.next()) {
                        return rs2.getString("name");
                    }
                }
            }
        } catch (Exception e) {
            log.error("查询三牧优选单位名称失败: goodsUnit={}", goodsUnit, e);
        }
        return null;
    }

    /**
     * 检查当前系统中是否存在指定的商品单位
     */
    private boolean unitExistsInLocalSystem(String unitName) {
        try {
            ProductAttr attr = productAttrService.getByName(unitName);
            return attr != null;
        } catch (Exception e) {
            log.debug("检查商品单位是否存在时出错：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 在当前系统中创建商品单位属性
     */
    private void createProductUnit(String unitName) {
        try {
            // 这里需要根据实际的ProductAttrService API创建商品属性
            // 暂时使用简单的实现，需要根据实际API调整
            log.info("需要创建商品单位属性：{}", unitName);
            // 示例：productAttrService.createUnit(unitName);
        } catch (Exception e) {
            log.error("创建商品单位失败：{}", unitName, e);
        }
    }

    /**
     * 映射分类信息（使用预先建立的ID映射关系）
     */
    private void mapCategoryInfo(Connection connection, String categoryPath, ProductAddRequest request, Map<Long, Integer> categoryIdMapping) {
        try {
            if (categoryPath != null && !categoryPath.isEmpty()) {
                // 分析分类路径，获取最后一级分类ID
                String[] categoryIds = categoryPath.split(",");
                if (categoryIds.length > 0) {
                    String sanmuCategoryIdStr = categoryIds[categoryIds.length - 1]; // 取最后一级分类
                    Long sanmuCategoryId = Long.parseLong(sanmuCategoryIdStr);
                    
                    // 使用预先建立的映射关系获取本地分类ID
                    Integer localCategoryId = categoryIdMapping.get(sanmuCategoryId);
                    if (localCategoryId != null) {
                        request.setCateId(localCategoryId.toString());
                        request.setCategoryId(localCategoryId);
                        log.debug("映射分类信息：SanmuID={}, LocalID={}", sanmuCategoryId, localCategoryId);
                        return;
                    }
                }
            }
            
            // 如果没有找到对应分类，使用默认值
            request.setCateId("1");
            request.setCategoryId(1);
            log.warn("未找到对应分类，使用默认分类ID=1");
        } catch (Exception e) {
            log.error("映射分类信息失败: {}", e.getMessage(), e);
            request.setCateId("1");
            request.setCategoryId(1);
        }
    }

    /**
     * 映射品牌信息（使用预先建立的ID映射关系）
     */
    private void mapBrandInfo(Connection connection, String sanmuBrandId, ProductAddRequest request, Map<String, Integer> brandIdMapping) {
        try {
            if (sanmuBrandId != null && !sanmuBrandId.isEmpty()) {
                // 使用预先建立的映射关系获取本地品牌ID
                Integer localBrandId = brandIdMapping.get(sanmuBrandId);
                if (localBrandId != null) {
                    request.setBrandId(localBrandId);
                    log.debug("映射品牌信息：SanmuID={}, LocalID={}", sanmuBrandId, localBrandId);
                    return;
                }
            }
            
            // 如果没有找到对应品牌，使用默认值
            request.setBrandId(1);
            log.warn("未找到对应品牌，使用默认品牌ID=1");
        } catch (Exception e) {
            log.error("映射品牌信息失败: {}", e.getMessage(), e);
            request.setBrandId(1);
        }
    }

    /**
     * 检查是否有多规格
     */
    private boolean checkMultipleSpecs(Connection connection, String goodsId) {
        try {
            String sql = "SELECT COUNT(*) as count FROM li_goods_sku WHERE goods_id = ? AND delete_flag = 0";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, goodsId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt("count");
                        boolean isMultiSpec = count > 1;
                        log.debug("商品规格检查：商品ID={}, SKU数量={}, 规格类型={}", goodsId, count, isMultiSpec ? "多规格" : "单规格");
                        return isMultiSpec; // 如果有多个SKU，则认为是多规格
                    }
                }
            }
        } catch (Exception e) {
            log.error("检查多规格失败: 商品ID={}", goodsId, e);
        }
        log.warn("检查多规格失败，默认返回单规格：商品ID={}", goodsId);
        return false; // 默认返回单规格
    }

    /**
     * 同步SKU信息
     */
    private void syncSkuInfo(Connection connection, String goodsId, ProductAddRequest request) {
        try {
            List<ProductAttrAddRequest> attrs = new ArrayList<>();
            List<ProductAttrValueAddRequest> attrValues = new ArrayList<>();

            // 查询SKU信息
            String sql = "SELECT * FROM li_goods_sku WHERE goods_id = ? AND delete_flag = 0 ORDER BY id";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, goodsId);
                try (ResultSet rs = stmt.executeQuery()) {
                    
                    List<ResultSet> skuDataList = new ArrayList<>();
                    // 先收集所有SKU数据
                    while (rs.next()) {
                        // 创建属性值
                        ProductAttrValueAddRequest attrValue = new ProductAttrValueAddRequest();
                        
                        // 价格信息
                        BigDecimal price = rs.getBigDecimal("price");
                        BigDecimal cost = rs.getBigDecimal("cost");
                        Integer quantity = rs.getInt("quantity");
                        BigDecimal weight = rs.getBigDecimal("weight");
                        
                        attrValue.setPrice(price != null ? price : BigDecimal.ZERO);
                        attrValue.setOtPrice(price != null ? price : BigDecimal.ZERO);
                        attrValue.setVipPrice(price != null ? price : BigDecimal.ZERO);
                        attrValue.setCost(cost != null ? cost : BigDecimal.ZERO);
                        attrValue.setStock(quantity != null ? quantity : 0);
                        attrValue.setWeight(weight != null ? weight : BigDecimal.ZERO);
                        attrValue.setVolume(BigDecimal.ZERO);
                        attrValue.setBrokerage(0);
                        attrValue.setBrokerageTwo(0);
                        attrValue.setImage(rs.getString("small") != null ? rs.getString("small") : "");
                        
                        // 处理规格信息
                        String specs = rs.getString("specs");
                        String simpleSpecs = rs.getString("simple_specs");
                        
                        if (request.getSpecType()) {
                            // 多规格商品：需要解析JSON格式的规格信息
                            if (specs != null && !specs.isEmpty()) {
                                attrValue.setAttrValue(specs);
                            } else if (simpleSpecs != null && !simpleSpecs.isEmpty()) {
                                // 如果没有specs但有simpleSpecs，尝试转换格式
                                attrValue.setAttrValue(convertSimpleSpecsToJson(simpleSpecs));
                            } else {
                                // 没有规格信息，使用默认
                                attrValue.setAttrValue("{\"规格\":\"默认\"}");
                            }
                        } else {
                            // 单规格商品：固定格式
                            attrValue.setAttrValue("{\"默认\":\"默认\"}");
                        }
                        
                        attrValues.add(attrValue);
                    }
                }
            }
            
            // 根据规格类型创建属性
            if (request.getSpecType()) {
                // 多规格商品：需要收集所有SKU的规格信息来创建完整的属性结构
                Set<String> allAttrValues = new HashSet<>();
                for (ProductAttrValueAddRequest attrValueReq : attrValues) {
                    allAttrValues.add(attrValueReq.getAttrValue());
                }
                createMultiSpecAttrsFromAllSkus(attrs, allAttrValues);
            } else {
                // 单规格商品：固定属性结构
                ProductAttrAddRequest attr = new ProductAttrAddRequest();
                attr.setAttrName("默认");
                attr.setAttrValues("默认");
                attrs.add(attr);
            }
            
            // 如果没有SKU数据，创建默认的
            if (attrValues.isEmpty()) {
                ProductAttrValueAddRequest attrValue = new ProductAttrValueAddRequest();
                attrValue.setPrice(BigDecimal.ZERO);
                attrValue.setOtPrice(BigDecimal.ZERO);
                attrValue.setVipPrice(BigDecimal.ZERO);
                attrValue.setCost(BigDecimal.ZERO);
                attrValue.setStock(0);
                attrValue.setWeight(BigDecimal.ZERO);
                attrValue.setVolume(BigDecimal.ZERO);
                attrValue.setBrokerage(0);
                attrValue.setBrokerageTwo(0);
                attrValue.setImage("");
                attrValue.setAttrValue("{\"默认\":\"默认\"}");
                attrValues.add(attrValue);
            }
            
            request.setAttr(attrs);
            request.setAttrValue(attrValues);
            
            log.debug("同步SKU信息完成：商品ID={}, 规格类型={}, SKU数量={}", goodsId, request.getSpecType() ? "多规格" : "单规格", attrValues.size());
            
        } catch (Exception e) {
            log.error("同步SKU信息失败: {}", e.getMessage(), e);
            
            // 创建默认的属性和属性值
            List<ProductAttrAddRequest> attrs = new ArrayList<>();
            ProductAttrAddRequest attr = new ProductAttrAddRequest();
            attr.setAttrName("默认");
            attr.setAttrValues("默认");
            attrs.add(attr);

            List<ProductAttrValueAddRequest> attrValues = new ArrayList<>();
            ProductAttrValueAddRequest attrValue = new ProductAttrValueAddRequest();
            attrValue.setPrice(BigDecimal.ZERO);
            attrValue.setOtPrice(BigDecimal.ZERO);
            attrValue.setVipPrice(BigDecimal.ZERO);
            attrValue.setCost(BigDecimal.ZERO);
            attrValue.setStock(0);
            attrValue.setWeight(BigDecimal.ZERO);
            attrValue.setVolume(BigDecimal.ZERO);
            attrValue.setBrokerage(0);
            attrValue.setBrokerageTwo(0);
            attrValue.setImage("");
            attrValue.setAttrValue("{\"默认\":\"默认\"}");
            attrValues.add(attrValue);

            request.setAttr(attrs);
            request.setAttrValue(attrValues);
        }
    }

    /**
     * 将简单规格字符串转换为JSON格式
     */
    private String convertSimpleSpecsToJson(String simpleSpecs) {
        try {
            if (simpleSpecs == null || simpleSpecs.isEmpty()) {
                return "{\"规格\":\"默认\"}";
            }
            
            // 如果已经是JSON格式，直接返回
            if (simpleSpecs.startsWith("{") && simpleSpecs.endsWith("}")) {
                return simpleSpecs;
            }
            
            // 简单转换：假设simpleSpecs是单个值
            return "{\"规格\":\"" + simpleSpecs + "\"}";
        } catch (Exception e) {
            log.error("转换规格格式失败: {}", simpleSpecs, e);
            return "{\"规格\":\"默认\"}";
        }
    }

    /**
     * 为多规格商品创建属性结构
     */
    private void createMultiSpecAttrs(List<ProductAttrAddRequest> attrs, String attrValueJson) {
        try {
            if (attrValueJson == null || attrValueJson.isEmpty()) {
                // 没有规格信息，创建默认的
                ProductAttrAddRequest attr = new ProductAttrAddRequest();
                attr.setAttrName("规格");
                attr.setAttrValues("默认");
                attrs.add(attr);
                return;
            }

            // 简单解析JSON（这里可以根据实际JSON结构优化）
            // 假设JSON格式是 {"颜色":"红色","尺寸":"大"}
            if (attrValueJson.startsWith("{") && attrValueJson.endsWith("}")) {
                // 移除大括号
                String content = attrValueJson.substring(1, attrValueJson.length() - 1);
                String[] pairs = content.split(",");
                
                // 用于收集每个属性的所有可能值
                Map<String, Set<String>> attrMap = new HashMap<>();
                
                for (String pair : pairs) {
                    String[] keyValue = pair.split(":");
                    if (keyValue.length == 2) {
                        String attrName = keyValue[0].trim().replace("\"", "");
                        String attrValue = keyValue[1].trim().replace("\"", "");
                        
                        attrMap.computeIfAbsent(attrName, k -> new HashSet<>()).add(attrValue);
                    }
                }
                
                // 创建属性对象
                for (Map.Entry<String, Set<String>> entry : attrMap.entrySet()) {
                    ProductAttrAddRequest attr = new ProductAttrAddRequest();
                    attr.setAttrName(entry.getKey());
                    // attrValues应该是逗号分隔的字符串
                    attr.setAttrValues(String.join(",", entry.getValue()));
                    attrs.add(attr);
                }
            }
            
            // 如果解析失败或没有解析到属性，创建默认的
            if (attrs.isEmpty()) {
                ProductAttrAddRequest attr = new ProductAttrAddRequest();
                attr.setAttrName("规格");
                attr.setAttrValues("默认");
                attrs.add(attr);
            }
            
        } catch (Exception e) {
            log.error("创建多规格属性失败: {}", attrValueJson, e);
            // 出错时创建默认的
            ProductAttrAddRequest attr = new ProductAttrAddRequest();
            attr.setAttrName("规格");
            attr.setAttrValues("默认");
            attrs.add(attr);
        }
    }

    /**
     * 从所有SKU的规格信息中创建完整的属性结构
     */
    private void createMultiSpecAttrsFromAllSkus(List<ProductAttrAddRequest> attrs, Set<String> allAttrValues) {
        try {
            if (allAttrValues.isEmpty()) {
                // 没有规格信息，创建默认的
                ProductAttrAddRequest attr = new ProductAttrAddRequest();
                attr.setAttrName("规格");
                attr.setAttrValues("默认");
                attrs.add(attr);
                return;
            }

            // 用于收集每个属性的所有可能值
            Map<String, Set<String>> attrMap = new HashMap<>();
            
            // 遍历所有SKU的规格信息
            for (String attrValueJson : allAttrValues) {
                if (attrValueJson != null && attrValueJson.startsWith("{") && attrValueJson.endsWith("}")) {
                    // 移除大括号
                    String content = attrValueJson.substring(1, attrValueJson.length() - 1);
                    String[] pairs = content.split(",");
                    
                    for (String pair : pairs) {
                        String[] keyValue = pair.split(":");
                        if (keyValue.length == 2) {
                            String attrName = keyValue[0].trim().replace("\"", "");
                            String attrValue = keyValue[1].trim().replace("\"", "");
                            
                            attrMap.computeIfAbsent(attrName, k -> new HashSet<>()).add(attrValue);
                        }
                    }
                }
            }
            
            // 创建属性对象
            if (!attrMap.isEmpty()) {
                for (Map.Entry<String, Set<String>> entry : attrMap.entrySet()) {
                    ProductAttrAddRequest attr = new ProductAttrAddRequest();
                    attr.setAttrName(entry.getKey());
                    // attrValues应该是逗号分隔的字符串
                    attr.setAttrValues(String.join(",", entry.getValue()));
                    attrs.add(attr);
                }
            } else {
                // 如果解析失败，创建默认的
                ProductAttrAddRequest attr = new ProductAttrAddRequest();
                attr.setAttrName("规格");
                attr.setAttrValues("默认");
                attrs.add(attr);
            }
            
            log.debug("多规格属性创建完成：共创建{}个属性", attrs.size());
            
        } catch (Exception e) {
            log.error("从所有SKU创建多规格属性失败: {}", allAttrValues, e);
            // 出错时创建默认的
            ProductAttrAddRequest attr = new ProductAttrAddRequest();
            attr.setAttrName("规格");
            attr.setAttrValues("默认");
            attrs.add(attr);
        }
    }

    /**
     * 根据品牌名称查询当前系统的品牌ID
     */
    private Integer getLocalBrandIdByName(String brandName) {
        try {
            ProductBrand productBrand = brandService.getByName(brandName);
            if (productBrand != null) {
                return productBrand.getId();
            }
            return null;
        } catch (Exception e) {
            log.error("根据名称查询本地品牌ID失败：Name={}", brandName, e);
            return null;
        }
    }

    /**
     * 解析轮播图字符串，支持多种格式
     * 支持格式：单个URL、逗号分隔的URL、JSON数组格式
     */
    private List<String> parseSliderImages(String sliderImageStr) {
        List<String> imageList = new ArrayList<>();
        
        try {
            if (sliderImageStr == null || sliderImageStr.trim().isEmpty()) {
                return imageList;
            }
            
            String trimmed = sliderImageStr.trim();
            
            // 如果是JSON数组格式 ["url1", "url2"]
            if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                String content = trimmed.substring(1, trimmed.length() - 1);
                if (!content.trim().isEmpty()) {
                    String[] urls = content.split(",");
                    for (String url : urls) {
                        String cleanUrl = url.trim().replace("\"", "").replace("'", "");
                        if (!cleanUrl.isEmpty()) {
                            imageList.add(cleanUrl);
                        }
                    }
                }
            } 
            // 如果包含逗号，按逗号分隔
            else if (trimmed.contains(",")) {
                String[] urls = trimmed.split(",");
                for (String url : urls) {
                    String cleanUrl = url.trim();
                    if (!cleanUrl.isEmpty()) {
                        imageList.add(cleanUrl);
                    }
                }
            } 
            // 单个URL
            else {
                imageList.add(trimmed);
            }
            
        } catch (Exception e) {
            log.error("解析轮播图字符串失败: {}", sliderImageStr, e);
            // 出错时，如果原字符串不为空，直接作为单个URL使用
            if (sliderImageStr != null && !sliderImageStr.trim().isEmpty()) {
                imageList.add(sliderImageStr.trim());
            }
        }
        
        return imageList;
    }

    /**
     * 将轮播图列表格式化为JSON数组字符串格式 ["url1", "url2", ...]
     */
    private String formatSliderImagesToJson(List<String> imageList) {
        if (imageList == null || imageList.isEmpty()) {
            return "[]";
        }
        
        StringBuilder jsonArray = new StringBuilder("[");
        for (int i = 0; i < imageList.size(); i++) {
            if (i > 0) {
                jsonArray.append(",");
            }
            // 转义双引号并包装在双引号中
            String escapedUrl = imageList.get(i).replace("\"", "\\\"");
            jsonArray.append("\"").append(escapedUrl).append("\"");
        }
        jsonArray.append("]");
        
        return jsonArray.toString();
    }
} 