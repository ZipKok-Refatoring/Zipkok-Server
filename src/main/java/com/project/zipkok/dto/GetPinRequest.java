package com.project.zipkok.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetPinRequest {
    @NotNull
    private Double southWestLat;

    @NotNull
    private Double southWestLon;

    @NotNull
    private Double northEastLat;

    @NotNull
    private Double northEastLon;

    @AssertTrue(message = "위도, 경도 정보가 잘못 입력되었습니다.(min > max 오류)")
    public boolean isSmallerThan(){
        return
                this.southWestLat <= this.northEastLat &&
                        this.southWestLon <= this.northEastLon;
    }
}
