package com.project.zipkok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetKokDetailResponse {

    private Long kokId;
    private ImageInfo imageInfo;
    private Long realEstateId;

    @Getter
    @Builder
    public static class ImageInfo {
        private int imageNumber;
        private List<String> imageUrls;
    }

    private String address;
    private String detailAddress;
    private String transactionType;
    private Long deposit;
    private Long price;
    private String detail;
    private Float areaSize;
    private Integer pyeongsu;
    private String realEstateType;
    private Integer floorNum;
    private Integer administrativeFee;
    private Double latitude;
    private Double longitude;

    @Getter(AccessLevel.NONE)
    @JsonProperty("isZimmed")
    private boolean isZimmed;
}
