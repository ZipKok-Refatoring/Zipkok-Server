package com.project.zipkok.common.exception;

import com.project.zipkok.common.response.status.BaseExceptionResponseStatus;
import com.project.zipkok.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class RealEstateException extends RuntimeException {

    private final BaseExceptionResponseStatus baseExceptionResponseStatus;

    public RealEstateException(BaseExceptionResponseStatus baseExceptionResponseStatus) {
        super(baseExceptionResponseStatus.getMessage());
        this.baseExceptionResponseStatus = baseExceptionResponseStatus;
    }
}
