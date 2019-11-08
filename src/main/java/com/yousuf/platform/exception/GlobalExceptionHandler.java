package com.yousuf.platform.exception;

import com.google.common.collect.Sets;
import com.yousuf.platform.common.core.RestCode;
import com.yousuf.platform.common.core.RestResponse;
import com.yousuf.platform.common.core.ValidError;
import com.yousuf.platform.exception.code.GlobalCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.Set;

/**
 * <p> Title: GlobalExceptionHandler
 * <p> Description: 全局异常处理
 *
 * @author yousuf zhang 2019/11/5
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Title: handleBindException
     * Description: 通过注解校验异常处理
     *
     * @param ex 异常
     * @param response 返回信息
     *
     * @return com.yousuf.platform.common.core.RestResponse<java.lang.Object>
     *
     * @author zhangshuai 2019/11/6
     *
     */
    @Order(1)
    @ResponseBody
    @ExceptionHandler(value= BindException.class)
    public RestResponse<Object> handleBindException(BindException ex, HttpServletResponse response){
        // 设置状态码为500
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        BindingResult result = ex.getBindingResult();
        Set<ValidError> errors = Sets.newHashSet();
        result.getFieldErrors().forEach(fieldError -> {
            log.warn("参数格式不正确 {} -> {}", fieldError.getField(), fieldError.getDefaultMessage());
            errors.add(new ValidError(fieldError.getField(), fieldError.getDefaultMessage()));
        });
        return RestResponse.error(GlobalCode.PARAMS_ERROR, errors);
    }

    /**
     * Title: handleConstraintViolationException
     * Description: 手动抛出异常处理
     *
     * @param ex 异常
     * @param response 返回
     *
     * @return com.yousuf.platform.common.core.RestResponse<java.lang.Object>
     *
     * @author zhangshuai 2019/11/6
     *
     */
    @Order(1)
    @ResponseBody
    @ExceptionHandler(value = ConstraintViolationException.class)
    public RestResponse<Object> handleConstraintViolationException(ConstraintViolationException ex,
                                                                   HttpServletResponse response) {
        // 设置状态码为500
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        Set<ValidError> errors = Sets.newHashSet();
        ex.getConstraintViolations().forEach(constraintViolation -> {
            ValidError error = new ValidError(constraintViolation.getPropertyPath().toString(),constraintViolation.getMessage());
            errors.add(error);
            log.warn("参数格式不正确 {} -> {}", error.getFiled(), error.getMessage());
        });
        return RestResponse.error(GlobalCode.PARAMS_ERROR, errors);
    }
    /**
     * Title: handleMessageNotReadableException
     * Description: 请求格式错误封装
     *
     * @param ex 错误
     * @param response 返回
     *
     * @return com.yousuf.platform.common.core.RestResponse<java.lang.Object>
     * @author zhangshuai 2019/11/8
     *
     */
    @Order(1)
    @ResponseBody
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public RestResponse<Object> handleMessageNotReadableException(HttpMessageNotReadableException ex,
                                                                  HttpServletResponse response) {
        // 设置状态码为500
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        log.error("请求格式错误", ex);
        return RestResponse.error(GlobalCode.DATA_SCHEMA_ERROR);

    }

    /**
     * Title: handler
     * Description: 统一异常处理
     *
     * @param request 请求
     * @param throwable 错误
     * @param response 返回
     *
     * @return com.yousuf.platform.common.core.RestResponse<java.lang.Object>
     *
     * @author zhangshuai 2019/11/6
     *
     */
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
