package com.yousuf.platform.common.exception.code;

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
