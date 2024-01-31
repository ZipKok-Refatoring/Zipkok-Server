package com.project.zipkok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetKokOptionLoadResponse {

    private List<String> highlights = new ArrayList<String>();

    private List<Option> outerOptions = new ArrayList<Option>();

    private List<Option> innerOptions = new ArrayList<Option>();

    private List<Option> contractOptions = new ArrayList<Option>();

    public void addHighlight(String highlight){
        this.highlights.add(highlight);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Option{

        private Long optionId;

        private String optionTitle;

        private Long orderNumber;

        @JsonProperty("isVisible")
        @Getter(AccessLevel.NONE)
        private boolean isVisible;

        private List<DetailOption> detailOptions = new ArrayList<DetailOption>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailOption{

        private Long detailOptionId;

        private String detailOptionTitle;

        private boolean detailOptionIsVisible;
    }
}
