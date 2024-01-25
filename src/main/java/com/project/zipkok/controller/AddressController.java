package com.project.zipkok.controller;

import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.common.response.status.BaseExceptionResponseStatus;
import com.project.zipkok.dto.GetAddressResponse;
import com.project.zipkok.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
@Tag(name = "Address API", description = "주소검색 API")
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "주소검색 API", description = "query에 검색할 주소를 입력")
    @GetMapping("")
    public BaseResponse<GetAddressResponse> searchAddress(@RequestParam("query") String query, @RequestParam("page") int page, @RequestParam("size") int size) {
        log.info("[AddressController.searchAddress]");

        return new BaseResponse<>(BaseExceptionResponseStatus.ADDRESS_SEARCH_SUCCESS, addressService.getAddresses(query, page, size));
    }
}
