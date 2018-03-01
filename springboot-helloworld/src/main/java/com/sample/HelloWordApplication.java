package com.sample;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Spring Boot 应用启动类
 * <p>
 *
 * @author bysocket
 * @date 16/4/26
 */
@SpringBootApplication
@MapperScan("com.sample.dao")
public class HelloWordApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelloWordApplication.class, args);
    }
}
