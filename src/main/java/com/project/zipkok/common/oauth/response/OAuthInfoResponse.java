package com.project.zipkok.common.oauth.response;

import com.project.zipkok.common.enums.OAuthProvider;

public interface OAuthInfoResponse {
    String getEmail();
    String getNickname();
    OAuthProvider getOAuthProvider();
}
