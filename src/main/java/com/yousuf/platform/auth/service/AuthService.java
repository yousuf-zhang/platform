package com.yousuf.platform.auth.service;

import com.yousuf.platform.vo.UserInfoDTO;

/**
 * <p> Title: AuthService
 * <p> Description: 授权认证服务
 *
 * @author yousuf zhang 2019/11/8
 */
public interface AuthService {
    /**
     * <p> Title: login
     * <p> Description: 用户登录
     *
     * @param user 用户名
     * @return com.yousuf.platform.vo.UserInfoDTO
     *
     * @author yousuf zhang 2019/11/8
     **/
    UserInfoDTO login(UserInfoDTO user);
}
