package com.yousuf.platform.controller;

import com.alibaba.fastjson.JSON;
import com.yousuf.platform.common.core.RestResponse;
import com.yousuf.platform.common.exception.code.GlobalCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * <p> Title: TestControllerTest
 * <p> Description: 测试类
 *
 * @author yousuf zhang 2019/11/5
 */
@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTest {
    @Autowired
    private MockMvc mockMvc;


    @Test
    public void test_exception_by_controller() {
        try {
            String json = JSON.toJSONString(RestResponse.error(GlobalCode.UTILS_ERROR));
            System.out.println(json);
            mockMvc.perform(MockMvcRequestBuilders.get("/test/exception"))
                    .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                    .andExpect(MockMvcResultMatchers.content().json(json))
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn();
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

}