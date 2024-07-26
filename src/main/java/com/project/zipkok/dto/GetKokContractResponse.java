package com.project.zipkok.dto;

import com.project.zipkok.common.enums.OptionCategory;
import com.project.zipkok.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.http.annotation.Contract;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        public static ContractOptions of(CheckedOption checkedOption, Set<CheckedDetailOption> checkedDetailOptions) {
            return GetKokContractResponse.ContractOptions.builder()
                    .option(checkedOption.getOption().getName())
                    .orderNumber((int) checkedOption.getOption().getOrderNum())
                    .detailOptions(checkedDetailOptions
                            .stream()
                            .map(CheckedDetailOption::getDetailOption)
                            .filter(detailOption -> detailOption.getOption().equals(checkedOption.getOption()))
                            .filter(DetailOption::isVisible)
                            .map(DetailOption::getName)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    private ImageInfo imageInfo;

    @Getter
    @Builder
    public static class ImageInfo {
        private int imageNumber;
        private List<String> imageUrls;
    }

    public static GetKokContractResponse of(Kok kok) {
        List<String> contractImages = kok.getKokImages()
                .stream()
                .filter(kokImage -> kokImage.getCategory().equals(OptionCategory.CONTRACT.getDescription()))
                .map(KokImage::getImageUrl)
                .toList();

        return GetKokContractResponse.builder()
                .options(kok.getCheckedOptions()
                        .stream()
                        .filter(checkedOption -> checkedOption.getOption().getCategory().equals(OptionCategory.CONTRACT))
                        .filter(checkedOption -> checkedOption.getOption().isVisible())
                        .map(checkedOption -> GetKokContractResponse.ContractOptions.of(checkedOption, kok.getCheckedDetailOptions()))
                        .toList()
                )
                .imageInfo(GetKokContractResponse.ImageInfo.builder()
                        .imageNumber(contractImages.size())
                        .imageUrls(contractImages)
                        .build())
                .build();
    }
}
