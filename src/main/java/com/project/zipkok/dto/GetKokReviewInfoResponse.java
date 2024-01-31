package com.project.zipkok.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetKokReviewInfoResponse {
    private List<String> impressions;
    private int facilityStarCount;
    private int infraStarCount;
    private int structureStarCount;
    private int vibeStarCount;
    private String reviewText;
}
