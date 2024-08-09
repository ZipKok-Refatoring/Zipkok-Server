package com.project.zipkok.service;

import com.project.zipkok.common.enums.*;
import com.project.zipkok.common.exception.s3.FileUploadException;
import com.project.zipkok.common.exception.user.KokOptionLoadException;
import com.project.zipkok.common.exception.user.UserBadRequestException;
import com.project.zipkok.common.service.RedisService;
import com.project.zipkok.dto.*;
import com.project.zipkok.model.DesireResidence;
import com.project.zipkok.model.TransactionPriceConfig;
import com.project.zipkok.model.User;
import com.project.zipkok.repository.DesireResidenceRepository;
import com.project.zipkok.repository.TransactionPriceConfigRepository;
import com.project.zipkok.repository.UserRepository;
import com.project.zipkok.model.*;
import com.project.zipkok.repository.*;
import com.project.zipkok.util.FileUploadUtils;
import com.project.zipkok.util.jwt.AuthTokens;
import com.project.zipkok.util.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DesireResidenceRepository desireResidenceRepository;
    private final TransactionPriceConfigRepository transactionPriceConfigRepository;
    private final OptionRepository optionRepository;
    private final KokImageRepository kokImageRepository;
    private final HighLightBulkJdbcRepository highlightBulkJdbcRepository;
    private final ImpressionBulkJdbcRepository impressionBulkJdbcRepository;
    private final OptionBulkJdbcRepository optionBulkJdbcRepository;
    private final DetailOptionBulkJdbcRepository detailOptionBulkJdbcRepository;

    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final FileUploadUtils fileUploadUtils;

    private final HighlightRepository highlightRepository;

    @Transactional
    public AuthTokens signUp(PostSignUpRequest postSignUpRequest){
        log.info("{UserService.signUp}");

        User user = createUser(postSignUpRequest);

        AuthTokens authTokens = makeJwtToken(user);

        addRedisEntry(user, authTokens);

        return authTokens;
    }

    private User createUser(PostSignUpRequest postSignUpRequest) {
        log.info("{UserService.createUser}");

        User user = postSignUpRequest.toEntity();
        userRepository.save(user);

        DesireResidence desireResidence = new DesireResidence(user);
        TransactionPriceConfig transactionPriceConfig = new TransactionPriceConfig(user);

        desireResidenceRepository.save(desireResidence);
        transactionPriceConfigRepository.save(transactionPriceConfig);

        makeDefaultUserInfo(user);

        return user;
    }

    private void makeDefaultUserInfo(User user) {
        List<Highlight> highlights = Highlight.makeDefaultHighlights(user).stream().toList();
        highlightBulkJdbcRepository.saveAll(highlights);
        List<Option> options = Option.makeDefaultOptions(user);
        optionBulkJdbcRepository.saveAll(options);
        List<Impression> impressions = Impression.makeDefaultImpressions(user).stream().toList();
        impressionBulkJdbcRepository.saveAll(impressions);
        List<DetailOption> detailOptions = DetailOption.makeDefaultDetailOptions(options);
        detailOptionBulkJdbcRepository.saveAll(detailOptions);
    }

    private AuthTokens makeJwtToken(User user) {
        return jwtProvider.createToken(user.getEmail(), user.getUserId());
    }

    private void addRedisEntry(User user, AuthTokens authTokens){
        redisService.setValues(user.getEmail(), authTokens.getRefreshToken(), Duration.ofMillis(jwtProvider.getREFRESH_TOKEN_EXPIRED_IN()));
    }

    @Transactional
    public Objects setOnBoarding(PatchOnBoardingRequest patchOnBoardingRequest, long userId) {
        log.info("{UserService.setOnBoarding}");

        User user = userRepository.findByUserIdWithDesireResidenceAndTransactionPriceConfig(userId);
        user.setOnBoardingInfo(patchOnBoardingRequest);

        userRepository.save(user);
        return null;
    }

    public GetMyPageResponse myPageLoad(long userId) {
        log.info("{UserService.myPageLoad}");

        User user = userRepository.findByUserIdWithDesireResidenceAndTransactionPriceConfig(userId);

        return GetMyPageResponse.from(user);
    }

    public GetMyPageDetailResponse myPageDetailLoad(long userId) {
        log.info("{UserService.myPageDetailLoad}");

        User user = userRepository.findByUserIdWithDesireResidenceAndTransactionPriceConfig(userId);

        return GetMyPageDetailResponse.from(user);
    }

    @Transactional
    public GetKokOptionLoadResponse loadKokOption(long userId) {
        log.info("{UserService.loadKokOption}");

        List<Highlight> highlightList = highlightRepository.findAllByUserId(userId);
        List<Option> optionList = optionRepository.findAllByUserIdWithDetailOption(userId);

        return GetKokOptionLoadResponse.of(highlightList, optionList);
    }

    @Transactional
    public Object updateKokOption(long userId, PostUpdateKokOptionRequest postUpdateKokOptionRequest) {
        log.info("{UserService.updateKokOption}");

        updateHighlightList(userId, postUpdateKokOptionRequest);
        updateOption(userId, postUpdateKokOptionRequest);

        return null;
    }

    private void updateHighlightList(Long userId, PostUpdateKokOptionRequest postUpdateKokOptionRequest) {

        highlightBulkJdbcRepository.deleteAll(userId);

        User user = userRepository.findByUserId(userId);

        List<Highlight> highlightList = postUpdateKokOptionRequest.getHighlights().stream()
                .map(title -> Highlight.of(title, user))
                .toList();

        highlightBulkJdbcRepository.saveAll(highlightList);
    }

    private void updateOption(Long userId, PostUpdateKokOptionRequest postUpdateKokOptionRequest) {

        List<PostUpdateKokOptionRequest.Option> requestOptions = Stream.of(
                        postUpdateKokOptionRequest.getOuterOptions(),
                        postUpdateKokOptionRequest.getInnerOptions(),
                        postUpdateKokOptionRequest.getContractOptions()
                )
                .flatMap(Collection::stream)
                .toList();

        optionBulkJdbcRepository.updateAll(requestOptions);

        updateDetailOption(requestOptions);
    }

    private void updateDetailOption(List<PostUpdateKokOptionRequest.Option> requestOptionIds) {
        List<PostUpdateKokOptionRequest.DetailOption> requestDetailOptionList =
                requestOptionIds.stream()
                .map(PostUpdateKokOptionRequest.Option::getDetailOptions)
                .flatMap(Collection::stream)
                .toList();

        detailOptionBulkJdbcRepository.updateAll(requestDetailOptionList);
    }

    @Transactional
    public Object logout(long userId) {
        log.info("{UserService.logout}");

        try{
            User user = this.userRepository.findByUserId(userId);

            //user table status를 disable로 설정
            user.setStatus("disable");

            //redis에 user Refresh token 삭제
            this.redisService.deleteValues(user.getEmail());

            this.userRepository.save(user);

        }catch(Exception e){
            throw new UserBadRequestException(LOGOUT_FAIL);
        }

        return null;
    }

    @Transactional
    public Object signout(long userId) {
        log.info("{UserService.signout}");

        try {
            User user = this.userRepository.findByUserId(userId);

            //redis에 user Refresh token 삭제
            this.redisService.deleteValues(user.getEmail());

            this.fileUploadUtils.deleteFile(user.getProfileImgUrl());

            this.userRepository.delete(user);

        } catch (Exception e) {
            throw new UserBadRequestException(DEREGISTRATION_FAIL);
        }

        return null;
    }

    @Transactional
    public Object deregister(long userId) {
        log.info("[UserService.deregister]");

        try {
            User user = this.userRepository.findByUserId(userId);

            this.redisService.deleteValues(user.getEmail());

            String updatedImgUrl = null;
            if(user.getProfileImgUrl() != null) {
                updatedImgUrl = this.fileUploadUtils.updateFileDir(extractKeyFromUrl(user.getProfileImgUrl()), "pending/");
            }

            if(!user.getKoks().isEmpty()) {
                user.getKoks()
                        .stream()
                        .forEach(kok -> {
                                    log.info("가져온 콕 아이디" + String.valueOf(kok.getKokId()));
                                    log.info("첫번째 콕이미지 url" + kok.getKokImages().get(0).getImageUrl());
                                    kok.getKokImages()
                                            .stream()
                                            .forEach(kokImage -> {
                                                log.info(kokImage.getImageUrl());
                                                try {
                                                    kokImage.setImageUrl(this.fileUploadUtils.updateFileDir(extractKeyFromUrl(kokImage.getImageUrl()), "pending/"));

                                                    log.info(kokImage.getImageUrl());
                                                } catch (IOException e) {
                                                    log.error(e.getMessage());
                                                    throw new RuntimeException(e);
                                                }
                                                kokImageRepository.save(kokImage);

                                            });
                                }
                        );
            }



            user.setProfileImgUrl(updatedImgUrl);
            user.setStatus("pending");

            userRepository.save(user);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UserBadRequestException(DEREGISTRATION_FAIL);
        }

        return null;
    }

    @Transactional
    public Object deregisterV2(long userId) {

        log.info("[UserService.deregisterV2");

        try {
            User user = this.userRepository.findByUserId(userId);

            this.redisService.deleteValues(user.getEmail());

            if(user.getProfileImgUrl() != null) {
                this.fileUploadUtils.deleteFile(extractKeyFromUrl(user.getProfileImgUrl()));
            }

            if(!user.getKoks().isEmpty()) {
                user.getKoks()
                        .stream()
                        .forEach(kok -> {
                                    kok.getKokImages()
                                            .stream()
                                            .forEach(kokImage -> {
                                                this.fileUploadUtils.deleteFile(extractKeyFromUrl(kokImage.getImageUrl()));
                                            });
                                }
                        );
            }

            userRepository.delete(user);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UserBadRequestException(DEREGISTRATION_FAIL);
        }

        return null;
    }

    @Transactional
    public Object updateMyInfo(long userId, MultipartFile file, PutUpdateMyInfoRequest putUpdateMyInfoRequest) {
        log.info("{UserService.updateMyInfo}");

        User user = this.userRepository.findByUserIdWithDesireResidenceAndTransactionPriceConfig(userId);

        String imageUrl = settingProfileImage(userId, file);

        user.setUpdateUserInfo(imageUrl, putUpdateMyInfoRequest);

        this.userRepository.save(user);
        return null;
    }

    private String settingProfileImage(Long userId, MultipartFile file){

        String url = null;
        if(file != null) {
            url = this.fileUploadUtils.uploadFile(userId.toString() + "/profile", file);

            if(url == null){
                throw new FileUploadException(CANNOT_SAVE_FILE);
            }
        }
        return url;
    }

    @Transactional
    public Object updateFilter(long userId, PatchUpdateFilterRequest patchUpdateFilterRequest) {
        log.info("{UserService.updateFilter}");

        User user = this.userRepository.findByUserId(userId);

        user.setTransactionType(patchUpdateFilterRequest.getTransactionType());
        user.setRealEstateType(patchUpdateFilterRequest.getRealEstateType());

        String filterInfo = patchUpdateFilterRequest.getTransactionType().getDescription();

        if(filterInfo.equals("월세")){
            user.getTransactionPriceConfig().setMPriceMin(patchUpdateFilterRequest.getPriceMin());
            user.getTransactionPriceConfig().setMPriceMax(patchUpdateFilterRequest.getPriceMax());
            user.getTransactionPriceConfig().setMDepositMin(patchUpdateFilterRequest.getDepositMin());
            user.getTransactionPriceConfig().setMDepositMax(patchUpdateFilterRequest.getDepositMax());
        }else if(filterInfo.equals("전세")){
            user.getTransactionPriceConfig().setYDepositMin(patchUpdateFilterRequest.getDepositMin());
            user.getTransactionPriceConfig().setYDepositMax(patchUpdateFilterRequest.getDepositMax());
        }else if(filterInfo.equals("매매")){
            user.getTransactionPriceConfig().setPurchaseMin(patchUpdateFilterRequest.getPriceMin());
            user.getTransactionPriceConfig().setPurchaseMax(patchUpdateFilterRequest.getPriceMax());
        }

        this.userRepository.save(user);

        return null;
    }

    public static String extractKeyFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            String key = url.getPath().substring(1);
            String returnKey = URLDecoder.decode(key, StandardCharsets.UTF_8.name());
            log.info(returnKey);
            return returnKey;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String removePendingDirPart(String inputString) {
        String prefixToRemove = "pending/";
        if (inputString.startsWith(prefixToRemove)) {
            return inputString.substring(prefixToRemove.length());
        }
        return inputString;
    }




}
