package com.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 博客系统启动入口。
 * <p>
 * {@code @SpringBootApplication} 聚合了 {@code @Configuration}、{@code @EnableAutoConfiguration}、
 * {@code @ComponentScan}，默认扫描当前包及子包下的所有组件。
 * {@code @EnableAsync} 启用异步方法支持，用于操作日志异步写入。
 */
@EnableAsync
@SpringBootApplication
public class BlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}
