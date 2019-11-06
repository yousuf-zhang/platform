package com.yousuf.platform.exception;

import com.yousuf.platform.common.core.RestCode;

/**
 * <p> Title: UtilsException
 * <p> Description: 工具类异常
 *
 * @author yousuf zhang 2019/11/5
 */
public class UtilsException extends BaseException {

    private static final long serialVersionUID = 9018455246960125159L;

    public UtilsException(RestCode code, String message) {
        super(code, message);
    }
}
