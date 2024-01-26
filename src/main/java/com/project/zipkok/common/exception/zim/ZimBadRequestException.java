package com.project.zipkok.common.exception.zim;

import com.project.zipkok.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class ZimBadRequestException extends RuntimeException{
    private final ResponseStatus exceptionStatus;

    public ZimBadRequestException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}
