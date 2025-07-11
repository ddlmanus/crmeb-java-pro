package com.zbkj.front.config;

import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.Constants;
import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Swagger配置组件
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
@Configuration
@EnableSwagger2
@ConfigurationProperties(prefix = "api.doc")
public class SwaggerConfig{

    //是否开启swagger，正式环境一般是需要关闭的，可根据springboot的多环境配置进行设置
    Boolean swaggerEnabled = true;

    @Autowired
    CrmebConfig crmebConfig;

    @Bean("front")
    public Docket create1RestApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("front")
                .host(crmebConfig.getDomain())
                .apiInfo(apiInfo())
                // 是否开启
                .enable(swaggerEnabled)
                .select()
                // 扫描的路径包
                .apis(RequestHandlerSelectors.basePackage("com.zbkj.front"))
                // 指定路径处理PathSelectors.any()代表所有的路径
                .paths(frontPathsAnt()) //只监听
                .build()
                .securitySchemes(security())
                .securityContexts(securityContexts())
//                .globalOperationParameters(pars) // 针对单个url的验证 如果需要的话
                .pathMapping("/");
    }

    @Bean("public")
    public Docket create2RestApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("public")
                .host(crmebConfig.getDomain())
                .apiInfo(apiInfo())
                // 是否开启
                .enable(swaggerEnabled)
                .select()
                // 扫描的路径包
                .apis(RequestHandlerSelectors.basePackage("com.zbkj.front"))
                // 指定路径处理PathSelectors.any()代表所有的路径
                .paths(publicPathsAnt()) //只监听
                .build()
                .securitySchemes(security())
                .securityContexts(securityContexts())
//                .globalOperationParameters(pars) // 针对单个url的验证 如果需要的话
                .pathMapping("/");
    }

    private Predicate<String> frontPathsAnt() {
        return PathSelectors.ant("/api/front/**");
    }

    private Predicate<String> publicPathsAnt() {
        return PathSelectors.ant("/api/public/**");
    }

    private List<ApiKey> security() {
        return newArrayList(
                new ApiKey(Constants.HEADER_AUTHORIZATION_KEY, Constants.HEADER_AUTHORIZATION_KEY, "header")
        );
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("Crmeb","https://www.crmeb.com/index/java_merchant", "278437628@qq.com");
        return new ApiInfoBuilder()
                .title("Crmeb Java 多商户用户侧")
                .description("Java多商户接口文档")
                .contact(contact)
                .termsOfServiceUrl("https://www.crmeb.com/index/java_merchant")
                .version("1.4.0").build();
    }


    private List<SecurityContext> securityContexts() {
        List<SecurityContext> res = new ArrayList<>();
        res.add(SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/.*"))
                .build());
        return res;
    }

    private List<SecurityReference> defaultAuth() {
        List<SecurityReference> res = new ArrayList<>();
        AuthorizationScope authorizationScope = new AuthorizationScope("global", Constants.HEADER_AUTHORIZATION_KEY);
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        res.add(new SecurityReference(Constants.HEADER_AUTHORIZATION_KEY, authorizationScopes));
        return res;
    }
}
