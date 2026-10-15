package com.power.Aspect;


import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.sql.Date;

@Component
@Aspect
@Slf4j
public class SysLogAspect {


    @Autowired
    private ObjectMapper objectMapper;

    public static final String point_cut = "execution(* com.power.controller.*.*(..))";

    @Around(point_cut)
    public Object logAround(ProceedingJoinPoint joinPoint) throws JsonProcessingException {

        String requestURI = "unknown";
        String remoteHost = "unknown";

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            requestURI = request.getRequestURI();
            remoteHost = request.getRemoteHost();
        }

        Object[] args = joinPoint.getArgs();
        MethodSignature signature =(MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().toString();

        ApiOperation apiOperation = signature.getMethod().getDeclaredAnnotation(ApiOperation.class);
        String operation = "";
        if (ObjectUtil.isNotNull(apiOperation)) {
            operation = apiOperation.value();
        }

        String finalArgs = "";
        if (ObjectUtil.isNotNull(args) && args.length != 0 && args[0] instanceof MultipartFile) {
            finalArgs = "file";
        } else {
           finalArgs =objectMapper.writeValueAsString(args);
        }

        Object result =null;
        long startTime = System.currentTimeMillis();
        try {
            result = joinPoint.proceed(args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        long endTime = System.currentTimeMillis();

        long execTime = endTime - startTime;


        log.info("调用时间:{},请求接口路径:{},请求IP地址:{},方法名称:{},执行时长:{},方法描述:{}",
                new Date(System.currentTimeMillis()),
                requestURI,
                remoteHost,
                methodName,
                execTime,
                operation);

        return result;
    }
    }
