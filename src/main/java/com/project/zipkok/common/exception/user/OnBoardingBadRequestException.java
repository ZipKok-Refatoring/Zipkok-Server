package com.project.zipkok.common.exception.user;

import com.project.zipkok.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class OnBoardingBadRequestException extends RuntimeException{
    private final ResponseStatus exceptionStatus;

    public OnBoardingBadRequestException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}

