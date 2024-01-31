package com.project.zipkok.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateKokOptionRequest {

    @NotNull
    private List<String> highlights = new ArrayList<>();

    @NotNull
    private List<Option> outerOptions = new ArrayList<>();

    @NotNull
    private List<Option> innerOptions = new ArrayList<>();

    @NotNull
    private List<Option> contractOptions = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Option{

        @NotNull
        @Min(1)
        private Long optionId;

        @NotNull
        @Min(1)
        private Long orderNumber;

        @NotNull
        private boolean isVisible;

        @NotNull
        private List<DetailOption> detailOptions = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailOption{

        @NotNull
        @Min(1)
        private Long detailOptionId;

        @NotNull
        private boolean detailOptionIsVisible;
    }


}
