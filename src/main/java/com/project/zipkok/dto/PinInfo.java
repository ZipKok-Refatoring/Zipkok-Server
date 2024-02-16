package com.project.zipkok.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PinInfo {

    private Long id;
    private String name;
    private PinAddressInfo address;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PinAddressInfo {
        private String addressName;
        private Double x;
        private Double y;
    }
}
