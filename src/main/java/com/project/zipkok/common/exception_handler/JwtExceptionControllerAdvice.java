package com.project.zipkok.common.exception_handler;

import com.project.zipkok.common.exception.jwt.bad_request.JwtNoTokenException;
import com.project.zipkok.common.exception.jwt.bad_request.JwtUnsupportedTokenException;
import com.project.zipkok.common.exception.jwt.unauthorized.JwtExpiredTokenException;
import com.project.zipkok.common.exception.jwt.unauthorized.JwtInvalidRefreshTokenException;
import com.project.zipkok.common.exception.jwt.unauthorized.JwtInvalidTokenException;
import jakarta.annotation.Priority;
import com.project.zipkok.common.exception.jwt.bad_request.JwtBadRequestException;
import com.project.zipkok.common.exception.jwt.unauthorized.JwtUnauthorizedTokenException;
import com.project.zipkok.common.response.BaseExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class JwtExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JwtBadRequestException.class)
    public BaseExceptionResponse handle_JwtBadRequestException(JwtBadRequestException e) {
        log.error("[handle_JwtBadRequestException]", e);
        return new BaseExceptionResponse(e.getExceptionStatus());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtUnauthorizedTokenException.class)
    public BaseExceptionResponse handle_JwtUnauthorizedException(JwtUnauthorizedTokenException e) {
        log.error("[handle_JwtUnauthorizedException]", e);
        return new BaseExceptionResponse(e.getExceptionStatus());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtInvalidRefreshTokenException.class)
    public BaseExceptionResponse handle_JwtInvalidRefreshTokenException(JwtInvalidRefreshTokenException e) {
        log.error("[handle_JwtInvalidRefreshTokenException]", e);
        return new BaseExceptionResponse(e.getExceptionStatus());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtNoTokenException.class)
    public BaseExceptionResponse handle_JwtNoTokenException(JwtNoTokenException e) {
        log.error("[handle_JwtNoTokenException]", e);
        return new BaseExceptionResponse(e.getExceptionStatus());
    }


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtUnsupportedTokenException.class)
    public BaseExceptionResponse handle_JwtUnsupportedTokenException(JwtUnsupportedTokenException e) {
        log.error("[handle_JwtUnsupportedTokenException]", e);
        return new BaseExceptionResponse(e.getExceptionStatus());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtExpiredTokenException.class)
    public BaseExceptionResponse handle_JwtExpiredTokenException(JwtExpiredTokenException e) {
        log.error("[handle_JwtExpiredTokenException]", e);
        return new BaseExceptionResponse(e.getExceptionStatus());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtInvalidTokenException.class)
    public BaseExceptionResponse handle_JwtInvalidTokenException(JwtInvalidTokenException e) {
        log.error("[handle_JwtInvalidTokenException]", e);
        return new BaseExceptionResponse(e.getExceptionStatus());
    }
}
