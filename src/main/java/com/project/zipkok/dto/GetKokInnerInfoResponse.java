package com.project.zipkok.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetKokInnerInfoResponse {

    private List<String> furnitureOptions;

    private String direction;

    private List<InnerOption> options;

    @Getter
    @Builder
    public static class InnerOption {
        private String option;
        private int orderNumber;
        private List<String> detailOptions;
    }
}
