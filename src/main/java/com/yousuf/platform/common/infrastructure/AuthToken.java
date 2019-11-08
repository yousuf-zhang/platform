package com.yousuf.platform.common.infrastructure;

import com.yousuf.platform.common.util.WebUtils;

/**
 * <p> Title: AuthToken
 * <p> Description: 收取token抽象
 *
 * @author yousuf zhang 2019/11/8
 */
public interface AuthToken {
    /**
     * Title: getUsername
     * Description: 登录名
     *
     * @return java.lang.String
     *
     * @author yousuf zhang 2019/11/8
     **/
    String getUsername();
    /**
     * Title: getUserId
     * Description: 用户ID
     *
     * @return java.lang.String
     *
     * @author yousuf zhang 2019/11/8
     **/
    String getUserId();
    /**
     * Title: likeName
     * Description: 用户显示名称
     *
     * @return java.lang.String
     *
     * @author yousuf zhang 2019/11/8
     **/
    default String getLikeName() {
        return null;
    }
    /**
     * Title: loginIp
     * Description: 登录IP
     *
     * @return java.lang.String
     *
     * @author yousuf zhang 2019/11/8
     **/
    default String getLoginIp() {
        return WebUtils.getIpAddress();
    }
}
