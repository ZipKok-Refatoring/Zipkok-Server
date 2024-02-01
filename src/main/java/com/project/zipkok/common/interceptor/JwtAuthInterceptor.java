package com.project.zipkok.common.interceptor;

import com.project.zipkok.common.exception.jwt.bad_request.JwtNoTokenException;
import com.project.zipkok.common.exception.jwt.bad_request.JwtUnsupportedTokenException;
import com.project.zipkok.common.exception.jwt.unauthorized.JwtExpiredTokenException;
import com.project.zipkok.common.exception.jwt.unauthorized.JwtInvalidTokenException;
import com.project.zipkok.common.exception.user.NoMatchUserException;
import com.project.zipkok.common.exception.user.UserBadRequestException;
import com.project.zipkok.model.User;
import com.project.zipkok.repository.UserRepository;
import com.project.zipkok.util.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private static final String JWT_TOKEN_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("[JwtAuthInterceptor.preHandle]");

        String method = request.getMethod();
        String url = request.getRequestURI();

        if((method.equals(HttpMethod.OPTIONS.name()) || method.equals(HttpMethod.POST.name())) && url.equals("/user")){
            return true;
        }
        log.info("[POST & OPTIONS /user must not pass this log]");

        String accessToken = resolveAccessToken(request);
        validateAccessToken(accessToken);

        String email = jwtProvider.getEmail(accessToken);
        validatePayload(email);

        User user = this.userRepository.findByEmail(email);

        if(user == null){
            throw new NoMatchUserException(MEMBER_NOT_FOUND);
        }
        if(!user.getStatus().equals("active")){
            throw new UserBadRequestException(NEED_TO_LOGIN);
        }

        long userId = user.getUserId();
        request.setAttribute("userId", userId);
        return true;
    }

    private String resolveAccessToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        validateToken(token);
        return token.substring(JWT_TOKEN_PREFIX.length());
    }

    private void validateToken(String token) {
        if (token == null) {
            throw new JwtNoTokenException(TOKEN_NOT_FOUND);
        }
        if (!token.startsWith(JWT_TOKEN_PREFIX)) {
            throw new JwtUnsupportedTokenException(UNSUPPORTED_TOKEN_TYPE);
        }
    }

    private void validateAccessToken(String accessToken) {
        if (jwtProvider.isExpiredToken(accessToken)) {
            throw new JwtExpiredTokenException(EXPIRED_TOKEN);
        }
    }

    private void validatePayload(String email) {
        if (email == null) {
            throw new JwtInvalidTokenException(INVALID_TOKEN);
        }
    }

}
