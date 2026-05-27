package com.blog.common;

import cn.dev33.satoken.exception.NotLoginException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常兜底处理，将异常转为统一的 {@link Result} 响应。
 * <p>
 * 重点：最底部的 {@code handleException} 兜底所有未捕获异常，生产环境应避免暴露
 * {@code e.getMessage()} 给前端（可能泄露内部信息），此处为开发阶段保留。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public Result<?> handleNotLogin(NotLoginException e) {
        return Result.fail(401, "请先登录");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgument(IllegalArgumentException e) {
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        return Result.fail(500, e.getMessage());
    }
}
