package com.yousuf.platform.exception.code;

import com.yousuf.platform.common.core.RestCode;
import lombok.Getter;

/**
 * <p> Title: AuthCode
 * <p> Description: 鉴权和认证返回码
 *
 * @author yousuf zhang 2019/11/8
 */
@Getter
public enum AuthCode implements RestCode {
    /**鉴权认证码*/
    NOT_LOGIN(1000, "token失效"),
    ;

    private Integer code;
    private String text;

    AuthCode(Integer code, String text) {
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
