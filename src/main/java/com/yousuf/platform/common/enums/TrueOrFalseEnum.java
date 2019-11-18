package com.yousuf.platform.common.enums;

import com.yousuf.platform.common.core.BaseEnum;
import lombok.Getter;

/**
 * <p> Title: TrueOrFalseEnum
 * <p> Description: 是否枚举转换
 *
 * @author yousuf zhang 2019/11/11
 */
@Getter
public enum TrueOrFalseEnum implements BaseEnum<Integer> {
    /**是否枚举转换*/
    TRUE(1, "是"),
    FAILURE(0, "否");
    private final Integer code;
    private final String text;

    TrueOrFalseEnum(Integer code, String text) {
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
