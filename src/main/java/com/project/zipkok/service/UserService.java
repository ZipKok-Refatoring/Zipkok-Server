package com.project.zipkok.service;

import com.project.zipkok.common.argument_resolver.PreAuthorize;
import com.project.zipkok.common.enums.Gender;
import com.project.zipkok.common.enums.OAuthProvider;
import com.project.zipkok.common.enums.RealEstateType;
import com.project.zipkok.common.exception.user.OnBoardingBadRequestException;
import com.project.zipkok.dto.GetUserResponse;
import com.project.zipkok.dto.PatchOnBoardingRequest;
import com.project.zipkok.dto.PostSignUpRequest;
import com.project.zipkok.model.DesireResidence;
import com.project.zipkok.model.TransactionPriceConfig;
import com.project.zipkok.model.User;
import com.project.zipkok.repository.DesireResidenceRepository;
import com.project.zipkok.repository.TransactionPriceConfigRepository;
import com.project.zipkok.repository.UserRepository;
import com.project.zipkok.util.jwt.AuthTokens;
import com.project.zipkok.util.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DesireResidenceRepository desireResidenceRepository;
    private final TransactionPriceConfigRepository transactionPriceConfigRepository;
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

    @Transactional
    public AuthTokens signUp(PostSignUpRequest postSignUpRequest) {
        log.info("{UserService.signUp}");

        String email = postSignUpRequest.getEmail();
        OAuthProvider oAuthProvider = postSignUpRequest.getOauthProvider();
        String nickname = postSignUpRequest.getNickname();
        Gender gender = postSignUpRequest.getGender();
        String birthday = postSignUpRequest.getBirthday();

        User user = new User(email, oAuthProvider, nickname,gender,birthday);
        DesireResidence desireResidence = new DesireResidence(user);
        TransactionPriceConfig transactionPriceConfig = new TransactionPriceConfig(user);

        this.userRepository.save(user);
        this.desireResidenceRepository.save(desireResidence);
        this.transactionPriceConfigRepository.save(transactionPriceConfig);

        long userId = this.userRepository.findByEmail(email).getUserId();

        return this.jwtProvider.createToken(email, userId);
    }

    @Transactional
    public Objects setOnBoarding(PatchOnBoardingRequest patchOnBoardingRequest, long userId) {
        log.info("{UserService.setOnBoarding}");

        String address = patchOnBoardingRequest.getAddress();
        double latitude = patchOnBoardingRequest.getLatitude();
        double longitude = patchOnBoardingRequest.getLongitude();
        RealEstateType realEstateType = patchOnBoardingRequest.getRealEstateType();
        long mpriceMin = patchOnBoardingRequest.getMpriceMin();
        long mpriceMax = patchOnBoardingRequest.getMpriceMax();
        long mdepositMin = patchOnBoardingRequest.getMdepositMin();
        long mdepositMax = patchOnBoardingRequest.getMdepositMax();
        long ydepositMin = patchOnBoardingRequest.getYdepositMin();
        long ydepositMax = patchOnBoardingRequest.getYdepositMax();
        long purchaseMin = patchOnBoardingRequest.getPurchaseMin();
        long purchaseMax = patchOnBoardingRequest.getPurchaseMax();

        //User table에 realEstateType 수정
        User user = this.userRepository.findByUserId(userId);

        user.setReslEstateType(realEstateType);
        this.userRepository.save(user);


        //희망 거주지 table 수정
        DesireResidence desireResidence = this.desireResidenceRepository.findByUser(user);

        desireResidence.setAddress(address);
        desireResidence.setLatitude(latitude);
        desireResidence.setLongitude(longitude);
        this.desireResidenceRepository.save(desireResidence);


        //거래가격설정 table 수정
        TransactionPriceConfig transactionPriceConfig = this.transactionPriceConfigRepository.findByUser(user);

        transactionPriceConfig.setMPriceMin(mpriceMin);
        transactionPriceConfig.setMPriceMax(mpriceMax);
        transactionPriceConfig.setMDepositMin(mdepositMin);
        transactionPriceConfig.setMDepositMax(mdepositMax);
        transactionPriceConfig.setYDepositMin(ydepositMin);
        transactionPriceConfig.setYDepositMax(ydepositMax);
        transactionPriceConfig.setPurchaseMin(purchaseMin);
        transactionPriceConfig.setPuchaseMax(purchaseMax);
        this.transactionPriceConfigRepository.save(transactionPriceConfig);

        return null;
    }
}
