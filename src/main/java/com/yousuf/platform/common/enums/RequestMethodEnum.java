package com.yousuf.platform.common.enums;

import com.yousuf.platform.common.core.BaseEnum;
import lombok.Getter;

/**
 * <p> Title: TrueOrFalseEnum
 * <p> Description: 请求方法枚举
 *
 * @author yousuf zhang 2019/11/11
 */
@Getter
public enum RequestMethodEnum implements BaseEnum<Integer> {
    /**请求方式枚举*/
    ALL(0, "ALL"),
    GET(1, "GET"),
    POST(2, "POST"),
    PUT(3, "PUT"),
    PATCH(4, "PATCH"),
    DELETE(5, "DELETE"),
    HEAD(6, "HEAD"),
    OPTIONS(7, "OPTIONS"),
    TRACE(8, "TRACE");

    private final Integer code;
    private final String text;

    RequestMethodEnum(Integer code, String text) {
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
