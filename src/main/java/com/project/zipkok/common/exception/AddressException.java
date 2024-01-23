package com.project.zipkok.common.exception;

import com.project.zipkok.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class AddressException extends RuntimeException {

    private final ResponseStatus exceptionStatus;

    public AddressException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

}
