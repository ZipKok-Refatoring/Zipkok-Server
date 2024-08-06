package com.project.zipkok.dto;

import com.project.zipkok.common.enums.TransactionType;
import com.project.zipkok.model.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    public static GetMyPageResponse from(User user) {

        GetMyPageResponse response =  GetMyPageResponse.builder()
                .nickname(user.getNickname())
                .imageUrl(user.getProfileImgUrl())
                .address(user.getDesireResidence().getAddress())
                .realEstateType(user.getRealEstateType().name())
                .transactionType(user.getTransactionType().name())
                .build();

        return setMinMaxPriceInfo(response, user);

    }

    private static GetMyPageResponse setMinMaxPriceInfo(GetMyPageResponse response, User user) {

        if(user.getTransactionType().equals(TransactionType.MONTHLY)) {
            response.setPriceMax(user.getTransactionPriceConfig().getMPriceMax());
            response.setPriceMin(user.getTransactionPriceConfig().getMPriceMin());
            response.setDepositMax(user.getTransactionPriceConfig().getMDepositMax());
            response.setDepositMin(user.getTransactionPriceConfig().getMDepositMin());
            return response;
        }

        if(user.getTransactionType().equals(TransactionType.YEARLY)) {
            response.setDepositMax(user.getTransactionPriceConfig().getYDepositMax());
            response.setDepositMin(user.getTransactionPriceConfig().getYDepositMin());
            return response;
        }

        if(user.getTransactionType().equals(TransactionType.PURCHASE)) {
            response.setDepositMax(user.getTransactionPriceConfig().getPurchaseMax());
            response.setDepositMin(user.getTransactionPriceConfig().getPurchaseMin());
            return response;
        }

        throw new IllegalArgumentException("TransactionType이 잘못되었습니다.");
    }
}
