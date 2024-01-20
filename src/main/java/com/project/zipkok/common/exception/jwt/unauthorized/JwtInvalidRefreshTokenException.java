package com.project.zipkok.common.exception.jwt.unauthorized;

import com.project.zipkok.common.response.status.ResponseStatus;

public class JwtInvalidRefreshTokenException extends JwtUnauthorizedTokenException {

    private final ResponseStatus exceptionStatus;

    public JwtInvalidRefreshTokenException(ResponseStatus exceptionStatus) {
        super(exceptionStatus);
        this.exceptionStatus = exceptionStatus;
    }

}
