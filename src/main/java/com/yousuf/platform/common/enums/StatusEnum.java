package com.yousuf.platform.common.enums;

import com.yousuf.platform.common.core.BaseEnum;

/**
 * <p> ClassName: StatusEnum
 * <p> Description: 状态枚举
 *
 * @author zhangshuai 2019/11/22
 */
public enum  StatusEnum implements BaseEnum<Integer> {
    /**状态*/
    DISABLE(0, "停用"),
    NORMAL(1, "正常"),
    ;
    private Integer code;
    private String text;

    StatusEnum(Integer code, String text) {
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
