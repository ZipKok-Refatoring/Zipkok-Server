package com.project.zipkok.controller;

import com.project.zipkok.common.argument_resolver.PreAuthorize;
import com.project.zipkok.common.exception.KokException;
import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.dto.*;
import com.project.zipkok.service.KokService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/kok")
@Tag(name = "Kok API", description = "콕 관련 API")
public class KokController {

    private final KokService kokService;

    @GetMapping("")
    @Operation(summary = "유저의 콕리스트 반환", description = "page: 표시할 페이지, size: 페이지당 표시할 데이터 수")
    public BaseResponse<GetKokResponse> getKoks(@Parameter(hidden = true) @PreAuthorize long userId,
                                                @ParameterObject Pageable pageable) {

        log.info("[KokController.getKoks]");

        return new BaseResponse<>(KOK_LIST_QUERY_SUCCESS, kokService.getKoks(userId, pageable));
    }

    @GetMapping("/{kokId}/detail")
    @Operation(summary = "콕 세부정보 반환", description = "kokId를 PathVariable에 추가하여 요청")
    public BaseResponse<GetKokDetailResponse> getKokDetail(@Parameter(hidden = true) @PreAuthorize long userId,
                                                           @Parameter(name = "kokId", description = "조회할 콕의 Id") @PathVariable(value = "kokId") long kokId) {
        log.info("[KokController.getKokDetail]");

        return new BaseResponse<GetKokDetailResponse>(KOK_DETAIL_QUERY_SUCCESS, kokService.getKokDetail(userId, kokId));
    }

    @GetMapping("/{kokId}/outer")
    @Operation(summary = "콕 집주변 정보 반환", description = "콕의 집 주변 정보를 반환")
    public BaseResponse<GetKokOuterInfoResponse> getKokOuterInfo(@Parameter(hidden = true) @PreAuthorize long userId,
                                                                @Parameter(name = "kokId", description = "집 주변 정보를 조회할 콕의 Id") @PathVariable(value = "kokId") long kokId) {

        log.info("[KokController.getKokOuterInfo]");

        return new BaseResponse<GetKokOuterInfoResponse>(KOK_OUTER_INFO_QUERY_SUCCESS, kokService.getKokOuterInfo(userId, kokId));

    }

    @GetMapping("/{kokId}/inner")
    @Operation(summary = "콕 집 내부 정보 반환", description = "콕의 집 내부 정보를 반환")
    public BaseResponse<GetKokInnerInfoResponse> getKokInnerInfo(@Parameter(hidden = true) @PreAuthorize long userId,
                                                                 @Parameter(name = "kokId", description = "집 내부 정보를 조회할 콕의 Id") @PathVariable(value = "kokId") long kokId) {

        log.info("[KokController.getKokInnerInfo]");

        return new BaseResponse<GetKokInnerInfoResponse>(KOK_INTERNAL_INFO_QUERY_SUCCESS, kokService.getKokInnerInfo(userId, kokId));

    }

    @GetMapping("/{kokId}/contract")
    @Operation(summary = "콕 중개/계약 정보 반환", description = "콕의 중개/계약 정보를 반환")
    public BaseResponse<GetKokContractResponse> getKokContractInfo(@Parameter(hidden = true) @PreAuthorize long userId,
                                                                   @Parameter(name = "kokId", description = "집 중개/계약 정보를 조회할 콕의 Id") @PathVariable(value = "kokId") long kokId) {

        log.info("[KokController.getKokContractInfo]");

        return new BaseResponse<GetKokContractResponse>(KOK_CONTRACT_INFO_QUERY_SUCCESS, kokService.getKokContractInfo(userId, kokId));

    }

    @GetMapping("/{kokId}/review")
    @Operation(summary = "콕의 후기 반환", description = "콕의 후기 정보를 반환")
    public BaseResponse<GetKokReviewInfoResponse> getKokReviewInfo(@Parameter(hidden = true) @PreAuthorize long userId,
                                                                   @Parameter(name = "kokId", description = " 후기 정보를 조회할 콕의 Id") @PathVariable(value = "kokId") long kokId) {

        log.info("[KokController.getKokReviewInfo]");

        return new BaseResponse<GetKokReviewInfoResponse>(KOK_REVIEW_INFO_QUERY_SUCCESS, kokService.getKokReviewInfo(userId, kokId));

    }

    @GetMapping("/config")
    @Operation(summary = "회원의 콕리스트 설정 정보 반환", description = "작성한 콕에서 확인 가능한 항목, 체크된 항목 정보 불러오기")
    public BaseResponse<GetKokConfigInfoResponse> getKokConfigInfo(@Parameter(hidden = true) @PreAuthorize long userId,
                                                                   @Parameter(name = "kokId", description = "체크된 항목을 불러올 콕의 Id") @RequestParam(value = "kokId", required = false) Long kokId) {

        log.info("[KokController.getKokCongInfo]");

        return new BaseResponse<GetKokConfigInfoResponse>(MEMBER_SETTING_INFO_QUERY_SUCCESS, kokService.getKokConfigInfo(userId, kokId));
    }

    @Operation(summary = "콕 등록", description = "콕 작성하기")
    @PostMapping(value = "", consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
    public BaseResponse<PostOrPutKokResponse> registerKok(@Parameter(hidden=true) @PreAuthorize long userId, @RequestPart(value = "file", required = false) List<MultipartFile> multipartFiles, @Validated @RequestPart(value = "data", required = false) PostOrPutKokRequest postKokRequest, BindingResult bindingResult){
        log.info("[KokController.registerKok]");

        if(bindingResult.hasErrors()){
            throw new KokException(KOK_REGISTRATION_FAILURE);
        }

        return new BaseResponse<>(KOK_REGISTRATION_SUCCESS, kokService.createOrUpdateKok(userId, multipartFiles, postKokRequest));

    }

    @Operation(summary = "콕 수정", description = "콕 수정하기")
    @PutMapping(value = "", consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
    public BaseResponse<PostOrPutKokResponse> modifyKok(@Parameter(hidden=true) @PreAuthorize long userId, @RequestPart(value = "file", required = false) List<MultipartFile> multipartFiles, @Validated @RequestPart(value = "data", required = false) PostOrPutKokRequest putKokRequest, BindingResult bindingResult){
        log.info("[KokController.modifyKok]");

        if(bindingResult.hasErrors()){
            throw new KokException(KOK_MODIFY_FAILURE);
        }

        return new BaseResponse<>(KOK_MODIFY_SUCCESS, kokService.createOrUpdateKok(userId, multipartFiles, putKokRequest));

    }


}
