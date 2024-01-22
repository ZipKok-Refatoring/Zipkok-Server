package com.project.zipkok.controller;

import com.project.zipkok.common.exception.user.UserBadRequestException;
import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.dto.GetUserResponse;
import com.project.zipkok.dto.PostSignUpRequest;
import com.project.zipkok.service.UserService;
import com.project.zipkok.util.jwt.AuthTokens;
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
public class UserController {
    private final UserService userService;

    @CrossOrigin(origins = {"http://localhost:3000"})
    @GetMapping("")
    public BaseResponse<List<GetUserResponse>> getUserInfo() {
        return new BaseResponse<List<GetUserResponse>>(userService.getUsers());
    }

    @CrossOrigin(origins = {"http://localhost:3000"})
    @PostMapping("")
    public BaseResponse<AuthTokens> signUp(@Validated @RequestBody PostSignUpRequest postSignUpRequest, BindingResult bindingResult) {
        log.info("{UserController.signUp}");

        System.out.println(postSignUpRequest.getNickname());

        if(bindingResult.hasFieldErrors("nickname")){
            System.out.println(bindingResult.getFieldError("nickname"));
            throw new UserBadRequestException(INVALID_NICKNAME_FORMAT);
        }
        if(bindingResult.hasFieldErrors("gender")){
            System.out.println(bindingResult.getFieldError("gender"));
            throw new UserBadRequestException(INVALID_GENDER_FORMAT);
        }
        if(bindingResult.hasFieldErrors("birthday")){
            throw new UserBadRequestException(INVALID_BIRTHDAY_FORMAT);
        }
        if(bindingResult.hasErrors()){
            System.out.println(bindingResult.getFieldError());
            throw new UserBadRequestException(SERVER_ERROR);
        }

        return new BaseResponse<>(this.userService.signUp(postSignUpRequest));
    }
}
