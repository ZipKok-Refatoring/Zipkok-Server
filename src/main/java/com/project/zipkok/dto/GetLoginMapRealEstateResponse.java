package com.project.zipkok.dto;

import com.project.zipkok.model.Kok;
import com.project.zipkok.model.Zim;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetLoginMapRealEstateResponse implements GetMapRealEstateResponse {

    private Filter filter;
    List<GetLoginMapRealEstateResponse.RealEstateInfo> realEstateInfoList = new ArrayList<>();

    @Getter
    @Setter
    @SuperBuilder
    public static class Filter extends GetMapRealEstateResponse.Filter{
        private Long mdepositMin;
        private Long mdepositMax;
        private Long mpriceMin;
        private Long mpriceMax;
        private Long ydepositMin;
        private Long ydepositMax;
        private Long purchaseMin;
        private Long purchaseMax;
    }

    @Getter
    @Setter
    @SuperBuilder
    public static class RealEstateInfo extends GetMapRealEstateResponse.RealEstateInfo {}

}
