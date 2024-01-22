package com.project.zipkok.common.exception_handler;

import com.project.zipkok.common.exception.NoExistUserException;
import com.project.zipkok.common.exception.user.UserBadRequestException;
import com.project.zipkok.common.response.BaseExceptionResponse;
import com.project.zipkok.common.response.BaseResponse;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.KAKAO_LOGIN_NEED_REGISTRATION;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class UserExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NoExistUserException.class)
    public BaseResponse handle_NoExistUserException(NoExistUserException e) {
        log.error("[handle_UserException]", e);
        return new BaseResponse(e.getExceptionStatus(), e.getGetLoginResponse());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserBadRequestException.class)
    public BaseExceptionResponse handle_UserBadRequestException(UserBadRequestException e) {
        log.error("[handle_UserBadRequestException]", e);
        return new BaseExceptionResponse(e.getExceptionStatus());
    }
}
