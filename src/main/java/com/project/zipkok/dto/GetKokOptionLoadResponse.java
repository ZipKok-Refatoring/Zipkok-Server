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

    private List<Option> outerOptions = new ArrayList<>();

    private List<Option> innerOptions = new ArrayList<>();

    private List<Option> contractOptions = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Option{

        private Long optionId;

        private String optionTitle;

        private Long orderNumber;

        @JsonProperty("isVisible")
        @Getter(AccessLevel.NONE)
        private boolean isVisible;

        private List<DetailOption> detailOptions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailOption{

        private Long detailOptionId;

        private String detailOptionTitle;

        private boolean detailOptionIsVisible;
    }

    public static GetKokOptionLoadResponse from(Set<Highlight> highlights, Set<com.project.zipkok.model.Option> options) {

        GetKokOptionLoadResponse getKokOptionLoadResponse =
                GetKokOptionLoadResponse.builder()
                    .highlights(
                            highlights.stream()
                                    .map(Highlight::getTitle)
                                    .toList()
                    )
                    .outerOptions(
                            options.stream()
                                    .filter(option -> option.getCategory()== OptionCategory.OUTER)
                                    .map(GetKokOptionLoadResponse::from)
                                    .toList()
                    )
                    .innerOptions(
                            options.stream()
                                    .filter(option -> option.getCategory()== OptionCategory.INNER)
                                    .map(GetKokOptionLoadResponse::from)
                                    .toList()
                    )
                    .contractOptions(
                            options.stream()
                                    .filter(option -> option.getCategory()== OptionCategory.CONTRACT)
                                    .map(GetKokOptionLoadResponse::from)
                                    .toList()
                    )
                    .build();

        //Batch size 지정해두었기 때문에 in 절 사용해서 쿼리를 날림.
        List<com.project.zipkok.model.DetailOption> detailOptions = options.stream()
                .flatMap(option -> option.getDetailOptions().stream())
                .toList();

        Stream.of(getKokOptionLoadResponse.outerOptions, getKokOptionLoadResponse.innerOptions, getKokOptionLoadResponse.contractOptions)
                .flatMap(Collection::stream)
                .forEach(option -> {
                    detailOptions.forEach(detailOption -> {
                        if(option.getOptionId().equals(detailOption.getOption().getOptionId())){
                            option.getDetailOptions().add(GetKokOptionLoadResponse.from(detailOption));
                        }
                    });
                });

        return getKokOptionLoadResponse;
    }

    private static Option from (com.project.zipkok.model.Option option) {
        return Option.builder()
                .optionId(option.getOptionId())
                .optionTitle(option.getName())
                .orderNumber(option.getOrderNum())
                .isVisible(option.isVisible())
                .detailOptions(new ArrayList<>())
                .build();
    }

    private static DetailOption from (com.project.zipkok.model.DetailOption detailOption) {
        return DetailOption.builder()
                .detailOptionId(detailOption.getDetailOptionId())
                .detailOptionTitle(detailOption.getName())
                .detailOptionIsVisible(detailOption.isVisible())
                .build();
    }
}
