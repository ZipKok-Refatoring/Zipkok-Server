package com.project.zipkok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.zipkok.common.enums.OptionCategory;
import com.project.zipkok.model.Highlight;
import lombok.*;

import java.util.*;
import java.util.stream.Stream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetKokOptionLoadResponse {

    private List<String> highlights;
    
    private List<Option> outerOptions;

    private List<Option> innerOptions;

    private List<Option> contractOptions;

    public static GetKokOptionLoadResponse of(List<Highlight> highlightList, List<com.project.zipkok.model.Option> optionList) {
        return GetKokOptionLoadResponse.builder()
                .highlights(highlightList.stream().map(Highlight::getTitle).toList())
                .outerOptions(optionList.stream().filter(option -> option.getCategory().equals(OptionCategory.OUTER)).map(Option::from).toList())
                .innerOptions(optionList.stream().filter(option -> option.getCategory().equals(OptionCategory.INNER)).map(Option::from).toList())
                .contractOptions(optionList.stream().filter(option -> option.getCategory().equals(OptionCategory.CONTRACT)).map(Option::from).toList())
                .build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Option{

        private Long optionId;

        private String optionTitle;

        private Long orderNumber;

        @JsonProperty("isVisible")
        @Getter(AccessLevel.NONE)
        private boolean isVisible;

        private List<DetailOption> detailOptions;

        public static Option from(com.project.zipkok.model.Option option){
            return Option.builder()
                    .optionId(option.getOptionId())
                    .optionTitle(option.getName())
                    .orderNumber(option.getOrderNum())
                    .isVisible(option.isVisible())
                    .detailOptions(option.getDetailOptions().stream().map(DetailOption::from).toList())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailOption{

        private Long detailOptionId;

        private String detailOptionTitle;

        private boolean detailOptionIsVisible;

        public static DetailOption from(com.project.zipkok.model.DetailOption detailOption){
            return DetailOption.builder()
                    .detailOptionId(detailOption.getDetailOptionId())
                    .detailOptionTitle(detailOption.getName())
                    .detailOptionIsVisible(detailOption.isVisible())
                    .build();
        }
    }
}
