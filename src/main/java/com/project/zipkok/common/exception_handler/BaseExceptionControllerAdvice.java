package com.project.zipkok.common.exception_handler;

import jakarta.validation.ConstraintViolationException;
import com.project.zipkok.common.exception.BadRequestException;
import com.project.zipkok.common.exception.InternalServerErrorException;
import com.project.zipkok.common.response.BaseExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@RestControllerAdvice
public class BaseExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class, NoHandlerFoundException.class, TypeMismatchException.class})
    public BaseExceptionResponse handle_BadRequest(Exception e) {
        log.error("[handle_BadRequest]", e);
        return new BaseExceptionResponse(URL_NOT_FOUND);
    }

    // 위와 동일 (return ResponseEntity<>)
    /*
    @ExceptionHandler({BadRequestException.class, NoHandlerFoundException.class, TypeMismatchException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<BaseErrorResponse> handle_BadRequest(BadRequestException e) {
        log.error("[handle_BadRequest]", e);
        return ResponseEntity.badRequest().body(new BaseErrorResponse(e.getExceptionStatus()));
    }
     */

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public BaseExceptionResponse handle_HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("[handle_HttpRequestMethodNotSupportedException]", e);
        return new BaseExceptionResponse(METHOD_NOT_ALLOWED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public BaseExceptionResponse handle_ConstraintViolationException(ConstraintViolationException e) {
        log.error("[handle_ConstraintViolationException]", e);
        return new BaseExceptionResponse(BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerErrorException.class)
    public BaseExceptionResponse handle_InternalServerError(InternalServerErrorException e) {
        log.error("[handle_InternalServerError]", e);
        return new BaseExceptionResponse(e.getExceptionStatus());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public BaseExceptionResponse handle_RuntimeException(Exception e) {
        log.error("[handle_RuntimeException]", e);
        return new BaseExceptionResponse(SERVER_ERROR);
    }

}
