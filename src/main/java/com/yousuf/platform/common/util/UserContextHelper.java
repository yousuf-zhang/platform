package com.yousuf.platform.common.util;

import com.yousuf.platform.common.core.ApplicationContextHelper;
import com.yousuf.platform.common.infrastructure.AuthToken;
import com.yousuf.platform.common.infrastructure.AuthTokenCache;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * ClassName: UserContextHelper
 * <p> Description: 用户上下文辅助类
 *
 * @author zhangshuai 2019/11/8
 */
@Component
@DependsOn("applicationContextHelper")
public class UserContextHelper {
    private static AuthTokenCache authTokenCache;
    @PostConstruct
    private void init() {
        authTokenCache = (AuthTokenCache) ApplicationContextHelper.getBean(AuthTokenCache.AUTH_TOKEN_CACHE_NAME);
    }

    /**
     * <p> Title: getCurrentUser
     * <p> Description: 从缓存中获取currentUser
     *
     * @return com.yousuf.platform.common.infrastructure.AuthToken
     *
     * @author zhangshuai 2019/11/8
     *
     */
    public static AuthToken getCurrentUser() {
        return authTokenCache.findCurrentUserByCache(JwtTokenHelper.findTokenByRequest());
    }

    /**
     * <p> Title: setCurrentUser
     * <p> Description: 把登录用户放入缓存中
     *
     * @param token token
     * @param authToken 当前用户
     *
     * @author zhangshuai 2019/11/8
     *
     */
    public static void setCurrentUser(String token, AuthToken authToken) {
        authTokenCache.cacheToken(token, authToken);
    }

    /**
     * <p> Title: removeCurrentUser
     * <p> Description: 把当前用户移除缓存
     *
     * @author zhangshuai 2019/11/8
     *
     */
    public static void removeCurrentUser() {
        authTokenCache.removeCurrentUser(JwtTokenHelper.findTokenByRequest());
    }
}
