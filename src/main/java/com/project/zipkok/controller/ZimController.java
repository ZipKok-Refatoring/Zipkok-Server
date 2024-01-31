package com.project.zipkok.controller;

import com.project.zipkok.common.argument_resolver.PreAuthorize;
import com.project.zipkok.common.exception.zim.ZimBadRequestException;
import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.dto.GetZimLoadResponse;
import com.project.zipkok.dto.PostZimRegisterRequest;
import com.project.zipkok.service.ZimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Zim API", description = "찜 관련 API")
public class ZimController {

    private final ZimService zimService;

    @Operation(summary = "찜 리스트 조회 API", description = "회원의 찜 리스트 반환")
    @GetMapping("")
    public BaseResponse<GetZimLoadResponse> zimLoad(@Parameter(hidden = true) @PreAuthorize long userId){
        log.info("{ZimController.zimLoad}");

        return new BaseResponse<>(FAVORITES_QUERY_SUCCESS, this.zimService.zimLoad(userId));
    }
    @Operation(summary = "찜 등록 API", description = "찜 하기")
    @PostMapping("")
    public BaseResponse<Object> zimRegister(@Parameter(hidden = true) @PreAuthorize long userId, @Validated  @RequestBody PostZimRegisterRequest postZimRegisterRequest, BindingResult bindingResult){
        log.info("{ZimController.zimRegister}");

        if(bindingResult.hasErrors()){
            throw new ZimBadRequestException(INVALID_REALESTATE_ID);
        }

        return new BaseResponse<>(FAVORITES_ADD_SUCCESS, this.zimService.zimRegister(userId, postZimRegisterRequest.getRealEstateId()));
    }

    @Operation(summary = "찜 취소 API", description = "찜 취소")
    @DeleteMapping("")
    public BaseResponse<Object> zimDelete(@Parameter(hidden = true) @PreAuthorize long userId, @Validated  @RequestBody PostZimRegisterRequest postZimRegisterRequest, BindingResult bindingResult){
        log.info("{ZimController.zimDelete}");

        if(bindingResult.hasErrors()){
            throw new ZimBadRequestException(INVALID_REALESTATE_ID);
        }

        return new BaseResponse<>(FAVORITES_CANCEL_SUCCESS, this.zimService.zimDelete(userId, postZimRegisterRequest.getRealEstateId()));
    }
}
