package com.project.zipkok.service;

import com.project.zipkok.common.enums.Gender;
import com.project.zipkok.common.enums.OAuthProvider;
import com.project.zipkok.dto.GetUserResponse;
import com.project.zipkok.dto.PostSignUpRequest;
import com.project.zipkok.model.User;
import com.project.zipkok.repository.UserRepository;
import com.project.zipkok.util.jwt.AuthTokens;
import com.project.zipkok.util.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public List<GetUserResponse> getUsers() {
        List<GetUserResponse> userList = userRepository.findAll()
                .stream()
                .map(user -> new GetUserResponse(
                        user.getNickname(),
                        user.getProfileImgUrl(),
                        user.getDesireResidence().getAddress(),
                        user.getReslEstateType().getDescription(),
                        user.getTransactionType().getDescription(),
                        user.getTransactionPriceConfig().getMPriceMax(),
                        user.getTransactionPriceConfig().getMPriceMin(),
                        user.getTransactionPriceConfig().getMDepositMax(),
                        user.getTransactionPriceConfig().getMDepositMin()
                )).collect(Collectors.toList());

        return userList;
    }

    public AuthTokens signUp(PostSignUpRequest postSignUpRequest) {
        log.info("{UserService.signUp}");

        String email = postSignUpRequest.getEmail();
        OAuthProvider oAuthProvider = postSignUpRequest.getOauthProvider();
        String nickname = postSignUpRequest.getNickname();
        Gender gender = postSignUpRequest.getGender();
        String birthday = postSignUpRequest.getBirthday();

        User user = new User(email, oAuthProvider, nickname,gender,birthday);

        this.userRepository.save(user);

        long userId = this.userRepository.findByEmail(email).getUserId();

        return this.jwtProvider.createToken(email, userId);
    }
}
