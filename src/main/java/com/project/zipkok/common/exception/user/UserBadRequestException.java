package com.project.zipkok.common.exception.user;

import com.project.zipkok.common.response.status.ResponseStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class UserBadRequestException extends RuntimeException{
    private final ResponseStatus exceptionStatus;

    public UserBadRequestException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}
