package com.project.zipkok.dto;

import com.project.zipkok.common.enums.RealEstateType;
import com.project.zipkok.common.enums.ValidEnum;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchOnBoardingRequest {

    @NotBlank
    @Size(max = 200)
    private String address;

    @NotNull
    private double latitude;

    @NotNull
    private double longitude;

    @ValidEnum(enumClass = RealEstateType.class)
    private RealEstateType realEstateType;

    @NotNull
    @PositiveOrZero
    private long mpriceMin;

    @NotNull
    @PositiveOrZero
    private long mpriceMax;

    @NotNull
    @PositiveOrZero
    private long mdepositMin;

    @NotNull
    @PositiveOrZero
    private long mdepositMax;

    @NotNull
    @PositiveOrZero
    private long ydepositMin;

    @NotNull
    @PositiveOrZero
    private long ydepositMax;

    @NotNull
    @PositiveOrZero
    private long purchaseMin;

    @NotNull
    @PositiveOrZero
    private long purchaseMax;

    @AssertTrue(message = "최소가격은 최대가격을 넘을 수 없습니다.")
    private boolean isSmallerthanMax(){
        return
                this.mpriceMax > this.mpriceMin &&
                this.mdepositMax > this.mdepositMin &&
                this.ydepositMax > this.ydepositMin &&
                this.purchaseMax > this.purchaseMin;
    }
}
