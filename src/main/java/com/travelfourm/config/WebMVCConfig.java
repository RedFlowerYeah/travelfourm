package com.travelfourm.config;

import com.travelfourm.controller.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 34612
 */

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    /**登录注册拦截器*/
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    /**未读消息拦截器*/
    @Autowired
    private MessageInterceptor messageInterceptor;

    @Autowired
    private DataInterceptor dataInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //错误是路径通配问题，查找发现是spring升级到5.3之后路径通配发生了变化，官方给出的解释是“In Spring MVC, the path was previously analyzed by AntPathMatcher, but it was changed to use PathPatternParser introduced in WebFlux from Spring 5.3.0.”。
        /**
         * 拦截相应的登录状态
         * excluePathPatterns是解除拦截相应的静态资源*/
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/css/*","/js/*","/img/*");


        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/css/*","/js/*","/img/*");

        registry.addInterceptor(dataInterceptor)
                .excludePathPatterns("/css/*","/js/*","/img/*");
    }
}
