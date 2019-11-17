## Caffeine Cache 缓存集成
### Caffeine常用配置说明:  
> initialCapacity=[integer]: 初始的缓存空间大小  
> maximumSize=[long]: 缓存的最大条数  
> maximumWeight=[long]: 缓存的最大权重  
> expireAfterAccess=[duration]: 最后一次写入或访问后经过固定时间过期  
> expireAfterWrite=[duration]: 最后一次写入后经过固定时间过期  
> refreshAfterWrite=[duration]: 创建缓存或者最近一次更新缓存后经过固定的时间间隔，刷新缓存  
> weakKeys: 打开key的弱引用  
> weakValues：打开value的弱引用  
> softValues：打开value的软引用  
> recordStats：开发统计功能  
> 注意：  
> expireAfterWrite和expireAfterAccess同时存在时，以expireAfterWrite为准。  
> maximumSize和maximumWeight不可以同时使用  
> weakValues和softValues不可以同时使用

### springboot 集成 caffeine
```yaml
cache:
  type: caffeine
  cache-names: jwtToken
  caffeine:
    spec: initialCapacity=50,maximumSize=5000,expireAfterWrite=15m
```
这种方式不够灵活, 所有的缓存都共享一个策略,不用 `springboot starter`的配置,自己写一个缓存控制器. 继承 `CaffeineCacheManager`
重写了 `createNativeCaffeineCache`, 然后对外暴露了一个map来配置不同策略的缓存, 因为jwtToken为本脚手架自带缓存,所以单独出来写.
```java
@Configuration("cacheManager")
@ConditionalOnClass(value = Caffeine.class)
public class FlexibleCaffeineCacheManager extends CaffeineCacheManager implements InitializingBean {
    private final FlexibleCacheConfig flexibleCacheConfig;
    private Map<String, Caffeine<Object, Object>> builders = Maps.newHashMap();
    private CacheLoader cacheLoader;

    public FlexibleCaffeineCacheManager(FlexibleCacheConfig flexibleCacheConfig) {
        super();
        this.flexibleCacheConfig = flexibleCacheConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        builders.put(FlexibleCacheConfig.JWT_TOKEN, Caffeine.from(flexibleCacheConfig.getJwtToken()));
        for (Map.Entry<String, String> cacheSpecEntry : flexibleCacheConfig.getCacheSpecs().entrySet()) {
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


    @Data
    @Valid
    @Configuration
    @ConfigurationProperties(prefix = "platform.cache")
    public static class FlexibleCacheConfig {
        public static final String JWT_TOKEN = "jwtToken";
        private String jwtToken = "initialCapacity=50,maximumSize=5000,expireAfterAccess=15m";
        private Map<String, String> cacheSpecs = Maps.newHashMap();
    }
}
```
缓存的配置文件:  
```yaml
  cache:
    jwt-token: initialCapacity=50,maximumSize=5000,expireAfterAccess=15m,recordStats
    cache-specs:
      test: initialCapacity=50,maximumSize=5000,expireAfterWrite=15m
```
接下来开始写缓存,这里为了以后可以无缝切换到redis缓存去,我们先设计了一个接口用户设置登录用户缓存,其中`AuthToken`为当前用户信息,
`AuthTokenCache`为登录缓存服务,具体代码如下
```java
public interface AuthToken extends Serializable {
    String getUsername();
    String getUserId();
    default String getLikeName() {
        return null;
    }
    default String getLoginIp() {
        return null;
    }
    default Integer isFull() {
        return 0;
    }
}
```
```java
public interface AuthTokenCache {
    /**缓存名称*/
    String AUTH_TOKEN_CACHE_NAME = "authTokenCache";
    AuthToken findCurrentUserByCache(String token);
    AuthToken cacheToken(String token, AuthToken authToken);
    void removeCurrentUser(String token);
}
```
接下来我们基于 caffeine来实现一个缓存
```java
@CacheConfig(cacheNames = FlexibleCaffeineCacheManager.FlexibleCacheConfig.JWT_TOKEN)
public class CaffeineAuthTokenCache implements AuthTokenCache {
    @Override
    @Cacheable(value = FlexibleCaffeineCacheManager.FlexibleCacheConfig.JWT_TOKEN, key = "#token")
    public AuthToken findCurrentUserByCache(String token) {
        // 直接通过缓存获取值，没有的话返回空值，证明登录超时
        return null;
    }

    @Override
    @CachePut(value = FlexibleCaffeineCacheManager.FlexibleCacheConfig.JWT_TOKEN, key = "#token")
    public AuthToken cacheToken(String token, AuthToken authToken) {
        return authToken;
    }

    @Override
    @CacheEvict(value = FlexibleCaffeineCacheManager.FlexibleCacheConfig.JWT_TOKEN, key = "#token")
    public void removeCurrentUser(String token) {
        // 调用缓存移除当前用户
    }
}
```
缓存服务的配置文件
```java
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
    }
}
```
```yaml
  register:
    token-cache-class: com.yousuf.platform.cache.CaffeineAuthTokenCache
```
当我们需要用`redis`来替换的时候,重新写一个`RedisAuthTokenCache`,然后修改配置文件就可以把缓存替换为`Redis`
