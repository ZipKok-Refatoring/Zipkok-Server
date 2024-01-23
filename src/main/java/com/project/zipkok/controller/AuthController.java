package com.project.zipkok.controller;

import com.project.zipkok.common.exception.jwt.unauthorized.JwtInvalidRefreshTokenException;
import com.project.zipkok.common.oauth.request.KakaoLoginParams;
import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.common.response.status.BaseExceptionResponseStatus;
import com.project.zipkok.common.service.OAuthLoginService;
import com.project.zipkok.dto.GetLoginResponse;
import com.project.zipkok.dto.PostRefreshTokenRequest;
import com.project.zipkok.dto.PostRefreshTokenResponse;
import com.project.zipkok.service.AuthService;
import com.project.zipkok.util.jwt.AuthTokens;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.INVALID_REFRESH_TOKEN;

@Slf4j
@CrossOrigin(origins = {"https://localhost:3000", "http://localhost:3000"})
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final OAuthLoginService oAuthLoginService;

    private final AuthService authService;


    @GetMapping("/oauth/kakao/callback")
    public BaseResponse<GetLoginResponse> loginKakao(@RequestParam("code") String authorizationCode) {
        log.info("AuthController.loginKakao");
        KakaoLoginParams params = new KakaoLoginParams(authorizationCode);
        return new BaseResponse<GetLoginResponse>(oAuthLoginService.login(params));
    }

    @PostMapping("/auth/refreshToken")
    public BaseResponse<PostRefreshTokenResponse> reIssueToken(@Validated @RequestBody PostRefreshTokenRequest postRefreshTokenRequest, BindingResult bindingResult) {
        log.info("AuthController.reIssueToken");
        if (bindingResult.hasErrors()) {
            throw new JwtInvalidRefreshTokenException(INVALID_REFRESH_TOKEN);
        }

        return new BaseResponse<>(authService.reIssueToken(postRefreshTokenRequest.getRefreshToken()));

    }

}
