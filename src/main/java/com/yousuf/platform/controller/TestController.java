package com.yousuf.platform.controller;

import com.yousuf.platform.common.core.RestResponse;
import com.yousuf.platform.common.exception.UtilsException;
import com.yousuf.platform.common.exception.code.GlobalCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p> Title: TestController
 * <p> Description: 测试controller
 *
 * @author yousuf zhang 2019/11/5
 */
@RestController
public class TestController {

    @GetMapping("/test/exception")
    public RestResponse<String> getUtilsException() {
        throw new UtilsException(GlobalCode.UTILS_ERROR, "工具类异常");
        //return RestResponse.success();
    }

}
