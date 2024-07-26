package com.project.zipkok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.zipkok.model.Kok;
import com.project.zipkok.model.KokImage;
import com.project.zipkok.model.RealEstate;
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

        public static ImageInfo of(Kok kok) {
            return GetKokDetailResponse.ImageInfo.builder()
                            .imageNumber(kok.getKokImages().size())
                            .imageUrls(kok.getKokImages().stream().map(KokImage::getImageUrl).collect(Collectors.toList()))
                            .build();
        }
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

        RealEstate realEstate = kok.getRealEstate();

        return GetKokDetailResponse.builder()
                .kokId(kok.getKokId())
                .imageInfo(
                        GetKokDetailResponse.ImageInfo.of(kok)
                )
                .address(realEstate.getAddress())
                .detailAddress(realEstate.getDetailAddress())
                .transactionType(realEstate.getTransactionType().toString())
                .deposit(realEstate.getDeposit())
                .price(realEstate.getPrice())
                .detail(realEstate.getDetail())
                .areaSize(realEstate.getAreaSize())
                .pyeongsu((int) realEstate.getPyeongsu())
                .realEstateType(realEstate.getRealEstateType().toString())
                .floorNum(realEstate.getFloorNum())
                .administrativeFee(realEstate.getAdministrativeFee())
                .latitude(realEstate.getLatitude())
                .longitude(realEstate.getLongitude())
                .isZimmed(isZimmed)
                .realEstateId(realEstate.getRealEstateId())
                .build();
    }
}
