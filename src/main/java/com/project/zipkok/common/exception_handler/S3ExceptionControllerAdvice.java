package com.project.zipkok.common.exception_handler;

import com.project.zipkok.common.exception.s3.FileUploadException;
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
public class S3ExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(FileUploadException.class)
    public BaseExceptionResponse handle_FileUploadException(FileUploadException e) {
        log.error("[handle_FileUploadException]", e);
        return new BaseExceptionResponse(e.getExceptionStatus());
    }
}
