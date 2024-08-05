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

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DesireResidenceRepository desireResidenceRepository;
    private final TransactionPriceConfigRepository transactionPriceConfigRepository;
    private final HighlightRepository highlightRepository;
    private final OptionRepository optionRepository;
    private final DetailOptionRepository detailOptionRepository;
    private final KokImageRepository kokImageRepository;

    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final FileUploadUtils fileUploadUtils;

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

        User user = User.from(postSignUpRequest);

        return userRepository.save(user);
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

        User user = this.userRepository.findByUserIdWithDesireResidenceAndTransactionPriceConfig(userId);

        user.setOnBoardingInfo(patchOnBoardingRequest);

        this.userRepository.save(user);

        return null;
    }

    public GetMyPageResponse myPageLoad(long userId) {
        log.info("{UserService.myPageLoad}");

        User user = this.userRepository.findByUserIdWithDesireResidenceAndTransactionPriceConfig(userId);

        return GetMyPageResponse.from(user);
    }

    public GetMyPageDetailResponse myPageDetailLoad(long userId) {
        log.info("{UserService.myPageDetailLoad}");

        User user = this.userRepository.findByUserIdWithDesireResidenceAndTransactionPriceConfig(userId);

        return GetMyPageDetailResponse.from(user);
    }

    @Transactional
    public GetKokOptionLoadResponse loadKokOption(long userId) {
        log.info("{UserService.kokOptionLoad}");

        //model 객체 호출
        User user = this.userRepository.findByUserId(userId);
        List<Highlight> highlightList = this.highlightRepository.findAllByUser(user);
        List<Option> optionList = this.optionRepository.findAllByUser(user);

        //exception 처리
        if(highlightList == null || optionList == null){
            throw new KokOptionLoadException(MEMBER_LIST_ITEM_QUERY_FAILURE);
        }

        GetKokOptionLoadResponse getKokOptionLoadResponse = new GetKokOptionLoadResponse();

        //dto에 highlight 정보 삽입
        for(Highlight highlight : highlightList){
            getKokOptionLoadResponse.addHighlight(highlight.getTitle());
        }

        //dto에 option 정보 삽입
        List<GetKokOptionLoadResponse.Option> outerOptions = new ArrayList<>();
        List<GetKokOptionLoadResponse.Option> innerOptions = new ArrayList<>();
        List<GetKokOptionLoadResponse.Option> contractOptions = new ArrayList<>();

        for(Option option : optionList){
            
            //dto에 detailOption 정보 삽입
            List<DetailOption> detailOptionList = this.detailOptionRepository.findAllByOption(option);

            List<GetKokOptionLoadResponse.DetailOption> detailOptionList1 = new ArrayList<>();

            for(DetailOption detailOption : detailOptionList){
                detailOptionList1.add(new GetKokOptionLoadResponse.DetailOption(detailOption.getDetailOptionId(), detailOption.getName(), detailOption.isVisible()));
            }

            //for문 해당 option이 outerOption 인지, innerOption 인지, contractOption 인지 판단 --> dto에 삽입
            if(option.getCategory().equals(OptionCategory.OUTER)){
                outerOptions.add(new GetKokOptionLoadResponse.Option(option.getOptionId(), option.getName(), option.getOrderNum(), option.isVisible(), detailOptionList1));
            }
            else if(option.getCategory().equals(OptionCategory.INNER)){
                innerOptions.add(new GetKokOptionLoadResponse.Option(option.getOptionId(), option.getName(), option.getOrderNum(), option.isVisible(), detailOptionList1));
            }
            else if(option.getCategory().equals(OptionCategory.CONTRACT)){
                contractOptions.add(new GetKokOptionLoadResponse.Option(option.getOptionId(), option.getName(), option.getOrderNum(), option.isVisible(), detailOptionList1));
            }
        }

        getKokOptionLoadResponse.setOuterOptions(outerOptions);
        getKokOptionLoadResponse.setInnerOptions(innerOptions);
        getKokOptionLoadResponse.setContractOptions(contractOptions);

        return getKokOptionLoadResponse;
    }

    @Transactional
    public Object updateKokOption(long userId, PostUpdateKokOptionRequest postUpdateKokOptionRequest) {
        log.info("{UserService.updateKokOption}");

        //model 객체 호출
        User user = this.userRepository.findByUserId(userId);
        List<Highlight> newHighlightList = this.highlightRepository.findAllByUser(user);

        //exception 처리
        if(newHighlightList == null){
            throw new KokOptionLoadException(MEMBER_LIST_ITEM_UPDATE_FAILURE);
        }


        //기존 Highlight 객체 삭제=========================================================================
        this.highlightRepository.deleteAll(newHighlightList);

        //새로운 highlight list 생성하기
        newHighlightList.clear();
        for(String highlightTitle : postUpdateKokOptionRequest.getHighlights()){
            newHighlightList.add(new Highlight(highlightTitle, user));
        }

        //user 객체에 highlight list 바꾸기
        this.highlightRepository.saveAllAndFlush(newHighlightList);


        //option, detailOption 객체 수정 (outer) ====================================================================
        for(PostUpdateKokOptionRequest.Option requestOption : postUpdateKokOptionRequest.getOuterOptions()){

            Option option = this.optionRepository.findByOptionId(requestOption.getOptionId());

            option.setOrderNum(requestOption.getOrderNumber());
            option.setVisible(requestOption.isVisible());

            for(PostUpdateKokOptionRequest.DetailOption requestDetailOption : requestOption.getDetailOptions()){
                DetailOption detailOption = this.detailOptionRepository.findByDetailOptionId(requestDetailOption.getDetailOptionId());

                detailOption.setVisible(requestDetailOption.isDetailOptionIsVisible());
                this.detailOptionRepository.save(detailOption);
            }
            this.optionRepository.save(option);
        }

        //option, detailOption 객체 수정 (inner) ====================================================================
        for(PostUpdateKokOptionRequest.Option requestOption : postUpdateKokOptionRequest.getInnerOptions()){

            Option option = this.optionRepository.findByOptionId(requestOption.getOptionId());

            option.setOrderNum(requestOption.getOrderNumber());
            option.setVisible(requestOption.isVisible());

            for(PostUpdateKokOptionRequest.DetailOption requestDetailOption : requestOption.getDetailOptions()){
                DetailOption detailOption = this.detailOptionRepository.findByDetailOptionId(requestDetailOption.getDetailOptionId());

                detailOption.setVisible(requestDetailOption.isDetailOptionIsVisible());
                this.detailOptionRepository.save(detailOption);
            }
            this.optionRepository.save(option);
        }

        //option, detailOption 객체 수정 (contract) ====================================================================
        for(PostUpdateKokOptionRequest.Option requestOption : postUpdateKokOptionRequest.getContractOptions()){

            Option option = this.optionRepository.findByOptionId(requestOption.getOptionId());

            option.setOrderNum(requestOption.getOrderNumber());
            option.setVisible(requestOption.isVisible());

            for(PostUpdateKokOptionRequest.DetailOption requestDetailOption : requestOption.getDetailOptions()){
                DetailOption detailOption = this.detailOptionRepository.findByDetailOptionId(requestDetailOption.getDetailOptionId());

                detailOption.setVisible(requestDetailOption.isDetailOptionIsVisible());
                this.detailOptionRepository.save(detailOption);
            }
            this.optionRepository.save(option);
        }

        return null;

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
