package com.yousuf.platform.common.core;

import com.yousuf.platform.common.infrastructure.AuthTokenCache;
import com.yousuf.platform.vo.UserInfoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * <p> Title: BaseEnumTest
 * <p> Description: 测试枚举类型
 *
 * @author yousuf zhang 2019/11/5
 */
@SpringBootTest
public class BaseEnumTest {
    @Test
    public void get_success_by_globalCode() {
        AuthTokenCache authTokenCache = (AuthTokenCache) ApplicationContextHelper.getBean("authTokenCache");
        authTokenCache.cacheToken("adfasdf",
                UserInfoDTO.builder().userId("test").username("aaaa").build());
        System.out.println(authTokenCache);
        System.out.println(authTokenCache.findCurrentUserByCache("adfasdf"));
    }
}
