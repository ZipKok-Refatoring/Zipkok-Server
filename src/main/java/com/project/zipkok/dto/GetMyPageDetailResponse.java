package com.project.zipkok.dto;

import com.project.zipkok.common.enums.Gender;
import com.project.zipkok.common.enums.RealEstateType;
import com.project.zipkok.common.enums.TransactionType;
import com.project.zipkok.model.DesireResidence;
import com.project.zipkok.model.TransactionPriceConfig;
import com.project.zipkok.model.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    private Double latitude;

    private Double longitude;

    // of 메서드
    public static GetMyPageDetailResponse of(User user, TransactionPriceConfig transactionPriceConfig, DesireResidence desireResidence) {
        return GetMyPageDetailResponse.builder()
                .imageUrl(user.getProfileImgUrl())
                .nickname(user.getNickname())
                .birthday(user.getBirthday())
                .gender(user.getGender())
                .address(desireResidence.getAddress())
                .realEstateType(user.getRealEstateType() == null ? null : user.getRealEstateType().toString())
                .transactionType(user.getTransactionType() == null ? null : user.getTransactionType().toString())
                .mpriceMin(transactionPriceConfig.getMPriceMin())
                .mpriceMax(transactionPriceConfig.getMPriceMax())
                .mdepositMin(transactionPriceConfig.getMDepositMin())
                .mpriceMax(transactionPriceConfig.getMDepositMax())
                .ydepositMin(transactionPriceConfig.getYDepositMin())
                .ydepositMax(transactionPriceConfig.getYDepositMax())
                .priceMin(transactionPriceConfig.getPurchaseMin())
                .priceMax(transactionPriceConfig.getPurchaseMax())
                .latitude(user.getDesireResidence().getLatitude() == null ? null : user.getDesireResidence().getLatitude())
                .longitude(user.getDesireResidence().getLongitude() == null ? null : user.getDesireResidence().getLongitude())
                .build();
    }
}
