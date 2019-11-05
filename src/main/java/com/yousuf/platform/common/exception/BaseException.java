package com.yousuf.platform.common.exception;

import com.yousuf.platform.common.core.RestCode;
import lombok.Getter;

/**
 * <p> Title: BaseException
 * <p> Description: 异常基础类
 *
 * @author yousuf zhang 2019/11/5
 */
@Getter
public abstract class BaseException extends RuntimeException {
    private static final long serialVersionUID = 6623312926437427049L;

    protected RestCode code;

    public BaseException() { }


    public BaseException(RestCode code, String message) {
        super(message);
        this.code = code;
    }
}
