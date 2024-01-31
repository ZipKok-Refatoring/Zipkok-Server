package com.project.zipkok.controller;

import com.project.zipkok.common.argument_resolver.PreAuthorize;
import com.project.zipkok.common.exception.RealEstateException;
import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.dto.GetRealEstateResponse;
import com.project.zipkok.service.RealEstateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.INVALID_PROPERTY_ID;
import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.PROPERTY_DETAIL_QUERY_SUCCESS;

@Slf4j
@RestController
@RequestMapping("/realEstate")
@RequiredArgsConstructor
@Tag(name = "RealEstate API", description = "매물 관련 API")
public class RealEstateController {

    private final RealEstateService realEstateService;

    @Operation(summary = "매물 상세정보 API", description = "매물의 상세정보를 응답하는 API입니다.")
    @GetMapping("/{realEstateId}")
    public BaseResponse<GetRealEstateResponse> getRealEstate(@Parameter(hidden = true) @PreAuthorize long userId, @Parameter(name = "realEstateId", description = "매물의 Id", in = ParameterIn.PATH)
                                                                 @PathVariable(value = "realEstateId") Long realEstateId) {
        log.info("[RealEstateController.getRealEstate]");

        if (realEstateId == null) {
            throw new RealEstateException(INVALID_PROPERTY_ID);
        }

        return new BaseResponse<>(PROPERTY_DETAIL_QUERY_SUCCESS, realEstateService.getRealEstateInfo(userId, realEstateId));
    }
}
