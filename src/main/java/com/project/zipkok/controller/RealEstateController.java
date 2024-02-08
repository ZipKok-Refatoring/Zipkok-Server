package com.project.zipkok.controller;

import com.project.zipkok.common.argument_resolver.PreAuthorize;
import com.project.zipkok.common.exception.RealEstateException;
import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.dto.GetRealEstateOnMapResponse;
import com.project.zipkok.dto.GetRealEstateResponse;
import com.project.zipkok.dto.PostRealEstateRequest;
import com.project.zipkok.dto.PostRealEstateResponse;
import com.project.zipkok.service.RealEstateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.*;

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

    @Operation(summary = "매물 직접등록 API", description = "매물을 직접 등록할 때 사용하는 API입니다.")
    @PostMapping("")
    public BaseResponse<PostRealEstateResponse> registerRealEstate(@Parameter(hidden = true) @PreAuthorize long userId,
                                                                   @Validated @RequestBody PostRealEstateRequest postRealEstateRequest, BindingResult bindingResult) {

        log.info("[RealEstateController.registerReslEstate]");

        if(bindingResult.hasFieldErrors("realEstateName")) {
            throw new RealEstateException(INVALID_PROPERTY_NAME);
        } else if (bindingResult.hasFieldErrors("transactioinType") || bindingResult.hasFieldErrors("realEstateType")) {
            throw new RealEstateException(INVALID_RENTAL_PRICE_FORMAT);
        } else if (bindingResult.hasFieldErrors("administrativeFee")) {
            throw new RealEstateException(INVALID_MANAGEMENT_FEE_FORMAT);
        } else if (bindingResult.hasFieldErrors("address")) {
            throw new RealEstateException(INVALID_ADDRESS_FORMAT);
        } else if (bindingResult.hasFieldErrors("latitude")) {
            throw new RealEstateException(INVALID_LATITUDE_FORMAT);
        } else if (bindingResult.hasFieldErrors("longitude")) {
            throw new RealEstateException(INVALID_LONGITUDE_FORMAT);
        }

        return new BaseResponse<>(PROPERTY_REGISTRATION_SUCCESS, realEstateService.registerRealEstate(userId, postRealEstateRequest));
    }

    @Operation(summary = "홈 화면 지도 API", description = "지도 상에 띄울 매물을 받기 위한 api입니다.")
    @GetMapping("")
    public BaseResponse<GetRealEstateOnMapResponse> realEstateOnMap(@Parameter(hidden=true) @PreAuthorize long userId, @RequestParam("southWestLat") Double southWestLat, @RequestParam("southWestLon") Double southWestLon, @RequestParam("northEastLat") Double northEastLat, @RequestParam("northEastLon") Double northEastLon){
        log.info("{UserController.signout}");

        if(southWestLat == null ||
            northEastLat == null){
            throw new RealEstateException(INVALID_LATITUDE_FORMAT);
        }
        if(southWestLon == null ||
                northEastLon == null){
            throw new RealEstateException(INVALID_LONGITUDE_FORMAT);
        }


        return new BaseResponse<>(PROPERTY_MAP_QUERY_SUCCESS, this.realEstateService.getRealEstate(userId, southWestLat,southWestLon,northEastLat,northEastLon));
    }
}
