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

    @PositiveOrZero
    private Long priceMin;

    @PositiveOrZero
    private Long priceMax;

    @PositiveOrZero
    private Long depositMin;

    @PositiveOrZero
    private Long depositMax;

    @AssertTrue(message = "최소가격은 최대가격을 넘을 수 없습니다.")
    private boolean isSmallerThanMax(){

        boolean result = false;

        if(this.priceMax != null && this.priceMin != null) {
             result = this.priceMax >= this.priceMin;
        }

        if(this.depositMin != null && this.depositMax != null) {
            result = this.depositMax >= this.depositMin;
        }
        return
                result;
    }
}
