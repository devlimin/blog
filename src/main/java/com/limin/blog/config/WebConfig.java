package com.limin.blog.config;

import com.limin.blog.inteceptor.LoginInterceptor;
import com.limin.blog.inteceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter{
    @Autowired
    TokenInterceptor tokenInterceptor;
    @Autowired
    LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(tokenInterceptor);
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**/man/**");
    }
}
