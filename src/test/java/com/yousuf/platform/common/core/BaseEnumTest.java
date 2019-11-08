package com.yousuf.platform.common.core;

import com.yousuf.platform.common.infrastructure.AuthTokenCache;
import com.yousuf.platform.vo.UserInfoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * <p> Title: BaseEnumTest
 * <p> Description: 测试枚举类型
 *
 * @author yousuf zhang 2019/11/5
 */
@SpringBootTest
public class BaseEnumTest {
    @Autowired
    @Test
    public void get_success_by_globalCode() {
        AuthTokenCache authTokenCache = (AuthTokenCache) ApplicationContextHelper.getBean("authTokenCache");
        authTokenCache.cacheToken("adfasdf", new UserInfoDTO("test", "test"));
        System.out.println(authTokenCache);
        System.out.println(authTokenCache.findCurrentUserByCache("adfasdf"));
    }
}
