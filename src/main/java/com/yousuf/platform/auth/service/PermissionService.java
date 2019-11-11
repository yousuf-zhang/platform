package com.yousuf.platform.auth.service;

import com.yousuf.platform.vo.UserInfoDTO;

/**
 * <p> Title: PermissionService
 * <p> Description: 权限校验service
 *
 * @author yousuf zhang 2019/11/11
 */
public interface PermissionService {
    /**
     * <p> Title: hasPermission
     * <p> Description: 是否具有权限
     *
     * @param requestUri 请求url
     * @param userInfo 用户信息
     * @return boolean
     *
     * @author yousuf zhang 2019/11/11
     **/
    boolean hasPermission(String requestUri, UserInfoDTO userInfo);
}
