package com.yousuf.platform.common.util;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ClassName: JwtTokenHelperTest
 * <p> Description: jwt测试
 *
 * @author zhangshuai 2019/11/7
 */
@SpringBootTest
class JwtTokenHelperTest {

    @Test
    void generateToken() {
        String token = JwtTokenHelper.generateToken("test", "username");
        Pair<String, String > pair = JwtTokenHelper.parseToken(token);
        Assertions.assertNotNull(pair);
        Assertions.assertEquals(pair.getLeft(), "test");
        Assertions.assertEquals(pair.getRight(), "username");

    }
}
