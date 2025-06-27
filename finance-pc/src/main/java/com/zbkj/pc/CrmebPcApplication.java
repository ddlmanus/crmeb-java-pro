package com.zbkj.pc;

import com.binarywang.spring.starter.wxjava.miniapp.config.WxMaAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * PC端应用启动类
 * @author: crmeb
 * @date: 2024-01-01
 */
@EnableAsync //开启异步调用
@EnableSwagger2
@Configuration
@EnableTransactionManagement
//@SpringBootApplication(exclude = DataSourceAutoConfiguration.class) //去掉数据源
@SpringBootApplication(exclude = {WxMaAutoConfiguration.class}) //去掉数据源
@ComponentScan(basePackages = {"com.zbkj", "com.zbkj.pc"})
@MapperScan(basePackages = {"com.zbkj.**.dao"})
public class CrmebPcApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrmebPcApplication.class, args);
    }
} 