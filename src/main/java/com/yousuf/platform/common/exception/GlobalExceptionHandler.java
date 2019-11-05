package com.yousuf.platform.common.exception;

import com.yousuf.platform.common.core.RestCode;
import com.yousuf.platform.common.core.RestResponse;
import com.yousuf.platform.common.exception.code.GlobalCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * <p> Title: GlobalExceptionHandler
 * <p> Description: 全局异常处理
 *
 * @author yousuf zhang 2019/11/5
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public RestResponse<Object> handler(ServletRequest request, Throwable throwable, HttpServletResponse response) {
        // 设置状态码为500
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        if (Objects.isNull(throwable)) {
            log.error("没有获取到异常信息");
            return RestResponse.error(GlobalCode.UNKNOWN);
        }

        // 业务异常
        if (throwable instanceof BaseException) {
            RestCode code = ((BaseException) throwable).getCode();
            log.warn("自定义异常 异常码 -> {}, 异常信息 -> {}", code.getCode(), code.getText());
            return RestResponse.error(code);
        }

        log.error("未知异常", throwable);
        return RestResponse.error(GlobalCode.UNKNOWN);
    }
}