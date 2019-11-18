package com.yousuf.platform.config.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Maps;
import com.yousuf.platform.config.ApplicationConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * <p> ClassName: FlexibleCaffeineCacheManager
 * <p> Description: 自定义缓存管理器
 *
 * @author zhangshuai 2019/11/12
 */
@Configuration("cacheManager")
@ConditionalOnClass(value = Caffeine.class)
public class FlexibleCaffeineCacheManager extends CaffeineCacheManager implements InitializingBean {
    private final ApplicationConfig.FlexibleCacheConfig flexibleCacheConfig;
    private Map<String, Caffeine<Object, Object>> builders = Maps.newHashMap();
    private CacheLoader cacheLoader;

    public FlexibleCaffeineCacheManager(ApplicationConfig.FlexibleCacheConfig flexibleCacheConfig) {
        super();
        this.flexibleCacheConfig = flexibleCacheConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        builders.put(ApplicationConfig.FlexibleCacheConfig.JWT_TOKEN, Caffeine.from(flexibleCacheConfig.getJwtToken()));
        this.flexibleCacheConfig.getCacheSpecs().forEach((key, value) -> {
            builders.put(key, Caffeine.from(value));
        });
    }


    @Override
    @SuppressWarnings({"unchecked", "NullableProblems"})
    protected Cache<Object, Object> createNativeCaffeineCache(String name) {
        Caffeine<Object, Object> builder = builders.get(name);
        if (builder == null) {
            return super.createNativeCaffeineCache(name);
        }

        if (this.cacheLoader != null) {
            return builder.build(this.cacheLoader);
        } else {
            return builder.build();
        }
    }


    @Override
    @SuppressWarnings({"unchecked", "NullableProblems"})
    public void setCacheLoader(CacheLoader cacheLoader) {
        super.setCacheLoader(cacheLoader);
        this.cacheLoader = cacheLoader;
    }



}
