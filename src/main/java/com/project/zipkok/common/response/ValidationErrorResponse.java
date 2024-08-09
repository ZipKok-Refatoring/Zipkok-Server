package com.project.zipkok.common.response;

import com.project.zipkok.common.response.status.BaseExceptionResponseStatus;
import com.project.zipkok.common.response.status.ResponseStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class ValidationErrorResponse extends BaseExceptionResponse {
    private final List<FieldErrorDetail> fieldErrors;

    public ValidationErrorResponse(ResponseStatus responseStatus, List<FieldErrorDetail> fieldErrors) {
        super(responseStatus);
        this.fieldErrors = fieldErrors;
    }
}
