package com.zbkj.admin.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * 第三方数据源配置
 * 用于连接三牧优选数据库
 */
@Configuration
@ConditionalOnProperty(prefix = "third-party", name = "enabled", havingValue = "true")
@MapperScan(basePackages = "com.zbkj.service.dao.third", sqlSessionFactoryRef = "thirdSqlSessionFactory")
public class ThirdPartyDataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(ThirdPartyDataSourceConfig.class);

    /**
     * 第三方数据源
     */
    @Bean(name = "thirdDataSource")
    @ConfigurationProperties(prefix = "third-party.datasource")
    public DataSource thirdDataSource() {
        logger.info("正在创建第三方数据源...");
        DruidDataSource dataSource = new DruidDataSource();
        logger.info("第三方数据源创建完成");
        return dataSource;
    }

    /**
     * 第三方数据源事务管理器
     */
    @Bean(name = "thirdTransactionManager")
    public DataSourceTransactionManager thirdTransactionManager(@Qualifier("thirdDataSource") DataSource dataSource) {
        logger.info("正在创建第三方数据源事务管理器...");
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 第三方SqlSessionFactory
     * 注意：由于使用注解方式的Mapper，不需要设置mapper文件路径
     */
    @Bean(name = "thirdSqlSessionFactory")
    public SqlSessionFactory thirdSqlSessionFactory(@Qualifier("thirdDataSource") DataSource dataSource) throws Exception {
        logger.info("正在创建第三方SqlSessionFactory...");
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        
        // 设置MyBatis配置，避免与主数据源冲突
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setDefaultStatementTimeout(30);
        bean.setConfiguration(configuration);
        
        // 不设置mapperLocations，因为使用注解方式的Mapper
        return bean.getObject();
    }
} 