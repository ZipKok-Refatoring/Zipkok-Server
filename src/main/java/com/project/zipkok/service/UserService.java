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
    private final ImpressionRepository impressionRepository;
    private final HighLightBulkJdbcRepository highlightBulkJdbcRepository;
    private final ImpressionBulkJdbcRepository impressionBulkJdbcRepository;
    private final OptionBulkJdbcRepository optionBulkJdbcRepository;
    private final DetailOptionBulkJdbcRepository detailOptionBulkJdbcRepository;

    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final FileUploadUtils fileUploadUtils;

    @Transactional
    public List<GetUserResponse> getUsers() {
        List<GetUserResponse> userList = userRepository.findAll()
                .stream()
                .map(user -> new GetUserResponse(
                        user.getNickname(),
                        user.getProfileImgUrl(),
                        user.getDesireResidence().getAddress(),
                        user.getRealEstateType().toString(),
                        user.getTransactionType().toString(),
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

        User user = postSignUpRequest.toEntity();

        this.userRepository.save(user);

        DesireResidence desireResidence = new DesireResidence(user);
        TransactionPriceConfig transactionPriceConfig = new TransactionPriceConfig(user);
        Set<Highlight> highlights = this.makeDefaultHighlights(user);
        List<Option> options = this.makeDefaultOptions(user);
        Set<Impression> impressions = this.makeDefaultImpressions(user);

        desireResidenceRepository.save(desireResidence);
        transactionPriceConfigRepository.save(transactionPriceConfig);
        List<Highlight> highlightList = highlights.stream().toList();
        highlightBulkJdbcRepository.saveAll(highlightList);
        optionBulkJdbcRepository.saveAll(options);
        List<DetailOption> detailOptions = this.makeDefaultDetailOptions(options);
        detailOptionBulkJdbcRepository.saveAll(detailOptions);

        List<Impression> impressionList = impressions.stream().toList();
        impressionBulkJdbcRepository.saveAll(impressionList);

        long userId = this.userRepository.findByEmail(user.getEmail()).getUserId();

        //token 발행
        AuthTokens authTokens = jwtProvider.createToken(user.getEmail(), userId);

        redisService.setValues(user.getEmail(), authTokens.getRefreshToken(), Duration.ofMillis(jwtProvider.getREFRESH_TOKEN_EXPIRED_IN()));

        return authTokens;
    }

    @Transactional
    public Objects setOnBoarding(PatchOnBoardingRequest patchOnBoardingRequest, long userId) {
        log.info("{UserService.setOnBoarding}");

        String address = patchOnBoardingRequest.getAddress();
        double latitude = patchOnBoardingRequest.getLatitude();
        double longitude = patchOnBoardingRequest.getLongitude();
        RealEstateType realEstateType = patchOnBoardingRequest.getRealEstateType();
        TransactionType transactionType = patchOnBoardingRequest.getTransactionType();
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
        user.setRealEstateType(realEstateType);
        user.setTransactionType(transactionType);
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
        DesireResidence desireResidence = this.desireResidenceRepository.findByUser(user);

        return GetMyPageResponse.of(user, transactionPriceConfig, desireResidence);
    }

    public GetMyPageDetailResponse myPageDetailLoad(long userId) {
        log.info("{UserService.myPageDetailLoad}");

        User user = this.userRepository.findByUserId(userId);
        TransactionPriceConfig transactionPriceConfig = this.transactionPriceConfigRepository.findByUser(user);
        DesireResidence desireResidence = this.desireResidenceRepository.findByUser(user);

        return GetMyPageDetailResponse.of(user, transactionPriceConfig, desireResidence);
    }

    @Transactional
    public GetKokOptionLoadResponse loadKokOption(long userId) {
        log.info("{UserService.loadKokOption}");

        List<Highlight> highlightList = this.highlightRepository.findAllByUserId(userId);
        List<Option> optionList = this.optionRepository.findAllByUserId(userId);

        return GetKokOptionLoadResponse.of(highlightList, optionList);
    }

    private Set<Highlight> makeDefaultHighlights(User user){
        Set<String> highlightNames = Set.of("CCTV", "주변공원", "현관보안", "편세권", "주차장", "역세권", "더블역세권", "트리플역세권");

        Set<Highlight> defaultHighlights = new LinkedHashSet<>();

        for(String highlightTitle : highlightNames){
            defaultHighlights.add(new Highlight(highlightTitle, user));
        }

        return defaultHighlights;
    }

    private List<DetailOption> makeDefaultDetailOptions(List<Option> options){

        Map<Option, List<String>> detailOptions = new LinkedHashMap<Option, List<String>>();

        for(Option option : options){
            switch (option.getName()) {
                case "편의성":
                    detailOptions.put(option, List.of("학교/직장과 가깝나요?", "편의점이 근처에 있나요?", "대형마트가 근처에 있나요?", "엘리베이터가 있나요?", "무인 택배 보관함이 있나요?", "전용 분리수거장이 있나요?", "주차할 수 있는 주차장 공간이 있나요?"));
                    break;
                case "접근성":
                    detailOptions.put(option, List.of("언덕과 오르막길이 있나요?", "골목길이 많나요?", "가로등이 충분히 있나요?", "지하철역과 가깝나요?"));
                    break;
                case "보안":
                    detailOptions.put(option, List.of("공동현관이 있나요?", "거주 전용 CCTV가 있나요?", "주변에 술집/유흥시설이 있나요?", "관리자/관리시설이 있나요?"));
                    break;
                case "디테일":
                    if (option.getCategory().equals(OptionCategory.OUTER)) {
                        detailOptions.put(option, List.of("1층에 음식점이 없나요?", "지어진지 얼마 안 된 신축인가요?", "한 층에 거주하는 세대가 많아요?"));
                        break;
                    }
                    else if (option.getCategory().equals(OptionCategory.INNER)) {
                        detailOptions.put(option, List.of("콘센트 개수가 충분한가요?", "인터넷 선 위치가 적절한가요?", "난방/냉방이 중앙난방인가요?", "인터넷 통신망이 뚫려있나요?", "빨래 건조할 공간이 충분한가요?"));
                        break;
                    }
                    break;
                case "현관/보안":
                    detailOptions.put(option, List.of("별도의 현관문 잠금장치가 있나요?", "외부를 볼 수 있는 인터폰이 있나요?", "공간을 분리하는 현관 문턱이 있나요?", "로비/복도에 CCTV가 있나요?"));
                    break;
                case "부엌":
                    detailOptions.put(option, List.of("싱크대 수압이 충분한가요?", "화구(ex, 가스레인지)의 화력이 충분한가요?", "냉장고의 크기가 충분한가요?", "싱크대의 크기가 충분한가요?", "물을 틀었을 때, 싱크대 아래에 물이 새나요?", "싱크대 아래에 곰팡이 흔적이 있나요?", "수납장/냉장고 뒤에 바퀴벌레 약이 있나요?"));
                    break;
                case "화장실":
                    detailOptions.put(option, List.of("화장실에 창문이 있나요?", "변기 물을 내렸을 때 잘 내려가나요?", "세면대 물 수압이 충분한가요?", "선반과 휴지걸이에 녹이 슬어있나요?", "타일 사이에 때가 껴있나요?", "배수구에서 불쾌한 냄새가 나는가요?", "온수가 금방 나오나요?", "환풍기가 정상적으로 작동하나요?"));
                    break;
                case "방/거실":
                    detailOptions.put(option, List.of("침대 놓을 공간이 충분한가요?", "벽지에 곰팡이 흔적이 있나요?", "곰팡이 냄새, 불쾌한 냄새가 나나요?", "장판에 긁힘, 찍힘 자국이 있나요?", "벽을 두드렸을 때 가벽인 것 같나요?"));
                    break;
                case "채광/창문/환기":
                    detailOptions.put(option, List.of("방충망이 있나요?", "앞 건물에서 우리 집이 보이나요?", "앞 건물에 가려져 그늘이 지나요?", "햇빛이 잘 들어오나요?", "창문의 크기가 충분히 큰가요?", "창문에 결로현상(물맺힘)이 있나요?", "창문에서 찬바람, 찬기가 느껴지나요?"));
                    break;
                case "옵션 상태 확인":
                    detailOptions.put(option, List.of("세탁기 상태가 깨끗한가요?", "에어컨은 잘 작동하나요?", "냉장고에 성에가 끼어있나요?", "인덕션/가스레인지는 잘 작동하나요?"));
                    break;
                case "집주인/매물 관련 질문체크":
                    detailOptions.put(option, List.of("집주인이 건물에 함께 상주하나요?", "관리비 포함 여부를 질문했나요?", "새로 벽지 도배가 가능한가요?", "장판 교체가 가능한가요?", "하자있는 부분 보수해주시나요?", "입주 청소해주시나요?", "반려동물 가능한가요?"));
                    break;
                case "보증금/월세 관련 질문체크":
                    detailOptions.put(option, List.of("보증금 대출 가능한가요?", "보증 보험 가입이 되나요?", "관리비에 어떤 것이 포함되어 있나요?", "공과금은 보통 얼마정도 나오나요?"));
                    break;
                case "계약 관련 질문 체크":
                    detailOptions.put(option, List.of("중개 수수료는 얼마인가요?", "전입신고가 가능한가요?", "이사 전날까지 수도/전기요금 정산됐나요?", "집주인의 빚(근저당)이 있나요?", "계약하는 분이 집주인이 맞나요?", "특약사항 기재가 가능한가요?"));
                    break;

            }
        }

        return detailOptions.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(string -> new DetailOption(string, true, entry.getKey()))
                ).toList();

    }

    private List<Option> makeDefaultOptions(User user){

        List<String> outerOptions = new ArrayList<>();

        outerOptions.add("편의성");
        outerOptions.add("접근성");
        outerOptions.add("보안");
        outerOptions.add("디테일");

        List<String> innerOptions = new ArrayList<>();

        innerOptions.add("현관/보안");
        innerOptions.add("부엌");
        innerOptions.add("화장실");
        innerOptions.add("방/거실");
        innerOptions.add("채광/창문/환기");
        innerOptions.add("옵션 상태 확인");
        innerOptions.add("디테일");

        List<String> contractOptions = new ArrayList<>();

        contractOptions.add("집주인/매물 관련 질문체크");
        contractOptions.add("보증금/월세 관련 질문체크");
        contractOptions.add("계약 관련 질문 체크");

        List<Option> defaultOptions = new ArrayList<>();

        int orderNumber =1;
        for(String optionName : outerOptions) {
            Option option = new Option(optionName, true, orderNumber++, OptionCategory.OUTER, user);
            defaultOptions.add(option);
        }
        orderNumber =1;
        for(String optionName : innerOptions){
            Option option = new Option(optionName, true, orderNumber++, OptionCategory.INNER, user);
            defaultOptions.add(option);
        }
        orderNumber =1;
        for(String optionName : contractOptions){
            Option option = new Option(optionName, true, orderNumber++, OptionCategory.CONTRACT, user);
            defaultOptions.add(option);
        }

        return defaultOptions;
    }


    private Set<Impression> makeDefaultImpressions(User user) {
        List<String> impressionNames = List.of("깔끔해요", "조용해요", "세련돼요", "심플해요", "더러워요", "냄새나요", "시끄러워요", "좁아요", "그냥 그래요", "마음에 들어요", "별로예요");

        Set<Impression> defaultImpressions = new LinkedHashSet<>();

        for(String impressionName : impressionNames) {
            defaultImpressions.add(Impression.builder()
                    .user(user)
                    .impressionTitle(impressionName)
                    .build());
        }

        return defaultImpressions;
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

        User user = this.userRepository.findByUserId(userId);

        if(file != null) {
            String url = this.fileUploadUtils.uploadFile(user.getUserId().toString() + "/profile", file);

            if(url == null){
                throw new FileUploadException(CANNOT_SAVE_FILE);
            }

            user.setProfileImgUrl(url);
        }

        user.setNickname(putUpdateMyInfoRequest.getNickname());
        user.setBirthday(putUpdateMyInfoRequest.getBirthday());
        user.setGender(putUpdateMyInfoRequest.getGender());
        user.setRealEstateType(putUpdateMyInfoRequest.getRealEstateType());
        user.setTransactionType(putUpdateMyInfoRequest.getTransactionType());

        DesireResidence desireResidence = user.getDesireResidence();
        desireResidence.setAddress(putUpdateMyInfoRequest.getAddress());
        desireResidence.setLatitude(putUpdateMyInfoRequest.getLatitude());
        desireResidence.setLongitude(putUpdateMyInfoRequest.getLongitude());

        TransactionPriceConfig transactionPriceConfig = user.getTransactionPriceConfig();
        transactionPriceConfig.setMPriceMin(putUpdateMyInfoRequest.getMpriceMin());
        transactionPriceConfig.setMPriceMax(putUpdateMyInfoRequest.getMpriceMax());
        transactionPriceConfig.setMDepositMin(putUpdateMyInfoRequest.getMdepositMin());
        transactionPriceConfig.setMDepositMax(putUpdateMyInfoRequest.getMdepositMax());
        transactionPriceConfig.setYDepositMin(putUpdateMyInfoRequest.getYdepositMin());
        transactionPriceConfig.setYDepositMax(putUpdateMyInfoRequest.getYdepositMax());
        transactionPriceConfig.setPurchaseMin(putUpdateMyInfoRequest.getPurchaseMin());
        transactionPriceConfig.setPurchaseMax(putUpdateMyInfoRequest.getPurchaseMax());

        this.userRepository.save(user);
        return null;
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
