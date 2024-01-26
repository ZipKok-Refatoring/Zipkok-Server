package com.project.zipkok.common.exception.user;

import com.project.zipkok.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class NoMatchUserException extends RuntimeException{
    private final ResponseStatus exceptionStatus;

    public NoMatchUserException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}
