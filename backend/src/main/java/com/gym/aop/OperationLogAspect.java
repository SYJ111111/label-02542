package com.gym.aop;

import com.gym.annotation.Log;
import com.gym.entity.OperationLog;
import com.gym.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogMapper operationLogMapper;

    @Around("@annotation(com.gym.annotation.Log)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Log logAnnotation = signature.getMethod().getAnnotation(Log.class);

        Object result = joinPoint.proceed();

        try {
            OperationLog operationLog = new OperationLog();
            operationLog.setModule(logAnnotation.module());
            operationLog.setAction(logAnnotation.action());
            operationLog.setDetail(signature.getMethod().getName() + " | 参数: " + Arrays.toString(joinPoint.getArgs()));
            operationLog.setCreateTime(LocalDateTime.now());

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operationLog.setOperator((String) request.getAttribute("username"));
                operationLog.setIp(getClientIp(request));
            }

            operationLogMapper.insert(operationLog);
            log.info("[操作日志] 模块: {}, 操作: {}, 操作人: {}", logAnnotation.module(), logAnnotation.action(), operationLog.getOperator());
        } catch (Exception e) {
            log.error("记录操作日志失败: ", e);
        }

        return result;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
