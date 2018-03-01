package com.sample.config;

import com.sample.util.DateNewUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author www
 * @date 2017/7/16
 * http://localhost:8081/swagger-ui.html
 */
@Configuration
@EnableSwagger2
@ComponentScan(basePackages = {"com.sample.controller"})
public class Swagger2Config {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(getPars());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("api接口文档")
                .description("接口描述")
                .termsOfServiceUrl("www.yblzy.com")
                .version("1.0.0 -发布时间" + DateNewUtil.getDateFormat(new Date(), "yyyy年MM月dd日 HH时mm分ss秒"))
                .build();
    }

    private List<Parameter> getPars() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name("Authorization").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(tokenPar.build());
        return pars;
    }
}
