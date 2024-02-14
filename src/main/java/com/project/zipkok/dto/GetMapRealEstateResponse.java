package com.project.zipkok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.zipkok.common.enums.RealEstateType;
import com.project.zipkok.common.enums.TransactionType;
import com.project.zipkok.model.Kok;
import com.project.zipkok.model.Zim;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

public interface GetMapRealEstateResponse {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    @AllArgsConstructor
    public static class Filter{
        @Enumerated(EnumType.STRING)
        private TransactionType transactionType;
        @Enumerated(EnumType.STRING)
        private RealEstateType realEstateType;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    @AllArgsConstructor
    public static class RealEstateInfo {
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

        @JsonProperty("isZimmed")
        @Getter(AccessLevel.NONE)
        private boolean isZimmed;

        @JsonProperty("isKokked")
        @Getter(AccessLevel.NONE)
        private boolean isKokked;

    }


//        public static class RealEstateInfoBuilder{
//            private boolean isZimmed;
//            private boolean isKokked;
//
//            public RealEstateInfo.RealEstateInfoBuilder isZimmed(Zim zim){
//                if(zim == null){
//                    this.isZimmed = false;
//                } else {
//                    this.isZimmed = true;
//                }
//                return this;
//            }
//
//            public RealEstateInfo.RealEstateInfoBuilder isKokked(Kok kok){
//                if(kok == null){
//                    this.isKokked = false;
//                } else {
//                    this.isKokked = true;
//                }
//                return this;
//            }
//        }

}
