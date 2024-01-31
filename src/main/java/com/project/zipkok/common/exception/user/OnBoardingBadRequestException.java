package com.project.zipkok.common.exception.user;

import com.project.zipkok.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class OnBoardingBadRequestException extends RuntimeException{
    private final ResponseStatus exceptionStatus;

    private String errorMessage;

    public OnBoardingBadRequestException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
        this.errorMessage = exceptionStatus.getMessage();
    }

    public OnBoardingBadRequestException(ResponseStatus exceptionStatus, String message) {
        super(message);
        this.exceptionStatus = exceptionStatus;
        this.errorMessage = message;
    }
}

