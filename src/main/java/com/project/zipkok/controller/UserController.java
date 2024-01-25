package com.project.zipkok.controller;

import com.project.zipkok.common.argument_resolver.PreAuthorize;
import com.project.zipkok.common.exception.user.OnBoardingBadRequestException;
import com.project.zipkok.common.exception.user.UserBadRequestException;
import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.dto.GetUserResponse;
import com.project.zipkok.dto.PatchOnBoardingRequest;
import com.project.zipkok.dto.PostSignUpRequest;
import com.project.zipkok.service.UserService;
import com.project.zipkok.util.jwt.AuthTokens;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User API", description = "회원 관련 API")
public class UserController {
    private final UserService userService;

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
        if(bindingResult.hasFieldErrors("isSmallerthanMax")){
            throw new OnBoardingBadRequestException(MIN_IS_BIGGER_THAN_MAX);
        }
        if(bindingResult.hasErrors()){
            throw new OnBoardingBadRequestException(BAD_REQUEST);
        }

        return new BaseResponse(MEMBER_INFO_UPDATE_SUCCESS, this.userService.setOnBoarding(patchOnBoardingRequest, userId));

    }
}
