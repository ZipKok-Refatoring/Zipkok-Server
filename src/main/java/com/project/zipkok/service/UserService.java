package com.project.zipkok.service;

import com.project.zipkok.common.argument_resolver.PreAuthorize;
import com.project.zipkok.common.enums.Gender;
import com.project.zipkok.common.enums.OAuthProvider;
import com.project.zipkok.common.enums.RealEstateType;
import com.project.zipkok.common.enums.TransactionType;
import com.project.zipkok.common.exception.user.NoMatchUserException;
import com.project.zipkok.common.exception.user.OnBoardingBadRequestException;
import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.common.service.RedisService;
import com.project.zipkok.config.RedisConfig;
import com.project.zipkok.dto.*;
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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.MEMBER_NOT_FOUND;
import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DesireResidenceRepository desireResidenceRepository;
    private final TransactionPriceConfigRepository transactionPriceConfigRepository;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

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

        User user = new User(email, oAuthProvider, nickname, gender,birthday);

        user.setDesireResidence(new DesireResidence(user));
        user.setTransactionPriceConfig(new TransactionPriceConfig(user));

        this.userRepository.save(user);

        long userId = this.userRepository.findByEmail(email).getUserId();

        AuthTokens authTokens = jwtProvider.createToken(email, userId);

        redisService.setValues(email, authTokens.getRefreshToken(), Duration.ofMillis(jwtProvider.getREFRESH_TOKEN_EXPIRED_IN()));

        return authTokens;
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
        transactionPriceConfig.setPurchaseMax(purchaseMax);
        this.transactionPriceConfigRepository.save(transactionPriceConfig);

        return null;
    }

    public GetMyPageResponse myPageLoad(long userId) {
        log.info("{UserService.myPageLoad}");

        //repository로부터 객체 가져오기
        User user = this.userRepository.findByUserId(userId);
        TransactionPriceConfig transactionPriceConfig = this.transactionPriceConfigRepository.findByUser(user);

        //return 할 dto 선언
        GetMyPageResponse getMyPageResponse = new GetMyPageResponse();

        //dto field 값 set
        getMyPageResponse.setNickname(user.getNickname());
        getMyPageResponse.setImageUrl(user.getProfileImgUrl());
        getMyPageResponse.setRealEstateType(user.getReslEstateType());

        getMyPageResponse.setAddress(this.desireResidenceRepository.findByUser(user).getAddress());

        String transactionType = null;

        //관심매물유형에 따라 dto field 값 set 작업 분기처리
        if(user.getTransactionType() == null){
            getMyPageResponse.setTransactionType(TransactionType.MONTHLY);
            transactionType = "월세";
        }
        else{
            getMyPageResponse.setTransactionType(user.getTransactionType());
            transactionType = user.getTransactionType().getDescription();
        }

        if(transactionType.equals("월세")){
            getMyPageResponse.setPriceMax(transactionPriceConfig.getMPriceMax());
            getMyPageResponse.setPriceMin(transactionPriceConfig.getMPriceMin());
            getMyPageResponse.setDepositMax(transactionPriceConfig.getMDepositMax());
            getMyPageResponse.setDepositMin(transactionPriceConfig.getMDepositMin());
        }
        else if(transactionType.equals("전세")){
            getMyPageResponse.setDepositMax(transactionPriceConfig.getYDepositMax());
            getMyPageResponse.setDepositMin(transactionPriceConfig.getYDepositMin());
        }
        else if(transactionType.equals("매매")){
            getMyPageResponse.setPriceMax(transactionPriceConfig.getPurchaseMax());
            getMyPageResponse.setPriceMin(transactionPriceConfig.getPurchaseMin());
        }

        return getMyPageResponse;
    }

    public GetMyPageDetailResponse myPageDetailLoad(long userId) {
        log.info("{UserService.myPageDetailLoad}");

        User user = this.userRepository.findByUserId(userId);
        TransactionPriceConfig transactionPriceConfig = this.transactionPriceConfigRepository.findByUser(user);

        GetMyPageDetailResponse getMyPageDetailResponse = new GetMyPageDetailResponse();

        getMyPageDetailResponse.setImageUrl(user.getProfileImgUrl());
        getMyPageDetailResponse.setNickname(user.getNickname());
        getMyPageDetailResponse.setBirthday(user.getBirthday());
        getMyPageDetailResponse.setGender(user.getGender());
        getMyPageDetailResponse.setAddress(this.desireResidenceRepository.findByUser(user).getAddress());
        getMyPageDetailResponse.setRealEstateType(user.getReslEstateType());

        if(user.getTransactionType() == null){
            getMyPageDetailResponse.setTransactionType(TransactionType.MONTHLY);
        }
        else{
            getMyPageDetailResponse.setTransactionType(user.getTransactionType());
        }

        getMyPageDetailResponse.setMpriceMin(transactionPriceConfig.getMPriceMin());
        getMyPageDetailResponse.setMpriceMax(transactionPriceConfig.getMPriceMax());
        getMyPageDetailResponse.setMdepositMin(transactionPriceConfig.getMDepositMin());
        getMyPageDetailResponse.setMdepositMax(transactionPriceConfig.getMDepositMax());
        getMyPageDetailResponse.setYdepositMin(transactionPriceConfig.getYDepositMin());
        getMyPageDetailResponse.setYdepositMax(transactionPriceConfig.getYDepositMax());
        getMyPageDetailResponse.setPriceMin(transactionPriceConfig.getPurchaseMin());
        getMyPageDetailResponse.setPriceMax(transactionPriceConfig.getPurchaseMax());

//        ModelMapper modelMapper = new ModelMapper();
//
//        modelMapper.typeMap(TransactionPriceConfig.class, GetMyPageDetailResponse.class).addMappings(mapper -> {
//            mapper.map(TransactionPriceConfig::getMPriceMin, GetMyPageDetailResponse::setMpriceMin);
//            mapper.map(TransactionPriceConfig::getMPriceMax, GetMyPageDetailResponse::setMpriceMax);
//            mapper.map(TransactionPriceConfig::getMDepositMin, GetMyPageDetailResponse::setMdepositMin);
//            mapper.map(TransactionPriceConfig::getMDepositMax, GetMyPageDetailResponse::setMdepositMax);
//            mapper.map(TransactionPriceConfig::getYDepositMin, GetMyPageDetailResponse::setYdepositMin);
//            mapper.map(TransactionPriceConfig::getYDepositMax, GetMyPageDetailResponse::setYdepositMax);
//            mapper.map(TransactionPriceConfig::getPurchaseMin, GetMyPageDetailResponse::setPriceMin);
//            mapper.map(TransactionPriceConfig::getPurchaseMax, GetMyPageDetailResponse::setPriceMax);
//        });
//
//        getMyPageDetailResponse = modelMapper.map(transactionPriceConfig, GetMyPageDetailResponse.class);
//
//        modelMapper.typeMap(User.class, GetMyPageDetailResponse.class).addMappings(mapper -> {
//            mapper.map(User::getProfileImgUrl, GetMyPageDetailResponse::setImageUrl);
//            mapper.map(User::getNickname, GetMyPageDetailResponse::setNickname);
//            mapper.map(User::getBirthday, GetMyPageDetailResponse::setBirthday);
//            mapper.map(User::getGender, GetMyPageDetailResponse::setGender);
//            mapper.map(User::getRealEstates, GetMyPageDetailResponse::setRealEstateType);
//            mapper.map(User::getTransactionType, GetMyPageDetailResponse::setTransactionType);
//        });
//
//        getMyPageDetailResponse = modelMapper.map(user, GetMyPageDetailResponse.class);
//
//        if(getMyPageDetailResponse.getTransactionType() == null){
//            getMyPageDetailResponse.setTransactionType(TransactionType.MONTHLY);
//        }
//
//        getMyPageDetailResponse.setAddress(user.getDesireResidence().getAddress());

        return getMyPageDetailResponse;
    }
}
