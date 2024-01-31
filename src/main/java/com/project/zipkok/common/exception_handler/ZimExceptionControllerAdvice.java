package com.project.zipkok.common.exception_handler;

import com.project.zipkok.common.exception.zim.NoUserOrRealEstate;
import com.project.zipkok.common.exception.zim.ZimBadRequestException;
import com.project.zipkok.common.response.BaseExceptionResponse;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class ZimExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ZimBadRequestException.class)
    public BaseExceptionResponse handle_ZimBadRequestException(ZimBadRequestException e) {
        log.error("[handle_ZimBadRequestException]", e);
        return new BaseExceptionResponse(e.getExceptionStatus());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoUserOrRealEstate.class)
    public BaseExceptionResponse handle_NoUserOrRealEstate(NoUserOrRealEstate e) {
        log.error("[handle_NoUserOrRealEstate]", e);
        return new BaseExceptionResponse(e.getExceptionStatus());
    }

}
