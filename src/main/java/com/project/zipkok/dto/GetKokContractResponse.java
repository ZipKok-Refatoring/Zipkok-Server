package com.project.zipkok.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetKokContractResponse {

    private List<ContractOptions> options;

    @Getter
    @Builder
    public static class ContractOptions {
        private String option;
        private int orderNumber;
        private List<String> detailOptions;
    }

    private ImageInfo imageInfo;

    @Getter
    @Builder
    public static class ImageInfo {
        private int imageNumber;
        private List<String> imageUrls;
    }
}
