package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.merchant.MerchantAddress;
import com.zbkj.common.request.merchant.MerchantAddressSaveRequest;
import com.zbkj.common.request.merchant.MerchantAddressSearchRequest;

import java.util.List;

/**
 * MerchantAddressService 接口实现
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
public interface MerchantAddressManagerService extends IService<MerchantAddress> {

    /**
     * 商户地址列表
     * @param request 请求参数
     * @return List
     */
    List<MerchantAddress> findList(MerchantAddressSearchRequest request);

    /**
     * 新增商户地址
     * @param request 请求参数
     * @return 新增结果
     */
    Boolean add(MerchantAddressSaveRequest request);

    /**
     * 修改商户地址
     * @param request 请求参数
     * @return 修改结果
     */
    Boolean updateAddress(MerchantAddressSaveRequest request);

    /**
     * 删除商户地址
     * @param id 商户地址ID
     * @return 删除结果
     */
    Boolean delete(Integer id);

    /**
     * 设置商户默认地址
     * @param id 商户地址ID
     */
    Boolean setDefault(Integer id);

    /**
     * 设置商户地址开启状态
     * @param id 商户地址ID
     */
    Boolean updateShow(Integer id);

}
