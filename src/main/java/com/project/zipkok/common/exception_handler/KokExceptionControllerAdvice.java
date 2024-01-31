package com.project.zipkok.common.exception_handler;

import com.project.zipkok.common.exception.KokException;
import com.project.zipkok.common.exception.RealEstateException;
import com.project.zipkok.common.response.BaseExceptionResponse;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class KokExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(KokException.class)
    public BaseExceptionResponse handle_KokException(KokException e) {
        log.error("[handle_KokException]", e);
        return new BaseExceptionResponse(e.getResponseStatus());
    }
}
