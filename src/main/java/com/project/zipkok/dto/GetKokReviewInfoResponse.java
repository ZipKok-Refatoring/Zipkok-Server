package com.project.zipkok.dto;

import com.project.zipkok.model.Kok;
import com.project.zipkok.model.Star;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GetKokReviewInfoResponse {
    private List<String> impressions;
    private int facilityStarCount;
    private int infraStarCount;
    private int structureStarCount;
    private int vibeStarCount;
    private String reviewText;

    public static GetKokReviewInfoResponse of(Kok kok){

        Star star = new Star();

        return GetKokReviewInfoResponse.builder()
                .impressions(kok.getCheckedImpressions().stream()
                        .map(checkedImpression ->
                                checkedImpression
                                        .getImpression()
                                        .getImpressionTitle()
                        )
                        .toList()
                )
                .facilityStarCount(star.getFacilityStar())
                .infraStarCount(star.getInfraStar())
                .structureStarCount(star.getStructureStar())
                .vibeStarCount(star.getVibeStar())
                .reviewText(kok.getReview())
                .build();
    }
}
