package com.yousuf.platform.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * <p> Title: ApplicationConfig
 * <p> Description: 配置文件类 把配置文件集中放置
 *
 * @author yousuf zhang 2019/11/17
 */
public class ApplicationConfig {
    /**
     * ClassName: JwtToken
     * <p> Description: 参数配置类
     *
     * @author zhangshuai 2019/11/7
     */
    @Data
    @Configuration
    @ConfigurationProperties(prefix = "platform.jwt")
    public static class JwtTokenConfig {
        private String header = "Authorization";
        private String prefix = "Bearer ";
        private String tokenId = "userId";
        private String tokenName = "username";
        @DurationUnit(ChronoUnit.MINUTES)
        private Duration expiration = Duration.ofMillis(30);
    }

    /**
     * ClassName: RsaConfig
     * <p> Description: RSA参数配置
     *
     * @author zhangshuai 2019/11/7
     */
    @Data
    @Validated
    @Configuration
    @ConfigurationProperties(prefix = "platform.rsa")
    public static class RsaConfig {
        @NotNull
        private String privateKeyPath;
        @NotNull
        private String publicKeyPath;
        // 秘钥长度
        private Integer keySize = 2048;
        //签名算法
        private String signatureAlgorithm = "SHA256withRSA";
        private String chatSet = "UTF-8";
    }

    /**
     * <p> Title: AuthenticationConfig
     * <p> Description: 授权认证配置文件
     *
     * @author yousuf zhang 2019/11/8
     */
    @Data
    @Configuration
    @ConfigurationProperties(prefix = "platform.auth")
    public static class AuthConfig {
        /**认证忽略url*/
        private List<String> authenticateExcludeUrl = Lists.newArrayList("/error", "/auth/login");
    }

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "platform.cache")
    public static class FlexibleCacheConfig {
        public static final String JWT_TOKEN = "jwtToken";
        private String jwtToken = "initialCapacity=50,maximumSize=5000,expireAfterAccess=15m";
        private Map<String, String> cacheSpecs = Maps.newHashMap();
    }

    @Data
    @Configuration
    @Validated
    @ConfigurationProperties(prefix = "platform.register")
    public static class RegisterBean {
        @NotNull
        private String tokenCacheClass;
    }
}
