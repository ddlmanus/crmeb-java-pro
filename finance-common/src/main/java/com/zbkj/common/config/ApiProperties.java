package com.zbkj.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * API 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "api")
public class ApiProperties {
    
    /**
     * 养殖场编码
     */
    private String farmCode = "01020420103569752";
    
    /**
     * 养殖品种API地址
     */
    private String breedingProductUrl = "https://mmt.haoyicn.cn/data-service/api/species";
    
    /**
     * 存栏量API地址
     */
    private String stockQuantityUrl = "https://mmt.haoyicn.cn/data-service/api/yangzhicx_report9_data_1";
    
    /**
     * 认证令牌
     */
    private String authToken="zAXyemg13M4Lc7tKhigKJ9jmpdiktP7W";
}
