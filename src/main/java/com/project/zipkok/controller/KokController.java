package com.project.zipkok.controller;

import com.project.zipkok.common.argument_resolver.PreAuthorize;
import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.dto.GetKokDetailResponse;
import com.project.zipkok.dto.GetKokResponse;
import com.project.zipkok.service.KokService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.KOK_DETAIL_QUERY_SUCCESS;
import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.KOK_LIST_QUERY_SUCCESS;

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



}
