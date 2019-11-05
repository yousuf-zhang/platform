package com.yousuf.platform.common.core;

import com.yousuf.platform.common.exception.code.GlobalCode;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * <p> Title: RestResponse
 * <p> Description: 返回信息封装
 *
 * @author yousuf zhang 2019/11/4
 */
@Data
public class RestResponse<T> implements Serializable {
    private static final long serialVersionUID = 7545652265259278927L;
    private Integer code;
    private String message;
    private Set<ValidError> errors;
    private T result;

    public RestResponse(Integer code, String message, Set<ValidError> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    public RestResponse(Integer code, String message) {
       this(code, message, null);
    }

    public RestResponse() {
        this(GlobalCode.SUCCESS.getCode(), GlobalCode.SUCCESS.getText());
    }

    /**
     * Title: success
     * Description: 返回成功信息
     *
     * @return com.yousuf.platform.common.core.RestResponse<T>
     *
     * @author yousuf zhang 2019/11/4
     **/
    public static <T> RestResponse<T> success() {
        return new RestResponse<>();
    }

    /**
     * Title: success
     * Description: 返回结果信息
     *
     * @param result 结果
     * @return com.yousuf.platform.common.core.RestResponse<T>
     *
     * @author yousuf zhang 2019/11/4
     **/
    public static <T> RestResponse<T> success(T result) {
        RestResponse<T> response = new RestResponse<>();
        response.setResult(result);
        return response;
    }

    /**
     * Title: error
     * Description: 封装错误信息
     *
     * @param code 错误码
     * @return com.yousuf.platform.common.core.RestResponse<T>
     *
     * @author yousuf zhang 2019/11/5
     **/
    public static <T> RestResponse<T> error(RestCode code) {
        return new RestResponse<>(code.getCode(),code.getText());
    }

    /**
     * Title: error
     * Description: 返回带错误信息的信息
     *
     * @param code 错误码
     * @param errors 错误内容
     * @return com.yousuf.platform.common.core.RestResponse<T>
     *
     * @author yousuf zhang 2019/11/6
     **/
    public static <T> RestResponse<T> error(RestCode code, Set<ValidError> errors) {
        return new RestResponse<>(code.getCode(),code.getText(), errors);
    }



}
