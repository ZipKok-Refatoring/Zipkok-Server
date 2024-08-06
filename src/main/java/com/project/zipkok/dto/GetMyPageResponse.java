package com.project.zipkok.dto;

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

    public static class GetMyPageResponseBuilder {
        public GetMyPageResponseBuilder fromTransactionType(User user, TransactionPriceConfig transactionPriceConfig, DesireResidence desireResidence) {
            this.nickname(user.getNickname());
            this.imageUrl(user.getProfileImgUrl());
            this.address(desireResidence.getAddress());
            this.realEstateType(user.getRealEstateType() == null ? null : user.getRealEstateType().toString());

            if(user.getTransactionType() == null) {
                this.transactionType(null);
                this.priceMax(null);
                this.priceMin(null);
                this.depositMax(null);
                this.depositMin(null);
            }
            else {
                this.transactionType = user.getTransactionType().toString();
                String transactionTypeDescription = user.getTransactionType().getDescription();
                switch (transactionTypeDescription) {
                    case "월세" -> {
                        this.priceMax(transactionPriceConfig.getMPriceMax());
                        this.priceMin(transactionPriceConfig.getMPriceMin());
                        this.depositMax(transactionPriceConfig.getMDepositMax());
                        this.depositMin(transactionPriceConfig.getMDepositMin());
                    }
                    case "전세" -> {
                        this.depositMax(transactionPriceConfig.getYDepositMax());
                        this.depositMin(transactionPriceConfig.getYDepositMin());
                    }
                    case "매매" -> {
                        this.priceMax(transactionPriceConfig.getPurchaseMax());
                        this.priceMin(transactionPriceConfig.getPurchaseMin());
                    }
                }
            }
            return this;
        }
    }

    // of 메서드
    public static GetMyPageResponse of(User user, TransactionPriceConfig transactionPriceConfig, DesireResidence desireResidence) {
        return GetMyPageResponse.builder()
                .fromTransactionType(user, transactionPriceConfig, desireResidence)
                .build();
    }
}
