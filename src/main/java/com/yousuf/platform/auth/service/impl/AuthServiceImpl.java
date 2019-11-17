package com.yousuf.platform.auth.service.impl;

import com.yousuf.platform.auth.service.AuthService;
import com.yousuf.platform.common.util.WebUtils;
import com.yousuf.platform.vo.UserInfoDTO;
import org.springframework.stereotype.Service;

/**
 * <p> Title: AuthServiceImpl
 * <p> Description: 授权认证服务
 *
 * @author yousuf zhang 2019/11/8
 */
@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public UserInfoDTO login(UserInfoDTO user) {
        // xxx 这里先不处理等整合完jpa在进行处理
        return UserInfoDTO.builder().userId("aaaa")
                .username(user.getUsername())
                .loginIp(WebUtils.getIpAddress())
                .isFull(1).build();
    }
}
