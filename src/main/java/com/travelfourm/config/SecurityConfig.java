package com.travelfourm.config;

import com.travelfourm.Util.CommunityConstant;
import com.travelfourm.Util.CommunityUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * SpringSecurity 配置类
 * @author 34612*/

/**
 * WebSecurityConfigurerAdapter 继承了 WebSecurityConfigurer，
 * 而通过WebSecurityConfigurerAdapter可以自定义相应的配置类*/

@Component
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    /**
     * 核心配置类，这里一般用来拦截页面的静态资源*/
    @Override
    public void configure(WebSecurity web) throws Exception {

        /**
         * 拦截静态资源*/
        web.ignoring().antMatchers("/resources/**");
    }

    /**
     * 安全过滤器链配置的方法*/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * 通过 authorizeRequests() 方法来开始请求权限配置
         * .antMatchers() 表示在在x权限下访问的接口
         * .hasAnyAuthority() 表示x权限
         * 每一个不同的antMatchers都代表着不同权限下的资源的管控
         * .anyRequest().permitAll() 表示其他请求可无条件访问
         * 这里默认会开启csrf控制访问权限，代表刷新页面后，需要重新登录或重载到当前的用户身份才可以进行其他资源的访问
         * */
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "discuss/add",
                        "comment/add/**",
                        "letter/**",
                        "notice/**",
                        "like",
                        "follow",
                        "unfollow"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR,
                        AUTHORITY_USER
                )
                .antMatchers(
                        "/discuss/top",
                        "/discuss/wonderful")
                .hasAnyAuthority(
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR
                )
                .antMatchers("/discuss/delete",
                        "/data/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll();

        /**
         * 异常处理
         * */
        http.exceptionHandling()

                /**
                 * AuthenticationEntryPoint 是用户认证时候抛出的异常*/
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    /**
                     * 用户没有登录时所进行的处理*/
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

                        /**
                         * 没有登录时进行的处理
                         * 首先判断请求头是异步请求还是同步请求
                         * 如果是ajax请求，则提示需要登录，否则则返回登录页面*/
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)){
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJsonString(403,"您还没有登录，请先进行登录"));
                        }else {
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {

                    /**
                     * 权限不足时进行的处理*/
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)){

                            //异步请求，期待返回xml->json
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJsonString(403,"您没有此功能的访问权限,请联系管理员"));
                        }else {
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

        //Security底层默认会拦截/logout请求，进行退出处理
        //覆盖logout底层代码逻辑，执行自己的logout代码请求

        http.logout().logoutUrl("/securitylogout");
    }
}
