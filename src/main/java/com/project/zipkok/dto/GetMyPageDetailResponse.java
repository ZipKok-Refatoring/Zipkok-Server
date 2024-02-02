package com.project.zipkok.dto;

import com.project.zipkok.common.enums.Gender;
import com.project.zipkok.common.enums.RealEstateType;
import com.project.zipkok.common.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMyPageDetailResponse {

    private String imageUrl;

    private String nickname;

    private String birthday;

    private Gender gender;

    private String address;

    private String realEstateType;

    private String transactionType;

    private Long mpriceMin;

    private Long mpriceMax;

    private Long mdepositMin;

    private Long mdepositMax;

    private Long ydepositMin;

    private Long ydepositMax;

    private Long priceMin;

    private Long priceMax;
}
