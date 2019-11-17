package com.yousuf.platform.cache;

import com.yousuf.platform.common.infrastructure.AuthToken;
import com.yousuf.platform.common.infrastructure.AuthTokenCache;
import com.yousuf.platform.config.ApplicationConfig;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/**
 * <p> Title: CaffeineAuthTokenCache
 * <p> Description: 基于caffeine的缓存
 *
 * @author yousuf zhang 2019/11/8
 */
@CacheConfig(cacheNames = ApplicationConfig.FlexibleCacheConfig.JWT_TOKEN)
public class CaffeineAuthTokenCache implements AuthTokenCache {
    @Override
    @Cacheable(value = ApplicationConfig.FlexibleCacheConfig.JWT_TOKEN, key = "#token")
    public AuthToken findCurrentUserByCache(String token) {
        // 直接通过缓存获取值，没有的话返回空值，证明登录超时
        return null;
    }

    @Override
    @CachePut(value = ApplicationConfig.FlexibleCacheConfig.JWT_TOKEN, key = "#token")
    public AuthToken cacheToken(String token, AuthToken authToken) {
        return authToken;
    }

    @Override
    @CacheEvict(value = ApplicationConfig.FlexibleCacheConfig.JWT_TOKEN, key = "#token")
    public void removeCurrentUser(String token) {
        // 调用缓存移除当前用户
    }
}
