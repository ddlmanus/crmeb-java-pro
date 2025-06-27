package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zbkj.common.constants.ProductConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.product.ProductAttr;
import com.zbkj.common.model.product.ProductAttrValue;
import com.zbkj.common.model.product.ProductDescription;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * CRMEB官网商品同步服务实现类
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
@Service
public class CrmebProductSyncServiceImpl implements CrmebProductSyncService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrmebProductSyncServiceImpl.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductAttrService productAttrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private ProductDescriptionService productDescriptionService;

    // CRMEB官网API地址
    private static final String CRMEB_API_URL = "https://v5.crmeb.net/api/pc/get_products";
    private static final String AUTHORIZATION_TOKEN = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwd2QiOiJkNDFkOGNkOThmMDBiMjA0ZTk4MDA5OThlY2Y4NDI3ZSIsImlzcyI6InY1LmNybWViLm5ldCIsImF1ZCI6InY1LmNybWViLm5ldCIsImlhdCI6MTc1MDkwODE3NSwibmJmIjoxNzUwOTA4MTc1LCJleHAiOjE3NTM1MDAxNzUsImp0aSI6eyJpZCI6NzY1MTIsInR5cGUiOiJhcGkifX0.NaOHs4-6m39ozIeZgbMOQFheGym4Wh1CmN4j4cbibKM";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean syncProductsFromCrmebApi() {
        try {
            LOGGER.info("开始从CRMEB官网同步商品数据...");

            // 调用CRMEB API获取商品数据
            String jsonData = fetchProductsFromCrmebApi();
            if (StrUtil.isBlank(jsonData)) {
                throw new CrmebException("获取CRMEB商品数据失败");
            }

            // 解析JSON数据
            com.alibaba.fastjson.JSONObject response = JSON.parseObject(jsonData);
            if (response.getInteger("status")!=200) {
                throw new CrmebException("CRMEB API响应失败: " + response.getString("msg"));
            }

            com.alibaba.fastjson.JSONObject data = response.getJSONObject("data");
            JSONArray productList = data.getJSONArray("list");

            if (CollUtil.isEmpty(productList)) {
                LOGGER.warn("CRMEB API返回的商品列表为空");
                return true;
            }

            LOGGER.info("获取到 {} 个商品，开始同步...", productList.size());

            int successCount = 0;
            int failCount = 0;

            // 遍历处理每个商品
            for (int i = 0; i < productList.size(); i++) {
                com.alibaba.fastjson.JSONObject productJson = productList.getJSONObject(i);
                try {
                    syncSingleProduct(productJson);
                    successCount++;
                    LOGGER.info("商品同步成功: ID={}, 名称={}", productJson.getInteger("id"), productJson.getString("store_name"));
                } catch (Exception e) {
                    failCount++;
                    LOGGER.error("商品同步失败: ID={}, 错误: {}", productJson.getInteger("id"), e.getMessage(), e);
                }
            }

            LOGGER.info("CRMEB商品同步完成: 成功{}个, 失败{}个", successCount, failCount);
            return true;

        } catch (Exception e) {
            LOGGER.error("CRMEB商品同步过程中发生错误", e);
            throw new CrmebException("商品同步失败: " + e.getMessage());
        }
    }

    /**
     * 从CRMEB API获取商品数据
     */
    private String fetchProductsFromCrmebApi() {
        try {
            HttpResponse response = HttpRequest.get(CRMEB_API_URL)
                    .header("accept", "application/json, text/plain, */*")
                    .header("accept-language", "zh,en;q=0.9,zh-CN;q=0.8")
                    .header("authorization", AUTHORIZATION_TOKEN)
                    .header("form-type", "pc")
                    .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36")
                    .form("page", 1)
                    .form("limit", 100000)
                    .timeout(30000)
                    .execute();

            if (response.getStatus() != 200) {
                LOGGER.error("调用CRMEB API失败，状态码: {}", response.getStatus());
                return null;
            }

            return response.body();

        } catch (Exception e) {
            LOGGER.error("调用CRMEB API异常", e);
            return null;
        }
    }

    /**
     * 同步单个商品
     */
    private void syncSingleProduct(com.alibaba.fastjson.JSONObject productJson) {
        Integer productId = productJson.getInteger("id");
        
        // 检查商品是否已存在
        LambdaQueryWrapper<Product> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Product::getId, productId);
        Product existingProduct = productService.getOne(wrapper);

        Product product = new Product();
        if (ObjectUtil.isNotNull(existingProduct)) {
            // 更新现有商品
            product = existingProduct;
        } else {
            // 创建新商品
            product.setId(productId);
            product.setCreateTime(new Date());
        }

        // 设置商品基本信息
        product.setName(productJson.getString("store_name"));
        product.setImage(productJson.getString("image"));
        product.setSales(productJson.getInteger("sales"));
        product.setPrice(new BigDecimal(productJson.getString("price")));
        product.setStock(productJson.getInteger("stock"));
        product.setOtPrice(new BigDecimal(productJson.getString("ot_price")));
        product.setSpecType(productJson.getBoolean("spec_type"));
        product.setUnitName(productJson.getString("unit_name"));
        product.setCateId(productJson.getString("cate_id"));
        
        // 设置默认值
        product.setMerId(0); // 平台商品
        product.setIsShow(true);
        product.setIsDel(false);
        product.setIsRecycle(false);
        product.setAuditStatus(ProductConstants.AUDIT_STATUS_SUCCESS);
        product.setIsAudit(false);
        product.setType(ProductConstants.PRODUCT_TYPE_NORMAL);
        product.setMarketingType(ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        product.setUpdateTime(new Date());
        
        // 保存或更新商品主表
        productService.saveOrUpdate(product);

        // 处理商品描述
        String description = productJson.getString("description");
        if (StrUtil.isNotBlank(description)) {
            syncProductDescription(productId, description);
        }

        // 处理商品属性
        JSONArray attrs = productJson.getJSONArray("attrs");
        if (CollUtil.isNotEmpty(attrs)) {
            syncProductAttrs(productId, attrs);
        }
    }

    /**
     * 同步商品描述
     */
    private void syncProductDescription(Integer productId, String description) {
        // 查找是否已存在描述
        LambdaQueryWrapper<ProductDescription> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ProductDescription::getProductId, productId)
               .eq(ProductDescription::getType, ProductConstants.PRODUCT_TYPE_NORMAL)
               .eq(ProductDescription::getMarketingType, ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        
        ProductDescription productDescription = productDescriptionService.getOne(wrapper);
        if (ObjectUtil.isNull(productDescription)) {
            productDescription = new ProductDescription();
            productDescription.setProductId(productId);
            productDescription.setType(ProductConstants.PRODUCT_TYPE_NORMAL);
            productDescription.setMarketingType(ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        }
        
        productDescription.setDescription(description);
        productDescriptionService.saveOrUpdate(productDescription);
    }

    /**
     * 同步商品属性和属性值
     */
    private void syncProductAttrs(Integer productId, JSONArray attrs) {
        // 删除现有的属性和属性值
        LambdaQueryWrapper<ProductAttr> attrWrapper = Wrappers.lambdaQuery();
        attrWrapper.eq(ProductAttr::getProductId, productId)
                   .eq(ProductAttr::getType, ProductConstants.PRODUCT_TYPE_NORMAL)
                   .eq(ProductAttr::getMarketingType, ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        productAttrService.remove(attrWrapper);

        LambdaQueryWrapper<ProductAttrValue> attrValueWrapper = Wrappers.lambdaQuery();
        attrValueWrapper.eq(ProductAttrValue::getProductId, productId)
                        .eq(ProductAttrValue::getType, ProductConstants.PRODUCT_TYPE_NORMAL)
                        .eq(ProductAttrValue::getMarketingType, ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        productAttrValueService.remove(attrValueWrapper);

        // 提取所有SKU名称作为属性值
        List<String> attrValues = new ArrayList<>();
        for (int i = 0; i < attrs.size(); i++) {
            com.alibaba.fastjson.JSONObject attr = attrs.getJSONObject(i);
            String suk = attr.getString("suk");
            if (StrUtil.isNotBlank(suk)) {
                attrValues.add(suk);
            }
        }

        if (CollUtil.isNotEmpty(attrValues)) {
            // 创建属性记录
            ProductAttr productAttr = new ProductAttr();
            productAttr.setProductId(productId);
            productAttr.setAttrName("规格");
            productAttr.setAttrValues(JSON.toJSONString(attrValues));
            productAttr.setType(ProductConstants.PRODUCT_TYPE_NORMAL);
            productAttr.setMarketingType(ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
            productAttr.setIsDel(false);
            productAttrService.save(productAttr);

            // 创建属性值记录
            for (int i = 0; i < attrs.size(); i++) {
                com.alibaba.fastjson.JSONObject attr = attrs.getJSONObject(i);
                
                ProductAttrValue attrValue = new ProductAttrValue();
                attrValue.setProductId(productId);
                attrValue.setSku(attr.getString("suk"));
                attrValue.setStock(attr.getInteger("stock"));
                attrValue.setSales(attr.getInteger("sales"));
                attrValue.setPrice(new BigDecimal(attr.getString("price")));
                attrValue.setImage(attr.getString("image"));
                
                // 处理可选字段，避免空值
                String cost = attr.getString("cost");
                attrValue.setCost(new BigDecimal(cost != null ? cost : "0.00"));
                
                String otPrice = attr.getString("ot_price");
                attrValue.setOtPrice(new BigDecimal(otPrice != null ? otPrice : "0.00"));
                
                String weight = attr.getString("weight");
                attrValue.setWeight(new BigDecimal(weight != null ? weight : "0.00"));
                
                String volume = attr.getString("volume");
                attrValue.setVolume(new BigDecimal(volume != null ? volume : "0.00"));
                
                attrValue.setBrokerage(attr.getInteger("brokerage") != null ? attr.getInteger("brokerage") : 0);
                attrValue.setBrokerageTwo(attr.getInteger("brokerage_two") != null ? attr.getInteger("brokerage_two") : 0);
                attrValue.setType(ProductConstants.PRODUCT_TYPE_NORMAL);
                attrValue.setQuota(attr.getInteger("quota") != null ? attr.getInteger("quota") : 0);
                attrValue.setQuotaShow(attr.getInteger("quota_show") != null ? attr.getInteger("quota_show") : 0);
                attrValue.setAttrValue(JSON.toJSONString(attr));
                attrValue.setIsDel(false);
                
                String vipPrice = attr.getString("vip_price");
                attrValue.setVipPrice(new BigDecimal(vipPrice != null ? vipPrice : "0.00"));
                
                attrValue.setMarketingType(ProductConstants.PRODUCT_MARKETING_TYPE_BASE);

                productAttrValueService.save(attrValue);
            }
        }
    }
} 