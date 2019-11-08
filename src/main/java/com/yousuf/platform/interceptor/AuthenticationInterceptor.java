package com.yousuf.platform.interceptor;

import com.alibaba.fastjson.JSON;
import com.yousuf.platform.common.core.RestResponse;
import com.yousuf.platform.common.infrastructure.AuthToken;
import com.yousuf.platform.common.util.JwtTokenHelper;
import com.yousuf.platform.common.util.UserContextHelper;
import com.yousuf.platform.common.util.WebUtils;
import com.yousuf.platform.exception.code.AuthCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        // 跨域时两段验证，当请求方法为OPTIONS直接跳过，不用判断是否登录
        if (Objects.equals(METHOD_OPTIONS, request.getMethod())) {
            return true;
        }
        String token = JwtTokenHelper.findTokenByRequest();
        if (StringUtils.isBlank(token) || JwtTokenHelper.verify(token)) {
            authFailure(response);
            return false;
        }
        Pair<String, String> pair = JwtTokenHelper.parseToken(token);
        AuthToken authToken = UserContextHelper.getCurrentUser();

        boolean isVerify = !Objects.isNull(authToken)
                && !Objects.isNull(pair)
                && Objects.equals(pair.getLeft(), authToken.getUserId())
                && Objects.equals(pair.getRight(), authToken.getUsername())
                && Objects.equals(authToken.getLoginIp(), WebUtils.getIpAddress());
        if (!isVerify) {
            authFailure(response);
            return false;
        }
        return true;
    }

    private void authFailure(HttpServletResponse response) throws IOException {
        log.warn("登录token验证失败 token -> {}, ip -> {}, url -> {}", JwtTokenHelper.findTokenByRequest(),
                WebUtils.getIpAddress(), WebUtils.getRequest().getRequestURI());
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(JSON.toJSONString(RestResponse.error(AuthCode.NOT_LOGIN)));
    }
}
