package com.project.zipkok.dto;

import com.project.zipkok.util.jwt.AuthTokens;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostRefreshTokenResponse {
    private AuthTokens authTokens;
}
