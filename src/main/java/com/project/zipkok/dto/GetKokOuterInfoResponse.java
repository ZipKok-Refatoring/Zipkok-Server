package com.project.zipkok.dto;

import com.project.zipkok.common.enums.OptionCategory;
import com.project.zipkok.model.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        public static OuterOption of(CheckedOption checkedOption, Set<CheckedDetailOption> checkedDetailOptions) {
            return GetKokOuterInfoResponse.OuterOption.builder()
                    .option(checkedOption.getOption().getName())
                    .orderNumber((int) checkedOption.getOption().getOrderNum())
                    .detailOptions(checkedDetailOptions
                            .stream()
                            .map(CheckedDetailOption::getDetailOption)
                            .filter(detailOption -> detailOption.getOption().equals(checkedOption.getOption()))
                            .filter(DetailOption::isVisible)
                            .map(DetailOption::getName)
                            .toList()
                    )
                    .build();
        }
    }

    public static GetKokOuterInfoResponse of(Kok kok) {

        return GetKokOuterInfoResponse.builder()
                .hilights(kok.getCheckedHighlights()
                        .stream()
                        .map(CheckedHighlight::getHighlight)
                        .map(Highlight::getTitle)
                        .toList()
                )
                .options(kok.getCheckedOptions()
                        .stream()
                        .filter(checkedOption -> checkedOption.getOption().getCategory().equals(OptionCategory.OUTER))
                        .filter(checkedOption -> checkedOption.getOption().isVisible())
                        .map(checkedOption -> GetKokOuterInfoResponse.OuterOption.of(checkedOption, kok.getCheckedDetailOptions()))
                        .toList()
                )
                .build();
    }
}
