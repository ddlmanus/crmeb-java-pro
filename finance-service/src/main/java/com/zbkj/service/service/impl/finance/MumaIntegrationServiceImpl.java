package com.zbkj.service.service.impl.finance;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.vo.finance.LivestockInventoryRequestVO;
import com.zbkj.common.vo.finance.LivestockInventoryResponseVO;

import com.zbkj.service.service.finance.MumaIntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 牧码通集成服务实现
 */
@Slf4j
@Service
public class MumaIntegrationServiceImpl implements MumaIntegrationService {

    @Value("${api.base-url:https://mmt.haoyicn.cn}")
    private String apiBaseUrl;

    @Value("${api.token:zAXyemg13M4Lc7tKhigKJ9jmpdiktP7W}")
    private String apiToken;



    @Override
    public LivestockInventoryResponseVO.LivestockInventoryData getLivestockInventory(LivestockInventoryRequestVO request) {
        // 检查配置
        if (apiToken == null || apiToken.isEmpty()) {
            throw new CrmebException("牧码通配置不完整，请检查apiToken配置");
        }

        try {
            // 构建请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("farm_code", request.getFarmCode());

            // 发送API请求
            String url = apiBaseUrl + "/data-service/api/yangzhicx_report9_data_1";
            log.info("开始查询牧码通存栏数据，URL: {}, 请求参数: {}", url, params);
            
            HttpResponse response = HttpRequest.post(url)
                    .header("Content-Type", "application/json")
                    .header("apiToken", apiToken)
                    .body(JSONUtil.toJsonStr(params))
                    .execute();

            if (!response.isOk()) {
                log.error("获取养殖场存栏数据失败: {}", response.body());
                throw new CrmebException("获取养殖场存栏数据失败: " + response.body());
            }

            log.info("牧码通存栏数据响应: {}", response.body());

            // 解析响应
            LivestockInventoryResponseVO inventoryResponse = JSONUtil.toBean(response.body(), LivestockInventoryResponseVO.class);
            
            if (inventoryResponse != null && inventoryResponse.getCode() == 0 && inventoryResponse.getData() != null) {
                return inventoryResponse.getData();
            } else {
                String errorMsg = inventoryResponse != null ? inventoryResponse.getMsg() : "未知错误";
                throw new CrmebException("查询牧码通存栏数据失败: " + errorMsg);
            }
        } catch (CrmebException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询牧码通存栏数据异常", e);
            throw new CrmebException("查询牧码通存栏数据异常: " + e.getMessage());
        }
    }
} 