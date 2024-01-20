package com.project.zipkok.common.exception;

import com.project.zipkok.common.response.status.BaseExceptionResponseStatus;
import com.project.zipkok.common.response.status.ResponseStatus;
import com.project.zipkok.dto.GetLoginResponse;
import lombok.Getter;

@Getter
public class NoExistUserException extends RuntimeException {

    private final BaseExceptionResponseStatus exceptionStatus;

    private GetLoginResponse getLoginResponse;

    public NoExistUserException(BaseExceptionResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public NoExistUserException(BaseExceptionResponseStatus exceptionStatus, String message) {
        super(message);
        this.exceptionStatus = exceptionStatus;
    }

    public NoExistUserException(BaseExceptionResponseStatus exceptionStatus, GetLoginResponse getLoginResponse) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
        this.getLoginResponse = getLoginResponse;
    }
}

