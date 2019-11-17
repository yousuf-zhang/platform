package com.yousuf.platform.cache;

import com.yousuf.platform.common.infrastructure.AuthTokenCache;
import com.yousuf.platform.vo.UserInfoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

/**
 * <p> Title: CaffeineAuthTokenCacheTest
 * <p> Description: cache测试
 *
 * @author yousuf zhang 2019/11/13
 */
@SpringBootTest
class CaffeineAuthTokenCacheTest {
    @Autowired
    private AuthTokenCache authTokenCache;
    @Autowired
    private CacheManager cacheManager;
    @Test
    public void test_cache() {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUsername("test,test");
        authTokenCache.cacheToken("test", userInfoDTO);
        System.out.println(authTokenCache.findCurrentUserByCache("test"));
    }
}