package com.project.zipkok.controller;

import com.project.zipkok.common.oauth.request.KakaoLoginParams;
import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.common.response.status.BaseExceptionResponseStatus;
import com.project.zipkok.common.service.OAuthLoginService;
import com.project.zipkok.dto.GetLoginResponse;
import com.project.zipkok.util.jwt.AuthTokens;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final OAuthLoginService oAuthLoginService;

    @GetMapping("/oauth/kakao/callback")
    public BaseResponse<GetLoginResponse> loginKakao(@RequestParam("code") String authorizationCode) {
        KakaoLoginParams params = new KakaoLoginParams(authorizationCode);
        return new BaseResponse<GetLoginResponse>(oAuthLoginService.login(params));
    }

}
