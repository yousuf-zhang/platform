package com.yousuf.platform.config.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@ConfigurationProperties(prefix = "platform.cache")
@ConditionalOnClass(value = {Caffeine.class})
public class FlexibleCaffeineCacheManager extends CaffeineCacheManager implements InitializingBean {
    @Getter
    @Setter
    private Map<String, String> cacheSpecs = Maps.newHashMap();

    private Map<String, Caffeine<Object, Object>> builders = Maps.newHashMap();

    private CacheLoader cacheLoader;
    @Override
    public void afterPropertiesSet() throws Exception {
        for (Map.Entry<String, String> cacheSpecEntry : cacheSpecs.entrySet()) {
            builders.put(cacheSpecEntry.getKey(), Caffeine.from(cacheSpecEntry.getValue()));
        }
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
