package com.project.zipkok.common.exception;

import com.project.zipkok.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

    private final ResponseStatus exceptionStatus;

    public UserException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public UserException(ResponseStatus exceptionStatus, String message) {
        super(message);
        this.exceptionStatus = exceptionStatus;
    }
}

