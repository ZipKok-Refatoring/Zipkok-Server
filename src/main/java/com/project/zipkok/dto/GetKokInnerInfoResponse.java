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

        public static InnerOption of(CheckedOption checkedOption, Set<CheckedDetailOption> checkedOptions) {
            return GetKokInnerInfoResponse.InnerOption.builder()
                    .option(checkedOption.getOption().getName())
                    .orderNumber((int) checkedOption.getOption().getOrderNum())
                    .detailOptions(checkedOptions
                            .stream()
                            .map(CheckedDetailOption::getDetailOption)
                            .filter(detailOption -> detailOption.getOption().equals(checkedOption.getOption()))
                            .filter(DetailOption::isVisible)
                            .map(DetailOption::getName)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    public static GetKokInnerInfoResponse of(Kok kok) {
        return GetKokInnerInfoResponse.builder()
                .furnitureOptions(kok.getCheckedFurnitures()
                        .stream()
                        .map(CheckedFurniture::getFurnitureOption)
                        .map(FurnitureOption::getFurnitureName)
                        .toList()
                )
                .direction(kok.getDirection())
                .options(kok.getCheckedOptions()
                        .stream()
                        .filter(checkedOption -> checkedOption.getOption().getCategory().equals(OptionCategory.INNER))
                        .filter(checkedOption -> checkedOption.getOption().isVisible())
                        .map(checkedOption -> GetKokInnerInfoResponse.InnerOption.of(checkedOption, kok.getCheckedDetailOptions()))
                        .collect(Collectors.toList()
                        )
                )
                .build();
    }
}
