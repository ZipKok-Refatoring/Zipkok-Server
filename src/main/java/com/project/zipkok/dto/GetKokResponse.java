package com.project.zipkok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.zipkok.model.Kok;
import com.project.zipkok.model.Zim;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Builder
public class GetKokResponse {

    private List<Koks> koks;

    @Builder
    public static class Koks {
        @Getter
        private Long kokId;

        @Getter
        private Long realEstateId;

        @Getter
        private String imageUrl;

        @Getter
        private String address;

        @Getter
        private String detailAddress;

        @Getter
        private String estateAgent;

        @Getter
        private String transactionType;

        @Getter
        private String realEstateType;

        @Getter
        private Long deposit;

        @Getter
        private Long price;

        @JsonProperty("isZimmed")
        private boolean isZimmed;

        public static Koks from(Kok kok, Boolean isZimmed) {
            return Koks.builder()
                    .kokId(kok.getKokId())
                    .realEstateId(kok.getRealEstate().getRealEstateId())
                    .imageUrl(kok.getRealEstate().getRealEstateImages().get(0).getImageUrl())
                    .address(kok.getRealEstate().getAddress())
                    .detailAddress(kok.getRealEstate().getDetailAddress())
                    .estateAgent(kok.getRealEstate().getAgent())
                    .transactionType(kok.getRealEstate().getTransactionType().getDescription())
                    .realEstateType(kok.getRealEstate().getRealEstateType().getDescription())
                    .deposit(kok.getRealEstate().getDeposit())
                    .price(kok.getRealEstate().getPrice())
                    .isZimmed(isZimmed)
                    .build();
        }
    }

    public static GetKokResponse from(List<GetKokWithZimStatus> getKokWithZimStatus) {

        List<Koks> koks = getKokWithZimStatus.stream()
                .map(kokWithZimStatus -> Koks.from(kokWithZimStatus.getKok(), kokWithZimStatus.getZimStatus()))
                .collect(Collectors.toList());

        return GetKokResponse.builder()
                .koks(koks)
                .build();
    }
}
