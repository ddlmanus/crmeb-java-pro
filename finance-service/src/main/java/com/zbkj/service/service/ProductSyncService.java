package com.zbkj.service.service;

import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.request.ProductSyncRequest;
import com.zbkj.common.response.ProductSyncResponse;

/**
 * 商品同步服务接口
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
public interface ProductSyncService {

    /**
     * 同步商品
     *
     * @param request 同步请求参数
     * @param admin 当前管理员信息
     * @return ProductSyncResponse
     */
    ProductSyncResponse syncProducts(ProductSyncRequest request, SystemAdmin admin);

    /**
     * 同步商品分类
     */
    ProductSyncResponse syncCategories();
} 