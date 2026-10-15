package com.power.exception.handler;

import com.power.constant.BusinessEnum;
import com.power.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@Slf4j
@Order(1)
public class GlobalExceptionHandle {

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public Result<String> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return Result.fail(BusinessEnum.ACCESS_DENY_FAIL);
    }
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return Result.fail(BusinessEnum.OPERATION_FAIL, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void accessDeniedException(AccessDeniedException e) throws AccessDeniedException {
        // 抛出AccessDeniedException异常，走自定义AccessDeniedException异常处理
        throw e;
    }

}