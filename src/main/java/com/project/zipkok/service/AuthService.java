package com.project.zipkok.service;

import com.project.zipkok.common.exception.jwt.unauthorized.JwtInvalidRefreshTokenException;
import com.project.zipkok.common.service.RedisService;
import com.project.zipkok.dto.PostRefreshTokenRequest;
import com.project.zipkok.dto.PostRefreshTokenResponse;
import com.project.zipkok.util.jwt.AuthTokens;
import com.project.zipkok.util.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.INVALID_REFRESH_TOKEN;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RedisService redisService;
    private final JwtProvider jwtProvider;


    public PostRefreshTokenResponse reIssueToken(String refreshToken) {
        log.info("AuthService.reIssueToken");

        jwtProvider.isExpiredToken(refreshToken);

        String email = jwtProvider.getEmail(refreshToken);

        if (!redisService.getValues(email).equals(refreshToken)) {
            throw new JwtInvalidRefreshTokenException(INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtProvider.getId(refreshToken);

        AuthTokens authTokens = jwtProvider.createToken(email, userId);

        redisService.setValues(email, authTokens.getRefreshToken(), Duration.ofMillis(jwtProvider.getREFRESH_TOKEN_EXPIRED_IN()));

        return new PostRefreshTokenResponse(authTokens);
    }
}
