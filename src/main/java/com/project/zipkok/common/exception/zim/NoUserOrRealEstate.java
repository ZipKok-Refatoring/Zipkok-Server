package com.project.zipkok.common.exception.zim;

import com.project.zipkok.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class NoUserOrRealEstate extends RuntimeException{
    private final ResponseStatus exceptionStatus;

    public NoUserOrRealEstate(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}
