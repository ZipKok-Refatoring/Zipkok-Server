package com.project.zipkok.controller;

import com.project.zipkok.common.argument_resolver.PreAuthorize;
import com.project.zipkok.common.exception.zim.ZimBadRequestException;
import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.dto.GetZimLoadResponse;
import com.project.zipkok.dto.PostZimRegisterRequest;
import com.project.zipkok.service.ZimService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@RestController
@RequestMapping("/zim")
@RequiredArgsConstructor
public class ZimController {

    private final ZimService zimService;

    @GetMapping("")
    public BaseResponse<GetZimLoadResponse> zimLoad(@Parameter(hidden = true) @PreAuthorize long userId){
        log.info("{ZimController.zimLoad}");

        return new BaseResponse<>(FAVORITES_QUERY_SUCCESS, this.zimService.zimLoad(userId));
    }

    @PostMapping("")
    public BaseResponse<Object> zimRegister(@Parameter(hidden = true) @PreAuthorize long userId, @Validated  @RequestBody PostZimRegisterRequest postZimRegisterRequest, BindingResult bindingResult){
        log.info("{ZimController.zimRegister}");

        if(bindingResult.hasErrors()){
            throw new ZimBadRequestException(INVALID_REALESTATE_ID);
        }

        return new BaseResponse<>(FAVORITES_ADD_SUCCESS, this.zimService.zimRegister(userId, postZimRegisterRequest.getRealEstateId()));
    }
}
