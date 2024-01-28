package com.project.zipkok.controller;

import com.project.zipkok.common.argument_resolver.PreAuthorize;
import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.dto.*;
import com.project.zipkok.service.KokService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.*;

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
                                                @Parameter(name = "page", description = "표시할 페이지 번호", in = ParameterIn.QUERY) @RequestParam(value = "page") int page,
                                                @Parameter(name = "size", description = "페이지당 표시할 데이터 수", in = ParameterIn.QUERY) @RequestParam(value = "size") int size) {

        log.info("[KokController.getKoks]");

        return new BaseResponse<>(KOK_LIST_QUERY_SUCCESS, kokService.getKoks(userId, page, size));
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


}
