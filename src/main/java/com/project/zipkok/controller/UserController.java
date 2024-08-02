package com.project.zipkok.controller;

import com.project.zipkok.common.argument_resolver.PreAuthorize;
import com.project.zipkok.common.exception.user.OnBoardingBadRequestException;
import com.project.zipkok.common.exception.user.UserBadRequestException;
import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.dto.*;
import com.project.zipkok.service.UserService;
import com.project.zipkok.util.jwt.AuthTokens;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.*;
import static com.project.zipkok.util.BindingResultUtils.getErrorMessages;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User API", description = "회원 관련 API")
public class UserController {
    private final UserService userService;

    //private final FileUploadUtils fileUploadUtils;

    @Operation(summary = "회원가입 API", description = "온보딩 전 회원정보를 입력하는 API입니다.")
    @PostMapping("")
    public BaseResponse<AuthTokens> signUp(@Validated @RequestBody PostSignUpRequest postSignUpRequest, BindingResult bindingResult) {
        log.info("{UserController.signUp}");

        if(bindingResult.hasFieldErrors("nickname")){
            throw new UserBadRequestException(INVALID_NICKNAME_FORMAT);
        }
        if(bindingResult.hasFieldErrors("gender")){
            throw new UserBadRequestException(INVALID_GENDER_FORMAT);
        }
        if(bindingResult.hasFieldErrors("birthday")){
            throw new UserBadRequestException(INVALID_BIRTHDAY_FORMAT);
        }
        if(bindingResult.hasFieldErrors("email")){
            throw new UserBadRequestException(INVALID_EMAIL_REQUEST);
        }
        if(bindingResult.hasErrors()){
            throw new UserBadRequestException(BAD_REQUEST);
        }

        return new BaseResponse<>(REGISTRATION_SUCCESS, this.userService.signUp(postSignUpRequest));
    }

    @Operation(summary = "온보딩정보 입력 API", description = "회원가입 후, 온보딩 정보를 입력하는 API입니다.")
    @PatchMapping("")
    public BaseResponse<Object> onBoarding(@Parameter(hidden=true) @PreAuthorize long userId, @Validated @RequestBody PatchOnBoardingRequest patchOnBoardingRequest, BindingResult bindingResult){
        log.info("{UserController.onBoarding}");
        System.out.println(patchOnBoardingRequest.toString());
        if(bindingResult.hasFieldErrors("address")){
            throw new OnBoardingBadRequestException(ADDRESS_OVER_LENGTH);
        }
        if(bindingResult.hasFieldErrors("latitude") || bindingResult.hasFieldErrors("longitude")){
            throw new OnBoardingBadRequestException(INVALID_LAT_LNG);
        }
        if(bindingResult.hasFieldErrors("mpriceMin") ||
            bindingResult.hasFieldErrors("mdepositMin") ||
            bindingResult.hasFieldErrors("ydepositMin") ||
            bindingResult.hasFieldErrors("purchaseMin")){
            throw  new OnBoardingBadRequestException(INVALID_MIN_PRICE);
        }
        if(bindingResult.hasFieldErrors("mpriceMax") ||
            bindingResult.hasFieldErrors("mdepositMax") ||
            bindingResult.hasFieldErrors("ydepositMax") ||
            bindingResult.hasFieldErrors("purchaseMax")){
            throw new OnBoardingBadRequestException(INVALID_MAX_PRICE);
        }
        if(bindingResult.hasFieldErrors("realEstateType")){
            throw new OnBoardingBadRequestException(INVALID_INTEREST_TYPE);
        }
        if(bindingResult.hasFieldErrors("transactionType")){
            throw new OnBoardingBadRequestException(INVALID_TRANSACTION_TYPE);
        }
        if(bindingResult.hasFieldErrors("isSmallerthanMax")){
            throw new OnBoardingBadRequestException(MIN_IS_BIGGER_THAN_MAX);
        }

        if(bindingResult.hasErrors()){
            throw new OnBoardingBadRequestException(BAD_REQUEST, getErrorMessages(bindingResult));
        }

        return new BaseResponse(MEMBER_INFO_UPDATE_SUCCESS, this.userService.setOnBoarding(patchOnBoardingRequest, userId));

    }

    @Operation(summary = "마이페이지 회원의 정보 조회 API", description = "마이페이지에서 보여줄 회원 정보를 반환")
    @GetMapping("")
    public BaseResponse<GetMyPageResponse> myPage(@Parameter(hidden = true) @PreAuthorize long userId){
        log.info("{UserController.myPage}");

        return new BaseResponse<>(MY_PAGE_INFO_LOAD_SUCCESS, this.userService.myPageLoad(userId));
    }

    @Operation(summary = "프로필 수정하기 화면에서 보여줄 회원 정보 조회 API", description = "프로필 수정하기 화면에서 보여줄 회원 정보를 반환")
    @GetMapping("/detail")
    public BaseResponse<GetMyPageDetailResponse> myPageDetail(@Parameter(hidden = true) @PreAuthorize long userId) {
        log.info("{UserController.myPageDetail}");

        return new BaseResponse<>(MY_PAGE_INFO_LOAD_SUCCESS, this.userService.myPageDetailLoad(userId));
    }

