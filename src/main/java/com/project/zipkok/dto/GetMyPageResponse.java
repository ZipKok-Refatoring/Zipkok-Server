package com.project.zipkok.dto;

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
public class GetMyPageResponse {

    private String nickname;

    private String imageUrl;

    private String address;

    private String realEstateType;

    private String transactionType;

    private Long priceMax;

    private Long depositMax;

    private Long priceMin;

    private Long depositMin;
}
