package com.yousuf.platform.config.core;

import com.yousuf.platform.common.infrastructure.AuthTokenCache;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * <p> Title: RegisterBeanConfig
 * <p> Description: //TODO
 *
 * @author yousuf zhang 2019/11/8
 */
@Configuration
@AllArgsConstructor
public class RegisterBeanConfig {
    private final RegisterBean registerBean;
    @Bean
    public AuthTokenCache authTokenCache() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return (AuthTokenCache) Class.forName(registerBean.getTokenCacheClass()).newInstance();
    }
    @Data
    @Configuration
    @Validated
    @ConfigurationProperties(prefix = "platform.register")
    public static class RegisterBean {
        @NotNull
        private String tokenCacheClass;
        private String tokenCacheName = "authTokenCache";
    }
}
