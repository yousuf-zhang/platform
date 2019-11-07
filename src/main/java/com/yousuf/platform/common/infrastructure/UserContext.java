package com.yousuf.platform.common.infrastructure;

/**
 * <p> Title: UserContext
 * <p> Description: 当前用户上下文
 *
 * @author yousuf zhang 2019/11/8
 */
public interface UserContext {
    /**
     * Title: getCurrentUser
     * Description: 获取当前登录用户
     *
     * @return com.yousuf.platform.common.infrastructure.AuthToken
     *
     * @author yousuf zhang 2019/11/8
     **/
    AuthToken getCurrentUser();
    /**
     * Title: setCurrentUser
     * Description: 设置当前登录用户
     *
     * @param token token
     * @param authToken 当前登录用户
     *
     * @author yousuf zhang 2019/11/8
     **/
    void setCurrentUser(String token, AuthToken authToken);
    /**
     * Title: removeCurrentUser
     * Description: 移除当前登录用户
     *
     * @author yousuf zhang 2019/11/8
     **/
    void removeCurrentUser();
}
