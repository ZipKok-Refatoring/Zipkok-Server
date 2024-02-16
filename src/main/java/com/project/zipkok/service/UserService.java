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

        String email = postSignUpRequest.getEmail();
        OAuthProvider oAuthProvider = postSignUpRequest.getOauthProvider();
        String nickname = postSignUpRequest.getNickname();
        Gender gender = postSignUpRequest.getGender();
        String birthday = postSignUpRequest.getBirthday();

        User user = new User(email, oAuthProvider, nickname, gender,birthday);

        //user 생성하면서 연관된 table 열도 생성
        user.setDesireResidence(new DesireResidence(user));
        user.setTransactionPriceConfig(new TransactionPriceConfig(user));
        user.setHighlights(this.makeDefaultHighlights(user));
        user.setOptions(this.makeDefaultOptions(user));
        user.setImpressions(this.makeDefaultImpressions(user));

        this.userRepository.save(user);

        long userId = this.userRepository.findByEmail(email).getUserId();

        //token 발행
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

        user.setRealEstateType(realEstateType);
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
        getMyPageResponse.setRealEstateType(user.getRealEstateType() == null ? null : user.getRealEstateType().toString());

        getMyPageResponse.setAddress(this.desireResidenceRepository.findByUser(user).getAddress());

        String transactionType = null;

        //관심매물유형에 따라 dto field 값 set 작업 분기처리
        if(user.getTransactionType() == null){
            getMyPageResponse.setTransactionType(null);
            getMyPageResponse.setPriceMax(null);
            getMyPageResponse.setPriceMin(null);
            getMyPageResponse.setDepositMax(null);
            getMyPageResponse.setDepositMin(null);
        }
        else{
            getMyPageResponse.setTransactionType(user.getTransactionType().toString());
            transactionType = user.getTransactionType().getDescription();
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
        getMyPageDetailResponse.setRealEstateType(user.getRealEstateType() == null ? null : user.getRealEstateType().toString());

        if (user.getTransactionType() == null) {
            getMyPageDetailResponse.setTransactionType(null);
        } else {
            getMyPageDetailResponse.setTransactionType(user.getTransactionType().toString());
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

    private List<Highlight> makeDefaultHighlights(User user){
        List<String> highlightNames = List.of("CCTV", "주변공원", "현관보안", "편세권", "주차장", "역세권", "더블역세권", "트리플역세권");

        List<Highlight> defaultHighlights = new ArrayList<>();

        for(String highlightTitle : highlightNames){
            defaultHighlights.add(new Highlight(highlightTitle, user));
        }

        return defaultHighlights;
    }

    private List<Option> makeDefaultOptions(User user){

        Map<String, List<String>> outerOptions = new LinkedHashMap<String, List<String>>();

        outerOptions.put("편의성", List.of("학교/직장과 가깝나요?", "편의점이 근처에 있나요?", "대형마트가 근처에 있나요?", "엘리베이터가 있나요?", "무인 택배 보관함이 있나요?", "전용 분리수거장이 있나요?", "주차할 수 있는 주차장 공간이 있나요?"));
        outerOptions.put("접근성", List.of("언덕과 오르막길이 있나요?", "골목길이 많나요?", "가로등이 충분히 있나요?", "지하철역과 가깝나요?"));
        outerOptions.put("보안", List.of("공동현관이 있나요?", "거주 전용 CCTV가 있나요?", "주변에 술집/유흥시설이 있나요?", "관리자/관리시설이 있나요?"));
        outerOptions.put("디테일", List.of("1층에 음식점이 없나요?", "지어진지 얼마 안 된 신축인가요?", "한 층에 거주하는 세대가 많아요?"));

        Map<String, List<String>> innerOptions = new LinkedHashMap<String, List<String>>();

        innerOptions.put("현관/보안", List.of("별도의 현관문 잠금장치가 있나요?", "외부를 볼 수 있는 인터폰이 있나요?", "공간을 분리하는 현관 문턱이 있나요?", "로비/복도에 CCTV가 있나요?"));
        innerOptions.put("부엌", List.of("싱크대 수압이 충분한가요?", "화구(ex, 가스레인지)의 화력이 충분한가요?", "냉장고의 크기가 충분한가요?", "싱크대의 크기가 충분한가요?", "물을 틀었을 때, 싱크대 아래에 물이 새나요?", "싱크대 아래에 곰팡이 흔적이 있나요?", "수납장/냉장고 뒤에 바퀴벌레 약이 있나요?"));
        innerOptions.put("화장실", List.of("화장실에 창문이 있나요?", "변기 물을 내렸을 때 잘 내려가나요?", "세면대 물 수압이 충분한가요?", "선반과 휴지걸이에 녹이 슬어있나요?", "타일 사이에 때가 껴있나요?", "배수구에서 불쾌한 냄새가 나는가요?", "온수가 금방 나오나요?", "환풍기가 정상적으로 작동하나요?"));
        innerOptions.put("방/거실", List.of("침대 놓을 공간이 충분한가요?", "벽지에 곰팡이 흔적이 있나요?", "곰팡이 냄새, 불쾌한 냄새가 나나요?", "장판에 긁힘, 찍힘 자국이 있나요?", "벽을 두드렸을 때 가벽인 것 같나요?"));
        innerOptions.put("채광/창문/환기", List.of("방충망이 있나요?", "앞 건물에서 우리 집이 보이나요?", "앞 건물에 가려져 그늘이 지나요?", "햇빛이 잘 들어오나요?", "창문의 크기가 충분히 큰가요?", "창문에 결로현상(물맺힘)이 있나요?", "창문에서 찬바람, 찬기가 느껴지나요?"));
        innerOptions.put("옵션 상태 확인", List.of("세탁기 상태가 깨끗한가요?", "에어컨은 잘 작동하나요?", "냉장고에 성에가 끼어있나요?", "인덕션/가스레인지는 잘 작동하나요?"));
        innerOptions.put("디테일", List.of("콘센트 개수가 충분한가요?", "인터넷 선 위치가 적절한가요?", "난방/냉방이 중앙난방인가요?", "인터넷 통신망이 뚫려있나요?", "빨래 건조할 공간이 충분한가요?"));

        Map<String, List<String>> contractOptions = new LinkedHashMap<String, List<String>>();

        contractOptions.put("집주인/매물 관련 질문체크", List.of("집주인이 건물에 함께 상주하나요?", "관리비 포함 여부를 질문했나요?", "새로 벽지 도배가 가능한가요?", "장판 교체가 가능한가요?", "하자있는 부분 보수해주시나요?", "입주 청소해주시나요?", "반려동물 가능한가요?"));
        contractOptions.put("보증금/월세 관련 질문체크", List.of("보증금 대출 가능한가요?", "보증 보험 가입이 되나요?", "관리비에 어떤 것이 포함되어 있나요?", "공과금은 보통 얼마정도 나오나요?"));
        contractOptions.put("계약 관련 질문 체크", List.of("중개 수수료는 얼마인가요?", "전입신고가 가능한가요?", "이사 전날까지 수도/전기요금 정산됐나요?", "집주인의 빚(근저당)이 있나요?", "계약하는 분이 집주인이 맞나요?", "특약사항 기재가 가능한가요?"));


        List<Option> defaultOptions = new ArrayList<>();

        int orderNumber =1;
        for(String optionName : outerOptions.keySet()){
            Option option = new Option(optionName, true, orderNumber++, OptionCategory.OUTER, user);
            for(String detailOptionTitle : outerOptions.get(optionName)){
                DetailOption detailOption = new DetailOption(detailOptionTitle, true, option);
                option.addDetailOption(detailOption);
            }
            defaultOptions.add(option);
        }
        orderNumber =1;
        for(String optionName : innerOptions.keySet()){
            Option option = new Option(optionName, true, orderNumber++, OptionCategory.INNER, user);
            for(String detailOptionTitle : innerOptions.get(optionName)){
                DetailOption detailOption = new DetailOption(detailOptionTitle, true, option);
                option.addDetailOption(detailOption);
            }
            defaultOptions.add(option);
        }
        orderNumber =1;
        for(String optionName : contractOptions.keySet()){
            Option option = new Option(optionName, true, orderNumber++, OptionCategory.CONTRACT, user);
            for(String detailOptionTitle : contractOptions.get(optionName)){
                DetailOption detailOption = new DetailOption(detailOptionTitle, true, option);
                option.addDetailOption(detailOption);
            }
            defaultOptions.add(option);
        }
        
        return defaultOptions;
    }

    private List<Impression> makeDefaultImpressions(User user) {
        List<String> impressionNames = List.of("깔끔해요", "조용해요", "세련돼요", "심플해요", "더러워요", "냄새나요", "시끄러워요", "좁아요", "그냥 그래요", "쌈@뽕 해요", "별로예요");

        List<Impression> defaultImpressions = new ArrayList<>();

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

            String updatedImgUrl = this.fileUploadUtils.updateFileDir(extractKeyFromUrl(user.getProfileImgUrl()), "pending/");

            user.setProfileImgUrl(updatedImgUrl);
            user.setStatus("pending");

            userRepository.save(user);
        } catch (Exception e) {
            throw new UserBadRequestException(DEREGISTRATION_FAIL);
        }

        return null;
    }

    @Transactional
    public Object updateMyInfo(long userId, MultipartFile file, PutUpdateMyInfoRequest putUpdateMyInfoRequest) {
        log.info("{UserService.updateMyInfo}");

        User user = this.userRepository.findByUserId(userId);

        if(file != null) {
            String url = this.fileUploadUtils.uploadFile(file);

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

    public static String extractLastPartFromKey(String inputString) {
        int lastIndex = inputString.lastIndexOf('/');

        if (lastIndex != -1) {
            return inputString.substring(lastIndex + 1);
        } else {
            return inputString;
        }
    }




}
