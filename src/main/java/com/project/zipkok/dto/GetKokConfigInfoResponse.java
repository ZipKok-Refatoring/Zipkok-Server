package com.project.zipkok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetKokConfigInfoResponse {
    private List<String> hilights;
    private List<String> checkedHilights;
    private List<String> furnitureOptions;
    private List<String> checkedFurnitureOptions;
    private ReviewInfo reviewInfo;
    private String direction;
    private List<String> outerImageUrls;
    private List<String> innerImageUrls;
    private List<String> contractImageUrls;
    private List<Option> outerOptions;
    private List<Option> innerOptions;
    private List<Option> contractOptions;

    @Getter
    @Builder
    public static class ReviewInfo {
        private List<String> impressions;
        private List<String> checkedImpressions;
        private Integer facilityStarCount = 0;
        private Integer infraStarCount = 0;
        private Integer structureStarCount = 0;
        private Integer vibeStarCount = 0;
        private String reviewText;
    }

    @Getter
    @Builder
    public static class Option {
        private Long optionId;
        private String optionTitle;
        private Integer orderNumber;

        @JsonProperty("isVisible")
        @Getter(AccessLevel.NONE)
        private boolean isChecked = false;

        private List<DetailOption> detailOptions;

    }

    @Getter
    @Builder
    public static class DetailOption {
        private Long detailOptionId;
        private String detailOptionTitle;

        @JsonProperty("detailOptionIsVisible")
        @Getter(AccessLevel.NONE)
        private boolean isChecked = false;
    }
}
