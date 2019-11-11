package com.yousuf.platform.common.util;

import com.yousuf.platform.common.core.ApplicationContextHelper;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

/**
 * ClassName: ValidatorHelper
 * <p> Description: 手动调用参数校验
 *
 * @author zhangshuai 2019/11/6
 */
@Component
@DependsOn("applicationContextHelper")
public class ValidatorHelper {
    private static Validator validator;

    @PostConstruct
    public void init() {
        validator = ApplicationContextHelper.getBean(Validator.class);
    }

    /**
     * <p> Title: validate
     * <p> Description: 参数校验
     *
     * @param object 校验参数
     * @param groups 分组
     *
     * @author zhangshuai 2019/11/6
     *
     */
    public static  <T> void validate(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> constraintViolationSet = validator.validate(object, groups);

        if (!constraintViolationSet.isEmpty()) {
            throw new ConstraintViolationException(constraintViolationSet);
        }
    }

    /**
     * <p> Title: validateProperty
     * <p> Description: 对某个属性值进行校验
     *
     * @param object 类
     * @param propertyName 属性名称
     * @param groups 分组
     *
     * @author zhangshuai 2019/11/6
     *
     */
    public static <T> void validateProperty(T object, String propertyName, Class<?>... groups) {
        Set<ConstraintViolation<T>> constraintViolationSet = validator.validateProperty(object, propertyName, groups);
        if (!constraintViolationSet.isEmpty()) {
            throw new ConstraintViolationException(constraintViolationSet);
        }
    }

    /**
     * <p> Title: validateValue
     * <p> Description: 对某个属性，是否是某个值进行校验
     *
     * @param beanType 类型
     * @param propertyName 属性名称
     * @param value 属性值
     * @param groups 分组
     *
     * @author zhangshuai 2019/11/6
     *
     */
    public static <T> void validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
        Set<ConstraintViolation<T>> constraintViolationSet = validator.validateValue(beanType, propertyName, value, groups);
        if (!constraintViolationSet.isEmpty()) {
            throw new ConstraintViolationException(constraintViolationSet);
        }
    }
}
