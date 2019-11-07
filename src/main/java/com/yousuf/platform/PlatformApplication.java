package com.yousuf.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * <p> Title: PlatformApplication
 * <p> Description: 项目启动类
 *
 * @author yousuf zhang 2019/11/5
 */
@EnableCaching
@SpringBootApplication
public class PlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformApplication.class, args);
    }

}
