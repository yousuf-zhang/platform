package com.yousuf.platform.auth.controller;

import com.yousuf.platform.common.core.RestResponse;
import com.yousuf.platform.common.util.JwtTokenHelper;
import com.yousuf.platform.common.util.UserContextHelper;
import com.yousuf.platform.vo.UserInfoDTO;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: AuthController
 * Description: 用户登录认证
 *
 * @author zhangshuai 2019/11/8
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * Title: login
     * Description: 登录
     *
     * @param user 用户
     *
     * @return com.yousuf.platform.common.core.RestResponse<java.lang.String>
     *
     * @author zhangshuai 2019/11/8
     *
     */
    @PostMapping("/login")
    public RestResponse<String> login(@RequestBody UserInfoDTO user) {
        String token = JwtTokenHelper.generateToken(user.getUserId(), user.getUsername());
        UserContextHelper.setCurrentUser(token, user);
        return RestResponse.success(token);
    }

    /**
     * Title: logout
     * Description: 登出
     *
     * @return com.yousuf.platform.common.core.RestResponse<java.lang.String>
     *
     * @author zhangshuai 2019/11/8
     *
     */
    @GetMapping("/logout")
    public RestResponse<Void> logout() {
        UserContextHelper.removeCurrentUser();
        return RestResponse.success();
    }
}
