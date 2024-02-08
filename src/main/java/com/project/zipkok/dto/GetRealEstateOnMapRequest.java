package com.project.zipkok.dto;

import com.project.zipkok.common.enums.RealEstateType;
import com.project.zipkok.common.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetRealEstateOnMapRequest {
    @NotNull
    private Double southWestLat;

    @NotNull
    private Double southWestLon;

    @NotNull
    private Double northEastLat;

    @NotNull
    private Double northEastLon;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private RealEstateType realEstateType;


    private Long depositMin;
    private Long depositMax;
    private Long priceMin;
    private Long priceMax;

    @AssertTrue(message = "위도, 경도 정보가 잘못 입력되었습니다.(min > max 오류)")
    public boolean isSmallerThan(){
        return
                this.southWestLat <= this.northEastLat &&
                        this.southWestLon <= this.northEastLon;
    }


}
