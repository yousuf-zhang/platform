package com.yousuf.platform.config.core;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * <p> Title: AuthenticationConfig
 * <p> Description: 授权认证配置文件
 *
 * @author yousuf zhang 2019/11/8
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "platform.auth")
public class AuthConfig {
    /**认证忽略url*/
    private List<String> authenticateExcludeUrl = Lists.newArrayList("/error", "/auth/login");
    /**授权忽略url*/
    private List<String> authorizeExcludeUrl = Lists.newArrayList("/auth/**", "/error");
}
