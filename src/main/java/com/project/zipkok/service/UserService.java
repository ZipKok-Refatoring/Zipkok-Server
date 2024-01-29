package com.project.zipkok.service;

import com.project.zipkok.common.argument_resolver.PreAuthorize;
import com.project.zipkok.common.enums.Gender;
import com.project.zipkok.common.enums.OAuthProvider;
import com.project.zipkok.common.enums.RealEstateType;
import com.project.zipkok.common.exception.user.KokOptionLoadException;
import com.project.zipkok.common.exception.user.OnBoardingBadRequestException;
import com.project.zipkok.common.service.RedisService;
import com.project.zipkok.config.RedisConfig;
import com.project.zipkok.dto.*;
import com.project.zipkok.model.*;
import com.project.zipkok.repository.*;
import com.project.zipkok.util.jwt.AuthTokens;
import com.project.zipkok.util.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

        //user 생성하면서 연관된 table 열도 생성
        user.setDesireResidence(new DesireResidence(user));
        user.setTransactionPriceConfig(new TransactionPriceConfig(user));
        user.setHighlights(this.makeDefaultHighlights(user));
        user.setOptions(this.makeDefaultOptions(user));

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
            if(option.getCategory().equals("outerOption")){
                outerOptions.add(new GetKokOptionLoadResponse.Option(option.getOptionId(), option.getName(), option.getOrderNum(), option.isVisible(), detailOptionList1));
            }
            else if(option.getCategory().equals("innerOption")){
                innerOptions.add(new GetKokOptionLoadResponse.Option(option.getOptionId(), option.getName(), option.getOrderNum(), option.isVisible(), detailOptionList1));
            }
            else if(option.getCategory().equals("contractOption")){
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
        Map<String, List<String>> outerOptions = Map.of(
                "편의성", List.of("학교/직장과 가깝나요?", "편의점이 근처에 있나요?", "대형마트가 근처에 있나요?", "엘리베이터가 있나요?", "무인 택배 보관함이 있나요?", "전용 분리수거장이 있나요?", "주차할 수 있는 주차장 공간이 있나요?"),
                "접근성", List.of("언덕과 오르막길이 있나요?", "골목길이 많나요?", "가로등이 충분히 있나요?", "지하철역과 가깝나요?"),
                "보안", List.of("공동현관이 있나요?", "거주 전용 CCTV가 있나요?", "주변에 술집/유흥시설이 있나요?", "관리자/관리시설이 있나요?"),
                "디테일", List.of("1층에 음식점이 없나요?", "지어진지 얼마 안 된 신축인가요?", "한 층에 거주하는 세대가 많아요?")
        );
        Map<String, List<String>> innerOptions = Map.of(
                  "현관/보안", List.of("별도의 현관문 잠금장치가 있나요?", "외부를 볼 수 있는 인터폰이 있나요?", "공간을 분리하는 현관 문턱이 있나요?", "로비/복도에 CCTV가 있나요?"),
                "부엌", List.of("싱크대 수압이 충분한가요?", "화구(ex, 가스레인지)의 화력이 충분한가요?", "냉장고의 크기가 충분한가요?", "싱크대의 크기가 충분한가요?", "물을 틀었을 때, 싱크대 아래에 물이 새나요?", "싱크대 아래에 곰팡이 흔적이 있나요?", "수납장/냉장고 뒤에 바퀴벌레 약이 있나요?"),
                "화장실", List.of("화장실에 창문이 있나요?", "변기 물을 내렸을 때 잘 내려가나요?", "세면대 물 수압이 충분한가요?", "선반과 휴지걸이에 녹이 슬어있나요?", "타일 사이에 때가 껴있나요?", "배수구에서 불쾌한 냄새가 나는가요?", "온수가 금방 나오나요?", "환풍기가 정상적으로 작동하나요?"),
                "방/거실", List.of("침대 놓을 공간이 충분한가요?", "벽지에 곰팡이 흔적이 있나요?", "곰팡이 냄새, 불쾌한 냄새가 나나요?", "장판에 긁힘, 찍힘 자국이 있나요?", "벽을 두드렸을 때 가벽인 것 같나요?"),
                "채광/창문/환기", List.of("방충망이 있나요?", "앞 건물에서 우리 집이 보이나요?", "앞 건물에 가려져 그늘이 지나요?", "햇빛이 잘 들어오나요?", "창문의 크기가 충분히 큰가요?", "창문에 결로현상(물맺힘)이 있나요?", "창문에서 찬바람, 찬기가 느껴지나요?"),
                "옵션 상태 확인", List.of("세탁기 상태가 깨끗한가요?", "에어컨은 잘 작동하나요?", "냉장고에 성에가 끼어있나요?", "인덕션/가스레인지는 잘 작동하나요?"),
                "디테일", List.of("콘센트 개수가 충분한가요?", "인터넷 선 위치가 적절한가요?", "난방/냉방이 중앙난방인가요?", "인터넷 통신망이 뚫려있나요?", "빨래 건조할 공간이 충분한가요?")
        );
        Map<String, List<String>> contractOptions = Map.of(
                "집주인/매물 관련 질문체크", List.of("집주인이 건물에 함께 상주하나요?", "관리비 포함 여부를 질문했나요?", "새로 벽지 도배가 가능한가요?", "장판 교체가 가능한가요?", "하자있는 부분 보수해주시나요?", "입주 청소해주시나요?", "반려동물 가능한가요?"),
                "보증금/월세 관련 질문체크", List.of("보증금 대출 가능한가요?", "보증 보험 가입이 되나요?", "관리비에 어떤 것이 포함되어 있나요?", "공과금은 보통 얼마정도 나오나요?"),
                "계약 관련 질문 체크", List.of("중개 수수료는 얼마인가요?", "전입신고가 가능한가요?", "이사 전날까지 수도/전기요금 정산됐나요?", "집주인의 빚(근저당)이 있나요?", "계약하는 분이 집주인이 맞나요?", "특약사항 기재가 가능한가요?")

        );

        List<Option> defaultOptions = new ArrayList<>();

        int orderNumber =1;
        for(String optionName : outerOptions.keySet()){
            Option option = new Option(optionName, true, orderNumber++, "outerOption", user);
            for(String detailOptionTitle : outerOptions.get(optionName)){
                DetailOption detailOption = new DetailOption(detailOptionTitle, true, option);
                option.addDetailOption(detailOption);
            }
            defaultOptions.add(option);
        }
        orderNumber =1;
        for(String optionName : innerOptions.keySet()){
            Option option = new Option(optionName, true, orderNumber++, "innerOption", user);
            for(String detailOptionTitle : innerOptions.get(optionName)){
                DetailOption detailOption = new DetailOption(detailOptionTitle, true, option);
                option.addDetailOption(detailOption);
            }
            defaultOptions.add(option);
        }
        orderNumber =1;
        for(String optionName : contractOptions.keySet()){
            Option option = new Option(optionName, true, orderNumber++, "contractOption", user);
            for(String detailOptionTitle : contractOptions.get(optionName)){
                DetailOption detailOption = new DetailOption(detailOptionTitle, true, option);
                option.addDetailOption(detailOption);
            }
            defaultOptions.add(option);
        }
        
        return defaultOptions;
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
}
