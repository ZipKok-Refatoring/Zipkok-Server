package com.project.zipkok.common.oauth;

import com.project.zipkok.common.enums.OAuthProvider;
import com.project.zipkok.common.oauth.request.OAuthLoginParams;
import com.project.zipkok.common.oauth.response.OAuthInfoResponse;

public interface OAuthApiClient {
    OAuthProvider oAuthProvider();
    String requestAccessToken(OAuthLoginParams params);
    OAuthInfoResponse requestOauthInfo(String accessToken);
}
