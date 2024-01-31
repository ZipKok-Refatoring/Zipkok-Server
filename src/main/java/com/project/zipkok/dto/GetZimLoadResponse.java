package com.project.zipkok.dto;

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
public class GetZimLoadResponse {

    List<RealEstateInfo> realEstateInfo = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class RealEstateInfo{

        private Long realEstateId;

        private String imageURL;

        private Long deposit;

        private Long price;

        private String address;

        private String agent;
    }

    public void addRealEstateInfo(long realEstateId, String imageURL, Long deposit, Long price, String address, String agent){
        this.realEstateInfo.add(new RealEstateInfo(realEstateId,imageURL,deposit,price,address,agent));
    }

}
