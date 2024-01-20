package com.project.zipkok.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.zipkok.util.jwt.AuthTokens;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public class GetLoginResponse {

    private boolean isMember;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AuthTokens authTokens;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;

    public boolean getIsMember() {
        return this.isMember;
    }
    public String getEmail() {
        return this.email;
    }

    public AuthTokens getAuthTokens() {
        return  this.authTokens;
    }


}
