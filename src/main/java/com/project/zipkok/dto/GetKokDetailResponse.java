package com.project.zipkok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.zipkok.model.Kok;
import com.project.zipkok.model.KokImage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

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

    public static GetKokDetailResponse of(Kok kok, boolean isZimmed) {
        return GetKokDetailResponse.builder()
                .kokId(kok.getKokId())
                .imageInfo(GetKokDetailResponse.ImageInfo.builder().
                        imageNumber(kok.getKokImages().size())
                        .imageUrls(kok.getKokImages().stream().map(KokImage::getImageUrl).collect(Collectors.toList()))
                        .build())
                .address(kok.getRealEstate().getAddress())
                .detailAddress(kok.getRealEstate().getDetailAddress())
                .transactionType(kok.getRealEstate().getTransactionType().toString())
                .deposit(kok.getRealEstate().getDeposit())
                .price(kok.getRealEstate().getPrice())
                .detail(kok.getRealEstate().getDetail())
                .areaSize(kok.getRealEstate().getAreaSize())
                .pyeongsu((int) kok.getRealEstate().getPyeongsu())
                .realEstateType(kok.getRealEstate().getRealEstateType().toString())
                .floorNum(kok.getRealEstate().getFloorNum())
                .administrativeFee(kok.getRealEstate().getAdministrativeFee())
                .latitude(kok.getRealEstate().getLatitude())
                .longitude(kok.getRealEstate().getLongitude())
                .isZimmed(isZimmed)
                .realEstateId(kok.getRealEstate().getRealEstateId())
                .build();
    }
}
