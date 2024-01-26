package com.project.zipkok.common.exception;

import com.project.zipkok.common.response.BaseExceptionResponse;
import com.project.zipkok.common.response.status.BaseExceptionResponseStatus;
import com.project.zipkok.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class RealEstateException extends RuntimeException {

    private final ResponseStatus responseStatus;

    public RealEstateException(ResponseStatus responseStatus) {
        super(responseStatus.getMessage());
        this.responseStatus = responseStatus;
    }
}
