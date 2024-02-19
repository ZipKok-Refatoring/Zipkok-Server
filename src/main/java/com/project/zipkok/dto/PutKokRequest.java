package com.project.zipkok.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PutKokRequest {

    @NotNull
    @Positive
    private Long kokId;

    @NotNull
    private List<String> checkedHighlights = new ArrayList<>();

    @NotNull
    private List<String> checkedFurnitureOptions = new ArrayList<>();

    @Size(max = 10)
    private String direction;

    @Valid
    private PostKokRequest.ReviewInfo reviewInfo;

    @Getter
    public static class ReviewInfo {

        @NotNull
        private List<String> checkedImpressions = new ArrayList<>();

        @Min(0)
        @Max(5)
        private int facilityStarCount;

        @Min(0)
        @Max(5)
        private int infraStarCount;

        @Min(0)
        @Max(5)
        private int structureStarCount;

        @Min(0)
        @Max(5)
        private int vibeStarCount;

        @Size(max = 500)
        private String reviewText;

    }

    @NotNull
    @Valid
    private List<PostKokRequest.Option> checkedOuterOptions = new ArrayList<>();

    @NotNull
    @Valid
    private List<PostKokRequest.Option> checkedInnerOptions = new ArrayList<>();

    @NotNull
    @Valid
    private List<PostKokRequest.Option> checkedContractOptions = new ArrayList<>();

    @Getter
    public static class Option {

        @NotNull
        @Positive
        private Long optionId;

        @NotNull
        private List<Long> checkedDetailOptionIds = new ArrayList<>();
    }
}
