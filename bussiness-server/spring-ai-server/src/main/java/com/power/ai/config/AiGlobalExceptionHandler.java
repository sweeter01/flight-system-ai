package com.power.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class AiGlobalExceptionHandler {

    @ExceptionHandler(NonTransientAiException.class)
    public String handleAiException(NonTransientAiException e) {
        log.error("AI服务异常：{}", e.getMessage());
        return "AI服务暂时不可用，请稍后重试。错误信息：" + e.getMessage();
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        log.error("系统异常：", e);
        return "操作失败：" + e.getMessage();
    }
}
