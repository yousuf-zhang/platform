package com.yousuf.platform.common.util;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ClassName: JwtTokenHelperTest
 * Description: jwt测试
 *
 * @author zhangshuai 2019/11/7
 */
@SpringBootTest
class JwtTokenHelperTest {

    @Test
    void generateToken() {
        String token = JwtTokenHelper.generateToken("test", "asdfasdf");
        System.out.println(token);
        Pair<String, String > pair = JwtTokenHelper.parseToken(token);
        System.out.println(pair.getLeft());
        System.out.println(pair.getRight());

    }
}
