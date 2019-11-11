package com.yousuf.platform.auth.controller;

import com.yousuf.platform.auth.service.AuthService;
import com.yousuf.platform.common.core.RestResponse;
import com.yousuf.platform.common.util.JwtTokenHelper;
import com.yousuf.platform.common.util.UserContextHelper;
import com.yousuf.platform.exception.code.AuthCode;
import com.yousuf.platform.vo.UserInfoDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

/**
 * <p>ClassName: AuthController
 * <p>Description: 用户登录认证
 *
 * @author zhangshuai 2019/11/8
 */
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    /**
     * <p>Title: login
     * <p>Description: 登录
     *
     * @param user 用户
     *
     * @return com.yousuf.platform.common.core.RestResponse<java.lang.String>
     *
     * @author zhangshuai 2019/11/8
     *
     */
    @PostMapping("/login")
    public RestResponse<String> login(@RequestBody @Valid UserInfoDTO user) {
        UserInfoDTO currentUser = authService.login(user);
        if (Objects.isNull(currentUser)) {
            return RestResponse.error(AuthCode.USER_PASSWORD_ERROR);
        }
        // 生成token
        String token = JwtTokenHelper.generateToken(currentUser.getUserId(), currentUser.getUsername());
        UserContextHelper.setCurrentUser(token, currentUser);
        return RestResponse.success(token);
    }

    /**
     * <p> Title: findUserInfo
     * <p> Description: 获取当前用户信息
     *
     * @return com.yousuf.platform.common.core.RestResponse<com.yousuf.platform.vo.UserInfoDTO>
     * @author yousuf zhang 2019/11/8
     **/
    @GetMapping("/info")
    public RestResponse<UserInfoDTO> findUserInfo() {
        UserInfoDTO userInfo = (UserInfoDTO) UserContextHelper.getCurrentUser();
        return RestResponse.success(userInfo);
    }
    /**
     * <p>Title: logout
     * <p>Description: 退出
     *
     * @return com.yousuf.platform.common.core.RestResponse<java.lang.Void>
     *
     * @author yousuf zhang 2019/11/8
     **/
    @GetMapping("/logout")
    public RestResponse<Void> logout() {
        UserContextHelper.removeCurrentUser();
        return RestResponse.success();
    }
}
