package com.project.zipkok.common.exception.zim;

import com.project.zipkok.common.response.status.ResponseStatus;

public class NoZimMatchedUser extends RuntimeException{
    private final ResponseStatus exceptionStatus;

    public NoZimMatchedUser(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}
