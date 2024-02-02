package com.project.zipkok.common.exception.s3;

import com.project.zipkok.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class FileUploadException extends RuntimeException{
    private final ResponseStatus exceptionStatus;

    public FileUploadException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}
