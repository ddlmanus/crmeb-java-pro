package com.zbkj.service.dao.finance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.finance.CreditPaymentOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 授信支付订单Mapper接口
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
@Mapper
public interface CreditPaymentOrderDao extends BaseMapper<CreditPaymentOrder> {
} 