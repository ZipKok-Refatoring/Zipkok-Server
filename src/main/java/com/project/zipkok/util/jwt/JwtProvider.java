package com.project.zipkok.util.jwt;

import io.jsonwebtoken.*;
import com.project.zipkok.common.exception.jwt.bad_request.JwtUnsupportedTokenException;
import com.project.zipkok.common.exception.jwt.unauthorized.JwtInvalidTokenException;
import com.project.zipkok.common.exception.jwt.unauthorized.JwtMalformedTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Component
public class JwtProvider {

    @Value("${secret.jwt-secret-key}")
    private String JWT_SECRET_KEY;

    @Value("${secret.jwt-expired-in.access-token}")
    private long JWT_EXPIRED_IN;

    @Value("${secret.jwt-expired-in.refresh-token}")
    private long REFRESH_TOKEN_EXPIRED_IN;

    public AuthTokens createToken(String principal, Long userId) {
        log.info("JWT key={}", JWT_SECRET_KEY);

        Claims claims = Jwts.claims().setSubject(principal);
        Date now = new Date();
        Date accessTokenExpiredAt = new Date(now.getTime() + JWT_EXPIRED_IN);
        Date refreshTokenExpiredAt = new Date(now.getTime() + REFRESH_TOKEN_EXPIRED_IN);

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(accessTokenExpiredAt)
                .claim("userId", userId)
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY)
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(refreshTokenExpiredAt)
                .claim("userId", userId)
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY)
                .compact();

        return AuthTokens.of(accessToken, refreshToken, JWT_EXPIRED_IN, REFRESH_TOKEN_EXPIRED_IN);
    }

    public boolean isExpiredToken(String token) throws JwtInvalidTokenException {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(JWT_SECRET_KEY).build()
                    .parseClaimsJws(token);
            return claims.getBody().getExpiration().before(new Date());

        } catch (ExpiredJwtException e) {
            return true;

        } catch (UnsupportedJwtException e) {
            throw new JwtUnsupportedTokenException(UNSUPPORTED_TOKEN_TYPE);
        } catch (MalformedJwtException e) {
            throw new JwtMalformedTokenException(MALFORMED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new JwtInvalidTokenException(INVALID_TOKEN);
        } catch (JwtException e) {
            log.error("[JwtTokenProvider.validateAccessToken]", e);
            throw e;
        }
    }

    public String getPrincipal(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(JWT_SECRET_KEY).build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

}