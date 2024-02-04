package com.project.zipkok.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.zipkok.common.enums.RealEstateType;
import com.project.zipkok.common.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class GetRealEstateResponse {

    private Long realEstateId;

    private ImageInfo imageInfo;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class ImageInfo {
        private int imageNumber;
        private List<String> imageURL;
    }

    private String address;

    private String detailAddress;

    private String transactionType;

    private Long deposit;

    private Long price;

    private String detail;

    private Float areaSize;

    private Long pyeongsu;

    private String realEstateType;

    private Integer floorNum;

    private Integer administrativeFee;

    private Double latitude;

    private Double longitude;

    @JsonProperty("isZimmed")
    private boolean isZimmed;

    @JsonProperty("isKokked")
    private boolean isKokked;

    private List<RealEstateBriefInfo> neighborRealEstates;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class RealEstateBriefInfo {
        private Long realEstateId;
        private String imageUrl;
        private String address;
        private Long deposit;
        private Long price;
    }

    public boolean getIsKokked() {
        return isKokked;
    }

    public boolean getIsZimmed() {
        return isZimmed;
    }
}
