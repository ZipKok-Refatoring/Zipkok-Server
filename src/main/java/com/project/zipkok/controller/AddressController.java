package com.project.zipkok.controller;

import com.project.zipkok.common.response.BaseResponse;
import com.project.zipkok.dto.GetAddressResponse;
import com.project.zipkok.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(origins = {"https://localhost:3000", "http://localhost:3000"})
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("")
    public BaseResponse<GetAddressResponse> searchAddress(@RequestParam("query") String query, @RequestParam("page") int page, @RequestParam("size") int size) {
        log.info("[AddressController.searchAddress]");

        return new BaseResponse<>(addressService.getAddresses(query, page, size));
    }
}
