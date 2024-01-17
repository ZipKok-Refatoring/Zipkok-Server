package com.project.zipkok.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetUserResponse {

    private String nickname;
    private String imageUrl;
    private String address;
    private String realEstateType;
    private String transactionType;
    private Long priceMax;
    private Long pirceMin;
    private Long depositMax;
    private Long depositMin;

}
