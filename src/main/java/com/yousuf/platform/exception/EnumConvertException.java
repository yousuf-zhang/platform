package com.yousuf.platform.exception;

import com.yousuf.platform.common.core.RestCode;

/**
 * <p> Title: EnumConvertException
 * <p> Description: 枚举转换异常
 *
 * @author yousuf zhang 2019/11/17
 */
public class EnumConvertException extends BaseException {
    private static final long serialVersionUID = 2682370869120913117L;

    public EnumConvertException(RestCode code, String message) {
        super(code, message);
    }
}
