package com.yousuf.platform.interceptor;

import com.alibaba.fastjson.JSON;
import com.yousuf.platform.auth.service.PermissionService;
import com.yousuf.platform.common.core.RestResponse;
import com.yousuf.platform.common.infrastructure.AuthToken;
import com.yousuf.platform.common.util.UserContextHelper;
import com.yousuf.platform.exception.code.AuthCode;
import com.yousuf.platform.vo.UserInfoDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p> Title: AuthorizationInterceptor
 * <p> Description: 鉴权拦截器
 *
 * @author yousuf zhang 2019/11/11
 */
@Component
@AllArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {
    private static final String METHOD_OPTIONS = "OPTIONS";
    private final PermissionService permissionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (METHOD_OPTIONS.equals(request.getMethod())) {
            return true;
        }

        AuthToken authToken = UserContextHelper.getCurrentUser();
        if (authToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.getWriter().write(JSON.toJSONString(RestResponse.error(AuthCode.NOT_LOGIN)));
            return false;
        }

        UserInfoDTO userInfo = (UserInfoDTO) authToken;
        if (!permissionService.hasPermission(request.getRequestURI(), userInfo)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.getWriter().write(JSON.toJSONString(RestResponse.error(AuthCode.FORBIDDEN)));
            return false;
        }
        return true;
    }
}
