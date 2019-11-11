package com.yousuf.platform.common.infrastructure;

import java.io.Serializable;

/**
 * <p> Title: AuthToken
 * <p> Description: 收取token抽象
 *
 * @author yousuf zhang 2019/11/8
 */
public interface AuthToken extends Serializable {
    /**
     * <p> Title: getUsername
     * <p> Description: 登录名
     *
     * @return java.lang.String
     *
     * @author yousuf zhang 2019/11/8
     **/
    String getUsername();
    /**
     * <p> Title: getUserId
     * <p> Description: 用户ID
     *
     * @return java.lang.String
     *
     * @author yousuf zhang 2019/11/8
     **/
    String getUserId();
    /**
     * <p> Title: likeName
     * <p> Description: 用户显示名称
     *
     * @return java.lang.String
     *
     * @author yousuf zhang 2019/11/8
     **/
    default String getLikeName() {
        return null;
    }
    /**
     * <p> Title: loginIp
     * <p> Description: 登录IP
     *
     * @return java.lang.String
     *
     * @author yousuf zhang 2019/11/8
     **/
    default String getLoginIp() {
        return null;
    }

    /**
     * <p> Title: isFull
     * <p> Description: 是否全部权限
     *
     * @return java.lang.Integer
     *
     * @author yousuf zhang 2019/11/8
     **/
    default Integer isFull() {
        return 0;
    }
}
