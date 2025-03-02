package com.yousuf.platform.config;

import com.yousuf.platform.interceptor.AuthorizationInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p> Title: WebAppConfig
 * <p> Description: 系统全局配置
 *
 * @author yousuf zhang 2019/11/8
 */
@Configuration
@AllArgsConstructor
public class WebAppConfig implements WebMvcConfigurer {

    private final AuthorizationInterceptor authorizationInterceptor;
    private final ApplicationConfig.AuthConfig authConfig;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 认证拦截器
        registry.addInterceptor(authorizationInterceptor)
                .excludePathPatterns(authConfig.getAuthenticateExcludeUrl())
                .addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(CorsConfiguration.ALL)
                .allowedHeaders(CorsConfiguration.ALL)
                .allowedMethods(CorsConfiguration.ALL)
                .allowCredentials(true)
                .exposedHeaders("Header1", "Header2");
    }


}
