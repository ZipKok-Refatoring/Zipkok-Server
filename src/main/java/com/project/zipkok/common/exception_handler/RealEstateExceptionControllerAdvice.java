package com.project.zipkok.common.exception_handler;

import com.project.zipkok.common.exception.RealEstateException;
import com.project.zipkok.common.response.BaseExceptionResponse;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.BAD_REQUEST;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class RealEstateExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(RealEstateException.class)
    public BaseExceptionResponse handle_RealEstateException(RealEstateException e) {
        log.error("[handle_RealEstateException]", e);
        return new BaseExceptionResponse(e.getResponseStatus(), e.getMessage());
    }
}
