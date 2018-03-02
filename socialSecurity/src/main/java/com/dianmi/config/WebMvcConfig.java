package com.dianmi.config;


import com.dianmi.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

/**
 * Created by www on 2017/7/21.
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Resource
    TokenInterceptor tokenInterceptor;

    /**
     * 配置拦截器
     *
     * @param registry
     * @author lance
     */
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor).addPathPatterns("/api/**");
    }

}
