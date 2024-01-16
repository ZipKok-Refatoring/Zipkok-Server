package com.project.zipkok.controller;

import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.dto.GetUserResponse;
import com.project.zipkok.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("")
    public BaseResponse<List<GetUserResponse>> getUserInfo() {
        return new BaseResponse<List<GetUserResponse>>(userService.getUsers());
    }
}
