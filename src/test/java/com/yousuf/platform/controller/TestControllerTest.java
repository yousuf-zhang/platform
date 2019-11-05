package com.yousuf.platform.controller;

import com.alibaba.fastjson.JSON;
import com.yousuf.platform.common.core.RestResponse;
import com.yousuf.platform.common.exception.code.GlobalCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    public void test_exception_by_controller() throws Exception {
        String json = JSON.toJSONString(RestResponse.error(GlobalCode.UTILS_ERROR));
        System.out.println(json);
        mockMvc.perform(get("/test/exception"))
                .andExpect(status().is5xxServerError())
                .andExpect(content().json(json))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void test_valid_title_null_return() throws Exception {
        ResultActions actions = mockMvc.perform(post("/test/valid"))
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("code").value(997))
            .andExpect(jsonPath("errors").exists());
        actions.andReturn().getResponse().setCharacterEncoding("utf-8");
        actions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void test_valid_desc_max() throws Exception {
        ResultActions actions = mockMvc.perform(post("/test/valid")
                .param("title", "test")
                .param("desc", "asdfasdfasdfasdfasdfasdfasdfadsfasdf"))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("code").value(997))
                .andExpect(jsonPath("errors").exists());
        actions.andReturn().getResponse().setCharacterEncoding("utf-8");
        actions.andDo(MockMvcResultHandlers.print());
    }
}