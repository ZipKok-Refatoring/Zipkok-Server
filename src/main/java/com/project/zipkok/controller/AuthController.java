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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.INVALID_REFRESH_TOKEN;
import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.TOKEN_REISSUE_SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "소셜로그인/ 인증/ 인가 관련 API")
public class AuthController {
    private final OAuthLoginService oAuthLoginService;

    private final AuthService authService;


    @Operation(summary = "인가코드를 받아 서비스의 회원여부를 응답하는 API ", description = "authorizaion code를 쿼리 파라미터로 추가한뒤, 요청해주세요")
    @GetMapping("/oauth/kakao/callback")
    public BaseResponse<GetLoginResponse> loginKakao(@RequestParam("code") String authorizationCode) throws IOException {
        log.info("AuthController.loginKakao");
        KakaoLoginParams params = new KakaoLoginParams(authorizationCode);
        return new BaseResponse<GetLoginResponse>(oAuthLoginService.login(params));
    }

    @Operation(summary = "토큰 재발행 API", description = "Token이 만료된 경우에 refreshToken을 통해 재발급 받는 API")
    @PostMapping("/auth/refreshToken")
    public BaseResponse<PostRefreshTokenResponse> reIssueToken(@Validated @RequestBody PostRefreshTokenRequest postRefreshTokenRequest, BindingResult bindingResult) {
        log.info("AuthController.reIssueToken");
        if (bindingResult.hasErrors()) {
            throw new JwtInvalidRefreshTokenException(INVALID_REFRESH_TOKEN);
        }

        return new BaseResponse<>(TOKEN_REISSUE_SUCCESS, authService.reIssueToken(postRefreshTokenRequest.getRefreshToken()));

    }

}
