package com.yousuf.platform.common.core;

import com.yousuf.platform.exception.code.GlobalCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * <p> Title: RestResponseTest
 * <p> Description: RestResponse测试类
 *
 * @author yousuf zhang 2019/11/5
 */
public class RestResponseTest {
    @Test
    public void getSuccess() {
        RestResponse success = RestResponse.success();
        Assertions.assertEquals(success.getCode(), GlobalCode.SUCCESS.getCode());
    }
}
