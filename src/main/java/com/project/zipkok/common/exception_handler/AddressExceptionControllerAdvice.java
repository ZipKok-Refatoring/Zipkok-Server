package com.project.zipkok.common.exception_handler;

import com.project.zipkok.common.exception.AddressException;
import com.project.zipkok.common.exception.NoExistUserException;
import com.project.zipkok.common.response.BaseExceptionResponse;
import com.project.zipkok.common.response.BaseResponse;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.ADDRESS_SEARCH_FAILURE;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class AddressExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AddressException.class)
    public BaseExceptionResponse handle_AddressException(AddressException e) {
        log.error("[handle_AddressException]", e);
        return new BaseExceptionResponse(e.getExceptionStatus());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpClientErrorException.class)
    public BaseExceptionResponse handle_AddressException(HttpClientErrorException e) {
        log.error("[handle_AddressException]", e);
        return new BaseExceptionResponse(ADDRESS_SEARCH_FAILURE, e.getMessage());
    }


}
