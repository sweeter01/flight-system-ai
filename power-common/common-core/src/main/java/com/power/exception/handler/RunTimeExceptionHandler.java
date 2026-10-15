package com.power.exception.handler;

import com.power.constant.BusinessEnum;
import com.power.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(2)
@Slf4j
public class RunTimeExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result runtimeException(RuntimeException e) {
        log.error("系统异常：{} - 类型: {}", e.getMessage(), e.getClass().getName());
        log.error("异常堆栈：", e);
        return Result.fail(BusinessEnum.SERVER_INNER_ERROR);
    }
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.fail(BusinessEnum.SERVER_INNER_ERROR);
    }
}
//19823298217