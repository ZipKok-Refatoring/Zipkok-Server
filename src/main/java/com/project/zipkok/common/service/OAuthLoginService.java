package com.project.zipkok.common.service;

import com.project.zipkok.common.exception.NoExistUserException;
import com.project.zipkok.common.oauth.RequestOAuthInfoService;
import com.project.zipkok.common.oauth.request.OAuthLoginParams;
import com.project.zipkok.common.oauth.response.OAuthInfoResponse;
import com.project.zipkok.dto.GetLoginResponse;
import com.project.zipkok.model.User;
import com.project.zipkok.repository.KokImageRepository;
import com.project.zipkok.repository.UserRepository;
import com.project.zipkok.util.FileUploadUtils;
import com.project.zipkok.util.jwt.AuthTokens;
import com.project.zipkok.util.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.KAKAO_LOGIN_NEED_REGISTRATION;
import static com.project.zipkok.service.UserService.extractKeyFromUrl;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RequestOAuthInfoService requestOAuthInfoService;
    private final RedisService redisService;
    private final FileUploadUtils fileUploadUtils;
    private final KokImageRepository kokImageRepository;

    public GetLoginResponse login(OAuthLoginParams params) throws IOException {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);

        User user = userRepository.findByEmail(oAuthInfoResponse.getEmail());

        if (user != null) {

            if(!user.getStatus().equals("active")){

                if(user.getStatus().equals("pending")) {
                    String newProfileUrl = fileUploadUtils.updateFileDir(extractKeyFromUrl(user.getProfileImgUrl()), "");

                    user.getKoks()
                            .stream()
                            .forEach(kok -> kok.getKokImages()
                                    .stream()
                                    .forEach(kokImage -> {
                                        try {
                                            kokImage.setImageUrl(this.fileUploadUtils.updateFileDir(extractKeyFromUrl(kokImage.getImageUrl()), ""));
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        kokImageRepository.save(kokImage);
                                    }));

                    user.setProfileImgUrl(newProfileUrl);
                }

                user.setStatus("active");
                this.userRepository.save(user);
            }

            AuthTokens authTokens = jwtProvider.createToken(user.getEmail(), user.getUserId());

            redisService.setValues(user.getEmail(), authTokens.getRefreshToken(), Duration.ofMillis(jwtProvider.getREFRESH_TOKEN_EXPIRED_IN()));

            return new GetLoginResponse(true, authTokens, null);
        }

        GetLoginResponse getLoginResponse = new GetLoginResponse(false, null, oAuthInfoResponse.getEmail());
        throw new NoExistUserException(KAKAO_LOGIN_NEED_REGISTRATION, getLoginResponse);

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
