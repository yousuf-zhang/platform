## 授权认证
通过jwt做授权认证操作, `AuthorizationInterceptor`拦截器来判断token的合法性,包括是否登录,是否有权限访问某个资源,
由于跨域问题,所以 `OPTIONS`请求将直接跳过,不进行校验, `WebAppConfig`进行拦截器注册和跨域设置 代码如下:
```java
public class AuthorizationInterceptor implements HandlerInterceptor {
    private static final String METHOD_OPTIONS = "OPTIONS";
    private final PermissionService permissionService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 跨域时两段验证，当请求方法为OPTIONS直接跳过，不用判断是否登录
        if (Objects.equals(METHOD_OPTIONS, request.getMethod())) {
            return true;
        }
        String token = JwtTokenHelper.findTokenByRequest();
        if (StringUtils.isBlank(token) || JwtTokenHelper.verify(token)) {
            authFailure(response, HttpServletResponse.SC_UNAUTHORIZED, AuthCode.NOT_LOGIN);
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
            authFailure(response, HttpServletResponse.SC_UNAUTHORIZED, AuthCode.NOT_LOGIN);
            return false;
        }

        UserInfoDTO userInfo = (UserInfoDTO) authToken;
        if (!permissionService.hasPermission(request.getRequestURI(), userInfo)) {
            authFailure(response, HttpServletResponse.SC_FORBIDDEN, AuthCode.FORBIDDEN);
            return false;
        }
        return true;
    }

    private void authFailure(HttpServletResponse response, int responseCode, RestCode code) throws IOException {
        log.warn("token验证失败 token -> {}, ip -> {}, url -> {}", JwtTokenHelper.findTokenByRequest(),
                WebUtils.getIpAddress(), WebUtils.getRequest().getRequestURI());
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(responseCode);
        response.getWriter().write(JSON.toJSONString(RestResponse.error(code)));
    }
}
``` 
```java
@Configuration
@AllArgsConstructor
public class WebAppConfig implements WebMvcConfigurer {

    private final AuthorizationInterceptor authorizationInterceptor;
    private final ApplicationConfig.AuthConfig authConfig;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 认证拦截器
        registry.addInterceptor(authorizationInterceptor)
                .excludePathPatterns(authConfig.getAuthenticateExcludeUrl())
                .addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(CorsConfiguration.ALL)
                .allowedHeaders(CorsConfiguration.ALL)
                .allowedMethods(CorsConfiguration.ALL)
                .allowCredentials(true)
                .exposedHeaders("Header1", "Header2");
    }


}
```
```yaml
  auth:
    authenticate-exclude-url:
      - /auth/login
      - /error
```

token发放通过登录操作完成:
```java
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
```
