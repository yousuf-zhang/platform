package com.yousuf.platform.common.infrastructure;

/**
 * <p> Title: AuthTokenCache
 * <p> Description: token缓存方法
 *
 * @author yousuf zhang 2019/11/8
 */
public interface AuthTokenCache {
    /**
     * Title: findUserByCache
     * Description: 根据token值查询当前用户
     *
     * @param token token
     *
     * @return com.pay.admin.common.infrastructure.AuthToken
     *
     * @author zhangshuai 2019/11/8
     *
     */
    AuthToken findCurrentUserByCache(String token);
    /**
     * Title: cacheToken
     * Description: 缓存当前登录信息
     *
     * @param token token值
     * @param authToken 登录信息
     *
     * @return authToken
     *
     * @author zhangshuai 2019/11/8
     *
     */
    AuthToken cacheToken(String token, AuthToken authToken);

    /**
     * Title: removeCurrentUser
     * Description: 移除缓存
     *
     * @param token token值
     *
     * @author zhangshuai 2019/11/8
     *
     */
    void removeCurrentUser(String token);
}
