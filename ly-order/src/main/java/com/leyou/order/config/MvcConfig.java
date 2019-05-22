package com.leyou.order.config;

import com.leyou.order.interceptors.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(Jwtproperties.class)
public class MvcConfig implements WebMvcConfigurer {
    @Autowired
    private Jwtproperties prop;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor(prop)).addPathPatterns("/order/**");
    }
}
