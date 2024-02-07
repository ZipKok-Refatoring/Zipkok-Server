package com.project.zipkok.dto;

import com.project.zipkok.common.enums.RealEstateType;
import com.project.zipkok.common.enums.TransactionType;
import com.project.zipkok.common.enums.ValidEnum;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchUpdateFilterRequest {

    @ValidEnum(enumClass = TransactionType.class)
    private TransactionType transactionType;

    @ValidEnum(enumClass = RealEstateType.class)
    private RealEstateType realEstateType;

    @NotNull
    @PositiveOrZero
    private Long priceMin;

    @NotNull
    @PositiveOrZero
    private Long priceMax;

    @NotNull
    @PositiveOrZero
    private Long depositMin;

    @NotNull
    @PositiveOrZero
    private Long depositMax;

    @AssertTrue(message = "최소가격은 최대가격을 넘을 수 없습니다.")
    private boolean isSmallerThanMax(){
        return
                this.priceMax >= this.priceMin &&
                this.depositMax >= this.depositMin;
    }
}
