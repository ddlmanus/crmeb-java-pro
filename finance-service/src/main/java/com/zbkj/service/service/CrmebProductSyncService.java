package com.zbkj.service.service;

/**
 * CRMEB官网商品同步服务接口
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
public interface CrmebProductSyncService {

    /**
     * 从CRMEB官网同步商品数据
     *
     * @return Boolean 同步结果
     */
    Boolean syncProductsFromCrmebApi();
} 