    @Operation(summary = "프로필 수정하기 API", description = "프로필 수정하기 기능을 담당하는 API입니다.")
    @PutMapping(value = "", consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
    public BaseResponse<Object> updateMyInfo(@Parameter(hidden = true) @PreAuthorize long userId, @RequestPart(value = "data", required = false) PutUpdateMyInfoRequest putUpdateMyInfoRequest, @RequestPart(value = "file", required = false) MultipartFile file, BindingResult bindingResult){
        log.info("{UserController.updateMyInfo}");

        if(bindingResult.hasFieldErrors("nickname")){
            throw new UserBadRequestException(INVALID_NICKNAME_FORMAT);
        }
        if(bindingResult.hasFieldErrors("gender")){
            throw new UserBadRequestException(INVALID_GENDER_FORMAT);
        }
        if(bindingResult.hasFieldErrors("birthday")){
            throw new UserBadRequestException(INVALID_BIRTHDAY_FORMAT);
        }
        if(bindingResult.hasFieldErrors("realEstateType")){
            throw new UserBadRequestException(INVALID_INTEREST_TYPE);
        }
        if(bindingResult.hasFieldErrors("address")){
            throw new UserBadRequestException(ADDRESS_OVER_LENGTH);
        }
        if(bindingResult.hasFieldErrors("latitude") || bindingResult.hasFieldErrors("longitude")){
            throw new UserBadRequestException(INVALID_LAT_LNG);
        }
        if(bindingResult.hasFieldErrors("transactionType")){
            throw new UserBadRequestException(INVALID_TRANSACTION_TYPE);
        }
        if(bindingResult.hasFieldErrors("mpriceMin") ||
                bindingResult.hasFieldErrors("mdepositMin") ||
                bindingResult.hasFieldErrors("ydepositMin") ||
                bindingResult.hasFieldErrors("purchaseMin")){
            throw  new UserBadRequestException(INVALID_MIN_PRICE);
        }
        if(bindingResult.hasFieldErrors("mpriceMax") ||
                bindingResult.hasFieldErrors("mdepositMax") ||
                bindingResult.hasFieldErrors("ydepositMax") ||
                bindingResult.hasFieldErrors("purchaseMax")){
            throw new UserBadRequestException(INVALID_MAX_PRICE);
        }
        if(bindingResult.hasFieldErrors("isSmallerthanMax")){
            throw new UserBadRequestException(MIN_IS_BIGGER_THAN_MAX);
        }

        return new BaseResponse<>(MEMBER_INFO_UPDATE_SUCCESS, this.userService.updateMyInfo(userId, file, putUpdateMyInfoRequest));
    }

    @Operation(summary = "마이페이지 리스트 항목 수정 페이지 로드 API", description = "콕리스트 항목 설정 정보를 반환")
    @GetMapping("/kokOption")
    public BaseResponse<GetKokOptionLoadResponse> loadKokOption(@Parameter(hidden=true) @PreAuthorize long userId){
        log.info("{UserController.kokOptionLoad}");

        return new BaseResponse<>(MEMBER_LIST_ITEM_QUERY_SUCCESS, this.userService.loadKokOption(userId));
    }

    @Operation(summary = "마이페이지 리스트 항목 수정 API", description = "콕리스트 항목 설정 정보를 수정")
    @PutMapping("/kokOption")
    public BaseResponse<Object> updateKokOption(@Parameter(hidden=true) @PreAuthorize long userId, @Validated @RequestBody PostUpdateKokOptionRequest postUpdateKokOptionRequest, BindingResult bindingResult){
        log.info("{UserController.updateKokOption}");

        if(bindingResult.hasErrors()){
            throw new UserBadRequestException(MEMBER_LIST_ITEM_UPDATE_FAILURE);
        }

        return new BaseResponse<>(MEMBER_LIST_ITEM_UPDATE_SUCCESS, this.userService.updateKokOption(userId, postUpdateKokOptionRequest));
    }


    @Operation(summary = "로그아웃 API", description = "회원 로그아웃을 위한 api입니다.")
    @GetMapping("/logout")
    public BaseResponse<Object> logout(@Parameter(hidden=true) @PreAuthorize long userId){
        log.info("{UserController.logout}");

        return new BaseResponse<>(LOGOUT_SUCCESS, this.userService.logout(userId));
    }

    @Operation(summary = "회원탈퇴 API", description = "회원탈퇴를 위한 api입니다.")
    @DeleteMapping("")
    public BaseResponse<Object> deregistration(@Parameter(hidden=true) @PreAuthorize long userId){
        log.info("[UserController.deregistration]");

        return new BaseResponse<>(DEREGISTRATION_SUCCESS, this.userService.deregisterV2(userId));
    }

    @Operation(summary = "필터 API", description = "필터정보 수정을 위한 api입니다.")
    @PatchMapping("/filter")
    public BaseResponse<Object> updateFilter(@Parameter(hidden=true) @PreAuthorize long userId, @Validated @RequestBody PatchUpdateFilterRequest patchUpdateFilterRequest, BindingResult bindingResult){
        log.info("{UserController.updateFilter}");

        if(bindingResult.hasFieldErrors("transactionType")){
            throw new UserBadRequestException(INVALID_TRANSACTION_TYPE);
        }
        if(bindingResult.hasFieldErrors("realEstateType")){
            throw new UserBadRequestException(INVALID_INTEREST_TYPE);
        }
        if(bindingResult.hasFieldErrors("realEstateType")){
            throw new UserBadRequestException(INVALID_INTEREST_TYPE);
        }
        if(bindingResult.hasFieldErrors("priceMin") ||
                bindingResult.hasFieldErrors("depositMin")){
            throw  new UserBadRequestException(INVALID_MIN_PRICE);
        }
        if(bindingResult.hasFieldErrors("priceMax") ||
                bindingResult.hasFieldErrors("depositMax")){
            throw new UserBadRequestException(INVALID_MAX_PRICE);
        }
        if(bindingResult.hasFieldErrors("isSmallerThanMax")){
            throw new UserBadRequestException(MIN_IS_BIGGER_THAN_MAX);
        }

        return new BaseResponse<>(MEMBER_FILTER_UPDATE_SUCCESS, this.userService.updateFilter(userId, patchUpdateFilterRequest));
    }
}
