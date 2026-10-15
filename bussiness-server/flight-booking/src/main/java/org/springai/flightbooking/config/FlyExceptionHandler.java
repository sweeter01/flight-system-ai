package org.springai.flightbooking.config;

import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@Priority(1)
public class FlyExceptionHandler {

    @ExceptionHandler(CancelRefuseException.class)
    public String handleCancelRefuseException(CancelRefuseException e){
        log.warn("航班操作被拒绝：", e);
        return "航班取消失败：" + e.getMessage();
    }
}