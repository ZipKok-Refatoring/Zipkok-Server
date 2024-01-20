package com.project.zipkok.common.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.project.zipkok.common.response.status.ResponseStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonPropertyOrder({"code", "message", "timestamp"})
public class BaseExceptionResponse implements ResponseStatus {

    private final int code;
    private final String message;
    private final LocalDateTime timestamp;

    public BaseExceptionResponse(ResponseStatus status) {
        this.code = status.getCode();
        this.message = status.getMessage();
        this.timestamp = LocalDateTime.now();
    }

    public BaseExceptionResponse(ResponseStatus status, String message) {
        this.code = status.getCode();
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}

