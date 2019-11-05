package com.yousuf.platform.common.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * <p> Title: ValidError
 * <p> Description: 校验错误信息
 *
 * @author yousuf zhang 2019/11/6
 */
@Data
@AllArgsConstructor
public class ValidError implements Serializable {
    private static final long serialVersionUID = 1492309047857495585L;
    private String filed;
    private String message;
}
