package com.project.zipkok.dto;

import com.project.zipkok.common.enums.TransactionType;
import com.project.zipkok.model.Kok;
import com.project.zipkok.model.Zim;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetRealEstateOnMapResponse {

    private Filter filter;
    private List<RealEstateInfo> realEstateInfoList = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    public static class Filter{
        private String transactionType;
        private String realEstateType;
        private Long depositMin;
        private Long depositMax;
        private Long priceMin;
        private Long priceMax;
    }

    @Getter
    @Setter
    @Builder
    public static class RealEstateInfo{
        private Long realEstateId;
        private String imageURL;
        private Long deposit;
        private Long price;
        private String transactionType;
        private String realEstateType;
        private String address;
        private String detailAddress;
        private Double latitude;
        private Double longitude;
        private String agent;
        private boolean isZimmed;
        private boolean isKokked;


        public static class RealEstateInfoBuilder{
            private boolean isZimmed;
            private boolean isKokked;

            public RealEstateInfoBuilder isZimmed(Zim zim){
                if(zim == null){
                    this.isZimmed = false;
                } else {
                    this.isZimmed = true;
                }
                return this;
            }

            public RealEstateInfoBuilder isKokked(Kok kok){
                if(kok == null){
                    this.isKokked = false;
                } else {
                    this.isKokked = true;
                }
                return this;
            }
        }
    }


}
