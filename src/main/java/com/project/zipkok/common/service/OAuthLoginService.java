package com.project.zipkok.common.service;

import com.project.zipkok.common.enums.Gender;
import com.project.zipkok.common.oauth.RequestOAuthInfoService;
import com.project.zipkok.common.oauth.request.OAuthLoginParams;
import com.project.zipkok.common.oauth.response.OAuthInfoResponse;
import com.project.zipkok.dto.GetLoginResponse;
import com.project.zipkok.model.DesireResidence;
import com.project.zipkok.model.TransactionPriceConfig;
import com.project.zipkok.model.User;
import com.project.zipkok.repository.UserRepository;
import com.project.zipkok.util.jwt.AuthTokens;
import com.project.zipkok.util.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RequestOAuthInfoService requestOAuthInfoService;

    public GetLoginResponse login(OAuthLoginParams params) {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);

        User user = userRepository.findByEmail(oAuthInfoResponse.getEmail());

        if (user != null) {
            AuthTokens authTokens = jwtProvider.createToken(user.getEmail(), user.getUserId());
            return new GetLoginResponse(true, authTokens, null);
        }

        return new GetLoginResponse(false, null, oAuthInfoResponse.getEmail());

    }

//    private String findOrCreateUser(OAuthInfoResponse oAuthInfoResponse) {
//
//        if(userRepository.findByEmail(oAuthInfoResponse.getEmail()) != null) {
//            return oAuthInfoResponse.getEmail();
//        }
//
//        return newMember(oAuthInfoResponse);
//    }

//    private String newMember(OAuthInfoResponse oAuthInfoResponse) {
//        User user = User.builder()
//                .email(oAuthInfoResponse.getEmail())
//                .oAuthProvider(oAuthInfoResponse.getOAuthProvider())
//                .build();
//
//        DesireResidence desireResidence = new DesireResidence(user);
//        TransactionPriceConfig transactionPriceConfig = new TransactionPriceConfig(user);
//        user.setDesireResidence(desireResidence);
//        user.setTransactionPriceConfig(transactionPriceConfig);
//
//        return userRepository.save(user).getEmail();
//    }
}
