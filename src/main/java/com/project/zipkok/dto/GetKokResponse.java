package com.project.zipkok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.zipkok.model.Kok;
import com.project.zipkok.model.Zim;
import lombok.Builder;
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
    }

    private Meta meta;

    @Builder
    public static class Meta {
        @JsonProperty("is_End")
        private boolean isEnd;

        @JsonProperty("current_page")
        private Integer currentPage;

        @JsonProperty("total_page")
        private Integer totalPage;

    }

    public static GetKokResponse of(List<Kok> responseKoks, Meta meta, List<Zim> zims) {
        return GetKokResponse.builder()
                .koks(responseKoks.stream().map(kok -> GetKokResponse.Koks.builder()
                                .kokId(kok.getKokId())
                                .realEstateId(kok.getRealEstate().getRealEstateId())
                                .imageUrl(Optional.ofNullable(kok.getRealEstate().getRealEstateImages())
                                        .filter(images -> !images.isEmpty())
                                        .map(images -> images.get(0).getImageUrl())
                                        .orElse(null))
                                .address(kok.getRealEstate().getAddress())
                                .detailAddress(kok.getRealEstate().getDetailAddress())
                                .estateAgent(kok.getRealEstate().getAgent())
                                .transactionType(kok.getRealEstate().getTransactionType().toString())
                                .realEstateType(kok.getRealEstate().getRealEstateType().toString())
                                .deposit(kok.getRealEstate().getDeposit())
                                .price(kok.getRealEstate().getPrice())
                                .isZimmed(zims.stream().anyMatch(zim -> zim.getRealEstate().equals(kok.getRealEstate())))
                                .build())
                        .collect(Collectors.toList()))
                .meta(meta)
                .build();
    }
}
