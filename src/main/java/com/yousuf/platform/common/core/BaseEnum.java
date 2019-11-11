package com.yousuf.platform.common.core;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * <p> Title: BaseEnum
 * <p> Description: 基础枚举类接口
 *
 * @author yousuf zhang 2019/11/4
 */
public interface BaseEnum<V> {
    /**
     * <p> Title: getCode
     * <p> Description: 获取code值
     *
     * @return V
     *
     * @author yousuf zhang 2019/11/4
     **/
    V getCode();

    /**
     * <p> Title: getText
     * <p> Description: 获取文本信息
     *
     * @return java.lang.String
     *
     * @author yousuf zhang 2019/11/4
     **/
    String getText();

    /**
     * <p> Title: find
     * <p> Description: 从指定的枚举类中查找想要的枚举,并返回一个{@link Optional},
     * 如果未找到,则返回一个{@link Optional#empty()}
     *
     * @param type 实现了{@link BaseEnum}的枚举类
     * @param predicate 判读逻辑
     * @return java.util.Optional<T> 返回枚举类型
     *
     * @author yousuf zhang 2019/11/4
     **/
    static <T extends Enum & BaseEnum> Optional<T> find(Class<T> type, Predicate<T> predicate) {
        if (type.isEnum()) {
            return Arrays.stream(type.getEnumConstants())
                    .filter(predicate).findFirst();
        }
        return Optional.empty();
    }

    /**
     * <p> Title: findOptionalByCode
     * <p> Description: 根据枚举的{@link BaseEnum#getCode()}来查找.
     *
     * @see this#find(Class, Predicate)
     * @param type 实现了{@link BaseEnum}的枚举类
     * @param code 枚举code
     * @return java.util.Optional<T>
     *
     * @author yousuf zhang 2019/11/4
     **/
    static <T extends Enum & BaseEnum<?>> Optional<T> findOptionalByCode(Class<T> type, Object code) {
        return find(type, e -> e.getCode() == code || e.getCode().equals(code)
                || String.valueOf(e.getCode()).equalsIgnoreCase(String.valueOf(code)));
    }

    /**
     * <p> Title: findOptionalByText
     * <p> Description: 根据枚举的{@link BaseEnum#getText()}来查找.
     *
     * @param type 实现了{@link BaseEnum}的枚举类
     * @param text 枚举 text
     * @return java.util.Optional<T>
     *
     * @author yousuf zhang 2019/11/5
     **/
    static <T extends Enum & BaseEnum> Optional<T> findOptionalByText(Class<T> type, String text) {
        return find(type, e -> e.getText().equalsIgnoreCase(text));
    }

}
