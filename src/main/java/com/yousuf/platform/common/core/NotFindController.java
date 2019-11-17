package com.yousuf.platform.common.core;

import com.yousuf.platform.exception.code.GlobalCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * ClassName: NotFindController
 * <p> Description: 404 错误处理
 *
 * @author zhangshuai 2019/11/8
 */
@Slf4j
@RestController
@AllArgsConstructor
public class NotFindController {
    private final BasicErrorController basicErrorController;
    @RequestMapping(value = "/error", method = {RequestMethod.GET, RequestMethod.POST})
    public RestResponse<ResponseEntity> error(HttpServletRequest request, HttpServletResponse response) {
        //获取异常返回
        ResponseEntity<Map<String, Object>> errorDetail = basicErrorController.error(request);
        response.setStatus(errorDetail.getStatusCode().value());
        log.warn("页面不存在， errorDetail -> {}", errorDetail);
        return RestResponse.error(GlobalCode.NOT_FOUND);
    }
}
