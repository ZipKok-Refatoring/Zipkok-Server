package com.project.zipkok.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetKokOuterInfoResponse {
    private List<String> hilights;
    private List<OuterOption> options;

    @Getter
    @Builder
    public static class OuterOption {
        private String option;
        private int orderNumber;
        private List<String> detailOptions;
    }
}
