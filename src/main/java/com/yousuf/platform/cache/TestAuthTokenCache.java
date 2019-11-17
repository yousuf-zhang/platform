package com.yousuf.platform.cache;

import com.yousuf.platform.common.infrastructure.AuthToken;
import com.yousuf.platform.common.infrastructure.AuthTokenCache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/**
 * <p> Title: CaffeineAuthTokenCache
 * <p> Description: 基于caffeine的缓存
 *
 * @author yousuf zhang 2019/11/8
 */
public class TestAuthTokenCache implements AuthTokenCache {
    @Override
    @Cacheable(value = "jwtToken", key = "#token")
    public AuthToken findCurrentUserByCache(String token) {
        // 直接通过缓存获取值，没有的话返回空值，证明登录超时
        return null;
    }

    @Override
    @CachePut(value = "jwtToken", key = "#token")
    public AuthToken cacheToken(String token, AuthToken authToken) {
        return authToken;
    }

    @Override
    @CacheEvict(value = "jwtToken", key = "#token")
    public void removeCurrentUser(String token) {
        // 调用缓存移除当前用户
    }
}
