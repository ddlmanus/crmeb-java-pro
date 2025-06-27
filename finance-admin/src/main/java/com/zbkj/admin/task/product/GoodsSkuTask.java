package com.zbkj.admin.task.product;

import com.zbkj.common.response.ProductSyncResponse;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.service.ProductSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component("GoodsSkuTask")
public class GoodsSkuTask {
    private static final Logger logger = LoggerFactory.getLogger(GoodsSkuTask.class);

    @Autowired
    private ProductSyncService productSyncService;

    /**
     * 同步三牧优选商品sku信息
     * 每天凌晨2点执行：0 0 2 * * ?
     */
    public void execute() {
        logger.info("---GoodsSkuTask task------开始同步三牧优选数据: Execution Time - {}", CrmebDateUtil.nowDateTime());
        try {
            // 执行全量数据同步
            ProductSyncResponse result = productSyncService.syncProducts(null, null);
            if (result.getSuccess()) {
                logger.info("三牧优选数据同步成功完成");
            } else {
                logger.error("三牧优选数据同步失败");
            }
        } catch (Exception e) {
            logger.error("GoodsSkuTask.execute 执行失败: " + e.getMessage(), e);
        }
    }
}
