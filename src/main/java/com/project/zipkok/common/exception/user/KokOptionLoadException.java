package com.project.zipkok.common.exception.user;

import com.project.zipkok.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class KokOptionLoadException extends RuntimeException{
    private final ResponseStatus exceptionStatus;

    public KokOptionLoadException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}
