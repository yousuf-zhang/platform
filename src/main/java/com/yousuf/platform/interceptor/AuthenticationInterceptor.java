package com.yousuf.platform.interceptor;

import com.alibaba.fastjson.JSON;
import com.yousuf.platform.common.core.RestResponse;
import com.yousuf.platform.common.util.JwtTokenHelper;
import com.yousuf.platform.exception.code.AuthCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * <p> Title: AuthenticationInterceptor
 * <p> Description: 身份验证过滤器 用于登录认证
 *
 * @author yousuf zhang 2019/11/7
 */
@Slf4j
@Component
@AllArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {
    private static final String METHOD_OPTIONS = "OPTIONS";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(JwtTokenHelper.getHeader());
        if (StringUtils.isNotBlank(token) && Objects.equals(METHOD_OPTIONS, request.getMethod())) {
            return true;
        }
        boolean isVerify = JwtTokenHelper.verifyToken(token);
        if (!isVerify) {
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JSON.toJSONString(RestResponse.error(AuthCode.NOT_LOGIN)));
            return false;
        }

        return true;
    }
}
