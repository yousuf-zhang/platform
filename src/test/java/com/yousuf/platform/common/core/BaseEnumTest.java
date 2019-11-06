package com.yousuf.platform.common.core;

import com.yousuf.platform.exception.code.GlobalCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * <p> Title: BaseEnumTest
 * <p> Description: 测试枚举类型
 *
 * @author yousuf zhang 2019/11/5
 */
public class BaseEnumTest {
    @Test
    public void get_success_by_globalCode() {
        Assertions.assertEquals(GlobalCode.SUCCESS, BaseEnum.findOptionalByCode(GlobalCode.class, 0).get());
    }
}
