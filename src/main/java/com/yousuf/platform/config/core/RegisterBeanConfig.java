package com.yousuf.platform.config.core;

import com.yousuf.platform.common.infrastructure.AuthTokenCache;
import com.yousuf.platform.config.ApplicationConfig;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

/**
 * <p> Title: RegisterBeanConfig
 * <p> Description: 初始化bean配置
 *
 * @author yousuf zhang 2019/11/8
 */
@Configuration
@AllArgsConstructor
public class RegisterBeanConfig {
    private final ApplicationConfig.RegisterBean registerBean;

    @Bean
    public AuthTokenCache authTokenCache() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return (AuthTokenCache) ClassUtils.forName(registerBean.getTokenCacheClass(), null).newInstance();
    }

}
