package com.yousuf.platform.common.enums;

import com.yousuf.platform.common.core.BaseEnum;

/**
 * <p> ClassName: UserAccountCategoryEnum
 * <p> Description: 登录账号类型枚举
 *
 * @author zhangshuai 2019/11/25
 */
public enum  UserAccountCategoryEnum implements BaseEnum<Integer> {
    /**登录类别*/
    LOGIN_USER(0, "用户名登录"),
    EMAIL_USER(1, "邮箱登录"),
    PHONE_USER(2, "电话号码登录"),
    WE_CHAT_USER(3, "微信登录"),
    ;

    private Integer code;
    private String text;

    UserAccountCategoryEnum(Integer code, String text) {
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
