package com.zbkj.service.service.finance;

import com.zbkj.common.vo.finance.LivestockInventoryRequestVO;
import com.zbkj.common.vo.finance.LivestockInventoryResponseVO;

/**
 * 牧码通集成服务
 */
public interface MumaIntegrationService {

    /**
     * 查询养殖场存栏数据
     * @param request 请求参数
     * @return 存栏数据
     */
    LivestockInventoryResponseVO.LivestockInventoryData getLivestockInventory(LivestockInventoryRequestVO request);
} 