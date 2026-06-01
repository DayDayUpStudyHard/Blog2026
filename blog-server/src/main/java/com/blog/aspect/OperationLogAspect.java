package com.blog.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.blog.annotation.OperationLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 操作日志切面 — 记录后台管理操作的请求信息。
 * <p>
 * 日志格式：{@code [操作类型] 操作描述 | 用户: xxx | IP: xxx | 参数: [...] | 耗时: xxxms}
 * 仅记录 INFO 级别，生产环境可接入 ELK/阿里云 SLS。
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Around("@annotation(com.blog.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog annotation = method.getAnnotation(OperationLog.class);

        // 获取操作人
        String username = getLoginUsername();

        // 获取客户端 IP
        String ip = getClientIp();

        // 截断参数避免日志过长
        String args = Arrays.toString(joinPoint.getArgs());
        if (args.length() > 200) {
            args = args.substring(0, 200) + "...";
        }

        // 执行原方法
        Object result = joinPoint.proceed();
        long elapsed = System.currentTimeMillis() - start;

        // 记录日志
        log.info("[{}] {} | 用户: {} | IP: {} | 参数: {} | 耗时: {}ms",
                annotation.type(), annotation.value(), username, ip, args, elapsed);

        return result;
    }

    private String getLoginUsername() {
        try {
            return (String) StpUtil.getLoginId();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String getClientIp() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return "unknown";
        HttpServletRequest request = attrs.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null ? ip.split(",")[0].trim() : "unknown";
    }
}
