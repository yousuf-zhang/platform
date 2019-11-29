package com.yousuf.platform.common.enums;

import com.yousuf.platform.common.core.BaseEnum;

/**
 * <p> ClassName: PermissionCategoryEnum
 * <p> Description: 资源类别
 *
 * @author zhangshuai 2019/11/29
 */
public enum  PermissionCategoryEnum implements BaseEnum<Integer> {
    /***/
    MENU(1, "菜单"),
    BUTTON(2, "按钮"),
    PERMISSION(3, "资源")
    ;

    private Integer code;
    private String text;

    PermissionCategoryEnum(Integer code, String text) {
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
