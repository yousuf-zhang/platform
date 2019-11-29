package com.yousuf.platform.controller;

import com.yousuf.platform.common.core.RestResponse;
import com.yousuf.platform.common.util.ValidatorHelper;
import com.yousuf.platform.exception.UtilsException;
import com.yousuf.platform.exception.code.GlobalCode;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @PostMapping("/test/valid")
    public RestResponse<DemoDTO> getDemo(@Valid DemoDTO demoDTO) {
        return RestResponse.success(demoDTO);
    }

    @PostMapping("/test/valid1")
    public RestResponse<DemoDTO> getDemo1(DemoDTO demoDTO) {
        // 手动校验
        ValidatorHelper.validate(demoDTO);
        return RestResponse.success(demoDTO);
    }

}
