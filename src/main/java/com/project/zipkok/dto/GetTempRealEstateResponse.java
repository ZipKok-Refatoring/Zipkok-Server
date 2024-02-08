package com.project.zipkok.dto;

import com.project.zipkok.model.Kok;
import com.project.zipkok.model.Zim;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GetTempRealEstateResponse implements GetMapRealEstateResponse {

    public Filter filter;

    List<RealEstateInfo> realEstateInfoList = new ArrayList<>();

    @Getter
    @SuperBuilder
    public static class Filter extends GetMapRealEstateResponse.Filter {

        private Long depositMin;
        private Long depositMax;
        private Long priceMin;
        private Long priceMax;
    }

    @Getter
    @Setter
    @SuperBuilder
    public static class RealEstateInfo extends GetMapRealEstateResponse.RealEstateInfo {}


}
