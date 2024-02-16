package com.project.zipkok.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PinInfo {

    @NotNull @Positive
    private Long id;

    @NotNull @Max(12)
    private String name;

    @Valid
    private PinAddressInfo address;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class PinAddressInfo {

        @NotNull @Max(200)
        private String addressName;

        @NotNull
        private Double x;

        @NotNull
        private Double y;
    }
}
