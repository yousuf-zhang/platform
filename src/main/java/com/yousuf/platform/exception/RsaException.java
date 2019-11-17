package com.yousuf.platform.exception;

import com.yousuf.platform.common.core.RestCode;

/**
 * ClassName: RsaException
 * <p> Description: Rsa加密异常
 *
 * @author zhangshuai 2019/11/7
 */
public class RsaException extends BaseException {
    public RsaException(RestCode code, String message) {
        super(code, message);
    }
}
