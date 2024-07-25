package com.project.zipkok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostOrPutKokRequest {

    @Positive
    private Long realEstateId;

    @Positive
    private Long kokId;

    @NotNull
    private List<String> checkedHighlights = new ArrayList<>();

    @NotNull
    private List<String> checkedFurnitureOptions = new ArrayList<>();

    @Size(max = 10)
    private String direction;

    @Valid
    private ReviewInfo reviewInfo;

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
    private List<Option> checkedOuterOptions = new ArrayList<>();

    @NotNull
    @Valid
    private List<Option> checkedInnerOptions = new ArrayList<>();

    @NotNull
    @Valid
    private List<Option> checkedContractOptions = new ArrayList<>();

    @Getter
    public static class Option {

        @NotNull
        @Positive
        private Long optionId;

        @NotNull
        private List<Long> checkedDetailOptionIds = new ArrayList<>();
    }


}
