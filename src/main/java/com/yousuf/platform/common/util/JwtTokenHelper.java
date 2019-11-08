package com.yousuf.platform.common.util;

import com.yousuf.platform.common.core.ApplicationContextHelper;
import io.jsonwebtoken.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

/**
 * ClassName: JwtTokenHelper
 * Description: jwt token 辅助类
 *
 * @author zhangshuai 2019/11/7
 */
@Slf4j
@Component
@DependsOn({"applicationContextHelper", "rsaHelper"})
public class JwtTokenHelper {
    private static JwtTokenConfig jwtTokenConfig;

    @PostConstruct
    public void init() {
        jwtTokenConfig = ApplicationContextHelper.getBean(JwtTokenConfig.class);
    }

    /**
     * Title: generateToken
     * Description: 生成token
     *
     * @param tokenId tokenId的名称
     * @param tokenName tokenName
     *
     * @return java.lang.String
     *
     * @author zhangshuai 2019/11/7
     *
     */
    public static String generateToken(String tokenId, String tokenName) {
        return Jwts.builder()
                .claim(jwtTokenConfig.getTokenId(), tokenId)
                .claim(jwtTokenConfig.getTokenName(), tokenName)
                .setSubject(tokenName)
                .setExpiration(DateUtils.localDateTimeToDateConverter(
                    LocalDateTime.now()
                    .plusMinutes(jwtTokenConfig.getExpiration().toMinutes())
                ))
                .signWith(SignatureAlgorithm.RS256, RsaHelper.PRIVATE_KEY)
                .setIssuedAt(new Date())
                .compact();
    }
    /**
     * Title: parseToken
     * Description: 解析 token
     *
     * @param token token值
     *
     * @return org.apache.commons.lang3.tuple.Pair<java.lang.String,java.lang.String>
     *
     * @author zhangshuai 2019/11/7
     *
     */
    public static Pair<String, String> parseToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        Jws<Claims> claims = Jwts.parser().setSigningKey(RsaHelper.PUBLIC_KEY).parseClaimsJws(token);
        return Pair.of(Objects.toString(claims.getBody().get(jwtTokenConfig.getTokenId())),
                Objects.toString(claims.getBody().get(jwtTokenConfig.getTokenName())));
    }

    /**
     * Title: verify
     * Description: 校验事件是否过期
     *
     * @param token token
     *
     * @return boolean
     *
     * @author zhangshuai 2019/11/8
     *
     */
    public static boolean verify(String token) {
        boolean expiration = true;
        try {
            expiration = Jwts.parser()
                    .setSigningKey(RsaHelper.PUBLIC_KEY)
                    .parseClaimsJws(token)
                .getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException ex) {
            log.warn("token已过期, token -> {}", token);
        } catch (SignatureException ex) {
            log.warn("token被人篡改, token -> {}", token);
        } catch (MalformedJwtException ex) {
            log.warn("token格式错误, token -> {}", token);
        }
        return expiration;
    }

    /**
     * Title: findTokenByRequest
     * Description: 根据 request header获取token
     *
     * @return java.lang.String
     *
     * @author zhangshuai 2019/11/8
     *
     */
    public static String findTokenByRequest() {
        String token = WebUtils.getRequest().getHeader(getHeader());
        boolean flag = StringUtils.isNotBlank(token)
                && token.startsWith(getPrefix());
        if (!flag) {
           return "";
        }
        return token.substring(getPrefix().length());
    }

    private static String getHeader() {
        return jwtTokenConfig.getHeader();
    }

     public static String getPrefix() {
        return jwtTokenConfig.getPrefix();
    }
    /**
     * ClassName: JwtToken
     * Description: 参数配置类
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
}
