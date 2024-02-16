package com.project.zipkok.controller;

import com.project.zipkok.common.argument_resolver.PreAuthorize;
import com.project.zipkok.common.exception.PinException;
import com.project.zipkok.common.exception.RealEstateException;
import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.dto.*;
import com.project.zipkok.service.PinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.*;
import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.MIN_POINT_IS_BIGGER_THAN_MAX_POINT;

@Slf4j
@RestController
@RequestMapping("/pin")
@RequiredArgsConstructor
@Tag(name = "Pin API", description = "핀 관련 API")
public class PinController {

    private final PinService pinService;

    @Operation(summary = "지도에서 핀 조회 API", description = "지도에서 핀 조회할때 사용하는 API")
    @GetMapping("")
    public BaseResponse<GetPinResponse> getPin(@Parameter(hidden=true) @PreAuthorize long userId,
                                               @Validated @ModelAttribute GetPinRequest getPinRequest,
                                               BindingResult bindingResult){
        log.info("{PinController.getPin}");

        if(bindingResult.hasFieldErrors("southWestLat") ||
                bindingResult.hasFieldErrors("northEastLat")){
            throw new RealEstateException(INVALID_LATITUDE_FORMAT);
        }
        if(bindingResult.hasFieldErrors("southWestLon") ||
                bindingResult.hasFieldErrors("northEastLon")){
            throw new RealEstateException(INVALID_LONGITUDE_FORMAT);
        }
        if(bindingResult.hasErrors()){
            throw new RealEstateException(MIN_POINT_IS_BIGGER_THAN_MAX_POINT);
        }

        return new BaseResponse<>(PIN_LOAD_SUCCESS, this.pinService.getPin(userId));
    }

    @Operation(summary = "핀 상세정보 조회 API", description = "핀의 상세정보를 조회하는 API")
    @GetMapping("/{pinId}")
    public BaseResponse<PinInfo> getPinDetail(@Parameter(hidden=true) @PreAuthorize long userId, @PathVariable Long pinId){
        log.info("{PinController.getPin}");

        return new BaseResponse<>(PIN_LOAD_SUCCESS, this.pinService.getPinDetail(userId, pinId));
    }

    @Operation(summary = "핀 등록 API", description = "핀을 등록하는 API")
    @PostMapping("")
    public BaseResponse<PostPinResponse> registerPin(@Parameter(hidden=true) @PreAuthorize long userId,
                                                     @Validated @RequestBody PostPinRequest postPinRequest,
                                                     BindingResult bindingResult){
        log.info("{PinController.getPin}");

        if(bindingResult.hasErrors()){
            throw new PinException(INVALID_PIN_FORMAT);
        }


        return new BaseResponse<>(PIN_LOAD_SUCCESS, this.pinService.registerPin(userId, postPinRequest));
    }

    @Operation(summary = "핀 수정 API", description = "핀을 수정하는 API")
    @PutMapping("")
    public BaseResponse<Object> updatePin(@Parameter(hidden=true) @PreAuthorize long userId,
                                                     @Validated @RequestBody PinInfo putPinRequest,
                                                     BindingResult bindingResult){
        log.info("{PinController.updatePin}");

        if(bindingResult.hasErrors()){
            throw new PinException(INVALID_PIN_FORMAT);
        }

        return new BaseResponse<>(PIN_UPDATE_SUCCESS, this.pinService.updatePin(userId, putPinRequest));
    }

    @Operation(summary = "핀 삭제 API", description = "핀을 삭제하는 API")
    @DeleteMapping("")
    public BaseResponse<Object> deletePin(@Parameter(hidden=true) @PreAuthorize long userId,
                                          @Validated @RequestBody DeletePinRequest deletePinRequest,
                                          BindingResult bindingResult){
        log.info("{PinController.deletePin}");

        if(bindingResult.hasErrors()){
            throw new PinException(INVALID_PIN_FORMAT);
        }

        return new BaseResponse<>(PIN_DELETE_SUCCESS, this.pinService.deletePin(userId, deletePinRequest));
    }

}
