package com.project.zipkok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class GetKokResponse {

    private List<Koks> koks;

    @Getter
    public static class Koks {
        private Long kokId;
        private String imageUrl;

        private String address;

        private String detailAddress;

        private String estateAgent;

        private String transactionType;

        private String realEstateType;

        private Long deposit;

        private Long price;

        private boolean isZimmed;
    }

    private Meta meta;

    public static class Meta {
        @JsonProperty("is_End")
        private boolean isEnd;

        @JsonProperty("current_page")
        private Integer currentPage;

        @JsonProperty("total_page")
        private Integer totalPage;

    }
}
