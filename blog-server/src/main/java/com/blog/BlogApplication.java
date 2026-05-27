package com.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 博客系统启动入口。
 * <p>
 * {@code @SpringBootApplication} 聚合了 {@code @Configuration}、{@code @EnableAutoConfiguration}、
 * {@code @ComponentScan}，默认扫描当前包及子包下的所有组件。
 */
@SpringBootApplication
public class BlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}
