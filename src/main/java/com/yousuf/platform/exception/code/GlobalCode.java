package com.yousuf.platform.exception.code;

import com.yousuf.platform.common.core.RestCode;
import lombok.Getter;

/**
 * <p> Title: GlobalCode
 * <p> Description: 全局状态码
 *
 * @author yousuf zhang 2019/11/5
 */
@Getter
public enum  GlobalCode implements RestCode {
    /**全局状态码*/
    SUCCESS(0, "Success"),
    UNKNOWN(9999, "系统异常"),
    UTILS_ERROR(9998, "工具类不允许实例化"),
    PARAMS_ERROR(9997, "参数校验异常"),
    RSA_ERROR(9996, "加密参数错误"),
    DATA_SCHEMA_ERROR(9995, "数据格式错误"),
    NOT_FOUND(9994, "资源不存在"),
    ;

    private Integer code;
    private String text;

    GlobalCode(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getText() {
        return this.text;
    }
}
