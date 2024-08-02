package com.project.zipkok.dto;

import com.project.zipkok.common.enums.RealEstateType;
import com.project.zipkok.common.enums.TransactionType;
import com.project.zipkok.model.TransactionPriceConfig;
import com.project.zipkok.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMyPageResponse {

    private String nickname;

    private String imageUrl;

    private String address;

    private String realEstateType;

    private String transactionType;

    private Long priceMax;

    private Long depositMax;

    private Long priceMin;

    private Long depositMin;

    public static GetMyPageResponse from (User user){
        GetMyPageResponse getMyPageResponse = new GetMyPageResponse();

        getMyPageResponse.setNickname(user.getNickname());
        getMyPageResponse.setImageUrl(user.getProfileImgUrl());
        getMyPageResponse.setRealEstateType(user.getRealEstateType() == null ? null : user.getRealEstateType().toString());

        getMyPageResponse.setAddress(user.getDesireResidence().getAddress());

        TransactionPriceConfig transactionPriceConfig = user.getTransactionPriceConfig();
        if(user.getTransactionType() == null){
            getMyPageResponse.setTransactionType(null);
            getMyPageResponse.setPriceMax(null);
            getMyPageResponse.setPriceMin(null);
            getMyPageResponse.setDepositMax(null);
            getMyPageResponse.setDepositMin(null);
        }
        else{
            getMyPageResponse.setTransactionType(user.getTransactionType().toString());
            String transactionType = user.getTransactionType().getDescription();
            switch (transactionType) {
                case "월세" -> {
                    getMyPageResponse.setPriceMax(transactionPriceConfig.getMPriceMax());
                    getMyPageResponse.setPriceMin(transactionPriceConfig.getMPriceMin());
                    getMyPageResponse.setDepositMax(transactionPriceConfig.getMDepositMax());
                    getMyPageResponse.setDepositMin(transactionPriceConfig.getMDepositMin());
                }
                case "전세" -> {
                    getMyPageResponse.setDepositMax(transactionPriceConfig.getYDepositMax());
                    getMyPageResponse.setDepositMin(transactionPriceConfig.getYDepositMin());
                }
                case "매매" -> {
                    getMyPageResponse.setPriceMax(transactionPriceConfig.getPurchaseMax());
                    getMyPageResponse.setPriceMin(transactionPriceConfig.getPurchaseMin());
                }
            }
        }

        return getMyPageResponse;
    }
}
