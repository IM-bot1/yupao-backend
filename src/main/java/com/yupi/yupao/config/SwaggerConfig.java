package com.yupi.yupao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;

@Configuration
@EnableSwagger2WebMvc   // 开启Swagger2
public class SwaggerConfig {
	// 配置Swagger的Docket实例
    @Bean("defaultApi2")
    public Docket getSwaggerDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
            	.apis(RequestHandlerSelectors.basePackage("com.yupi.yupao.controller"))		// 指定扫描接口
            	.paths(PathSelectors.any())	// 过滤路径
                .build();
    }
    private ApiInfo apiInfo() {
        // 作者信息
        Contact contact = new Contact("zdh", "http://www.yupi.com", "917991980@qq.com");
        return new ApiInfo(
                "用户中心API",
                "用户中心API文档",
                "v2.0",
                "http://www.yupi.com",
                contact,
                "Apache 2.0",
                "www.apache.org/licenses/LICENSE-2.0",
                new ArrayList<>()
        );
    }
}
