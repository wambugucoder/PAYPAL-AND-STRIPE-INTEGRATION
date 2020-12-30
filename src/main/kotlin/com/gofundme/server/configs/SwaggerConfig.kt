package com.gofundme.server.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors.regex
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableSwagger2
class SwaggerConfig:WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("swagger-ui.html")
            .addResourceLocations("classpath:/META-INF/resources/")

        registry
            .addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/")
    }

    @Bean
    fun swagger():Docket{

        return Docket(DocumentationType.SWAGGER_2).apiInfo(goFundMeInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.gofundme.server.controller"))
            .paths(apiPaths())
            .build()
    }

    fun goFundMeInfo():ApiInfo{
        return ApiInfoBuilder()
            .title("GOFUNDME_SERVER -API-DOCS")
            .description("A Documentation revolving around the GoFundMe-Server Apis")
            .contact(adminContact())
            .version("0.0.1")
            .license("MIT")
            .build()
    }
    fun adminContact():Contact{
        return Contact("Jos Wambugu","localhost:3000/","josphatwambugu77@gmail.com")

    }
    fun apiPaths(): com.google.common.base.Predicate<String>? {
        return regex("/.*")
    }
}