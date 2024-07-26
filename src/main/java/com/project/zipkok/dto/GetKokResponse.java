package com.project.zipkok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

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
}
