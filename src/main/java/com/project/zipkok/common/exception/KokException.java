package com.project.zipkok.common.exception;

import com.project.zipkok.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class KokException extends RuntimeException{

    private final ResponseStatus responseStatus;

    public KokException(ResponseStatus responseStatus) {
        super(responseStatus.getMessage());
        this.responseStatus = responseStatus;
    }

}
