package com.project.zipkok.controller;

import com.project.zipkok.common.argument_resolver.PreAuthorize;
import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.dto.GetZimLoadResponse;
import com.project.zipkok.service.ZimService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/zim")
@RequiredArgsConstructor
public class ZimController {

    private final ZimService zimService;

    @GetMapping("")
    public BaseResponse<GetZimLoadResponse> zimLoad(@Parameter(hidden = true) @PreAuthorize long userId){
        log.info("{ZimController.zimLoad}");

        return new BaseResponse<>(this.zimService.zimLoad(userId));
    }
}
