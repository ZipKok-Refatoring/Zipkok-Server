package com.project.zipkok.service;

import com.project.zipkok.common.enums.RealEstateType;
import com.project.zipkok.common.enums.TransactionType;
import com.project.zipkok.common.exception.RealEstateException;
import com.project.zipkok.dto.*;
import com.project.zipkok.model.RealEstate;
import com.project.zipkok.model.RealEstateImage;
import com.project.zipkok.model.User;
import com.project.zipkok.repository.KokRepository;
import com.project.zipkok.repository.RealEstateRepository;
import com.project.zipkok.repository.UserRepository;
import com.project.zipkok.repository.ZimRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealEstateService {

    private final RealEstateRepository realEstateRepository;
    private final UserRepository userRepository;
    private final ZimRepository zimRepository;
    private final KokRepository kokRepository;


    @Transactional
    public GetRealEstateResponse getRealEstateInfo(Long userId, Long realEstateId) {

        log.info("[RealEstateService.getRealEstateInfo]");

//        try {
            RealEstate realEstate = realEstateRepository.findById(realEstateId).get();
            User user = userRepository.findByUserId(userId);

            List<String> realEstateImages = new ArrayList<>();

            realEstateImages.add(realEstate.getImageUrl());

            realEstate.getRealEstateImages()
                    .stream()
                    .map(RealEstateImage::getImageUrl)
                    .map(realEstateImages::add);


            boolean isZimmed = false;
            boolean isKokked = false;

            if (zimRepository.findFirstByUserAndRealEstate(user, realEstate) != null) {
                isZimmed = true;
            }

            if (kokRepository.findFirstByUserAndRealEstate(user, realEstate) != null) {
                isKokked = true;
            }

            List<GetRealEstateResponse.RealEstateBriefInfo> neighborRealEstates = realEstateRepository.findAllByProximity(realEstate.getLatitude(), realEstate.getLongitude())
                    .stream()
                    .map(result -> GetRealEstateResponse.RealEstateBriefInfo.builder()
                            .realEstateId(result.getRealEstateId())
                            .imageUrl(result.getImageUrl())
                            .address(result.getAddress())
                            .deposit(result.getDeposit())
                            .price(result.getPrice())
                            .build())
                    .toList();


            GetRealEstateResponse response = GetRealEstateResponse.builder()
                    .realEstateId(realEstate.getRealEstateId())
                    .imageInfo(new GetRealEstateResponse.ImageInfo(realEstateImages.size(), realEstateImages))
                    .address(realEstate.getAddress())
                    .detailAddress(realEstate.getDetailAddress())
                    .transactionType(realEstate.getTransactionType().toString())
                    .deposit(realEstate.getDeposit())
                    .price(realEstate.getPrice())
                    .detail(realEstate.getDetail())
                    .areaSize(realEstate.getAreaSize())
                    .pyeongsu(realEstate.getPyeongsu())
                    .realEstateType(realEstate.getRealEstateType().toString())
                    .floorNum(realEstate.getFloorNum())
                    .administrativeFee(realEstate.getAdministrativeFee())
                    .latitude(realEstate.getLatitude())
                    .longitude(realEstate.getLongitude())
                    .isZimmed(isZimmed)
                    .isKokked(isKokked)
                    .neighborRealEstates(neighborRealEstates)
                    .build();

            return response;
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            throw new RealEstateException(INVALID_PROPERTY_ID);
//        }
    }

    public PostRealEstateResponse registerRealEstate(long userId, PostRealEstateRequest postRealEstateRequest) {

//        try {
            User user = userRepository.findByUserId(userId);

            RealEstate realEstate = RealEstate.builder()
                    .imageUrl(null)
                    .address(postRealEstateRequest.getAddress())
                    .latitude(postRealEstateRequest.getLatitude())
                    .longitude(postRealEstateRequest.getLongitude())
                    .transactionType(postRealEstateRequest.getTransactionType())
                    .deposit(postRealEstateRequest.getDeposit())
                    .price(postRealEstateRequest.getPrice())
                    .administrativeFee(postRealEstateRequest.getAdministrativeFee())
                    .detail(postRealEstateRequest.getRealEstateName())
                    .areaSize(null)
                    .pyeongsu(postRealEstateRequest.getPyeongsu())
                    .realEstateType(postRealEstateRequest.getRealEstateType())
                    .floorNum(postRealEstateRequest.getFloorNum())
                    .user(user)
                    .agent(null)
                    .detailAddress(postRealEstateRequest.getDetailAddress())
                    .status("active")
                    .build();

            Long realEstateId = realEstateRepository.save(realEstate).getRealEstateId();

            return new PostRealEstateResponse(realEstateId);
//        } catch (Exception e) {
//            throw new RealEstateException(PROPERTY_REGISTRATION_FAILURE);
//        }
    }

    public GetMapRealEstateResponse getRealEstate(long userId, GetRealEstateOnMapRequest getRealEstateOnMapRequest) {
        log.info("{UserService.getRealEstate}");


        List<RealEstate> realEstateList = this.realEstateRepository.findByLatitudeBetweenAndLongitudeBetween(getRealEstateOnMapRequest.getSouthWestLat(),getRealEstateOnMapRequest.getNorthEastLat(),getRealEstateOnMapRequest.getSouthWestLon(),getRealEstateOnMapRequest.getNorthEastLon());
        User user = this.userRepository.findByUserId(userId);

        if(realEstateList == null){
            throw new RealEstateException(PROPERTY_NOT_FOUND);
        }

        TransactionType userTransactionType;
        RealEstateType userRealEstateType;

        if(userId == -1) {
            userTransactionType = getRealEstateOnMapRequest.getTransactionType();
            userRealEstateType = getRealEstateOnMapRequest.getRealEstateType();

            GetTempRealEstateResponse getTempRealEstateResponse = new GetTempRealEstateResponse();

            getTempRealEstateResponse.setFilter(GetTempRealEstateResponse.Filter.builder()
                    .transactionType(userTransactionType)
                    .realEstateType(userRealEstateType)
                    .depositMin(getRealEstateOnMapRequest.getDepositMin())
                    .depositMax(getRealEstateOnMapRequest.getDepositMax())
                    .priceMin(getRealEstateOnMapRequest.getPriceMin())
                    .priceMax(getRealEstateOnMapRequest.getPriceMax())
                    .build()
            );

            if(userTransactionType != null && userRealEstateType != null){
                realEstateList = realEstateList
                                    .stream()
                                    .filter(result -> result.getTransactionType().equals(userTransactionType) && result.getRealEstateType().equals(userRealEstateType))
                                    .filter(result -> filterPriceConfig(result, getTempRealEstateResponse.getFilter(), false))
                                    .toList();
            }


            List<GetTempRealEstateResponse.RealEstateInfo> realEstateInfoList = realEstateList
                    .stream()
                    .filter(result -> result.getUser() == null)
                    .map(result -> GetTempRealEstateResponse.RealEstateInfo.builder()
                            .realEstateId(result.getRealEstateId())
                            .imageURL(result.getImageUrl())
                            .deposit(result.getDeposit())
                            .price(result.getPrice())
                            .transactionType(result.getTransactionType().toString())
                            .realEstateType(result.getRealEstateType().toString())
                            .address(result.getAddress())
                            .detailAddress(result.getDetailAddress())
                            .latitude(result.getLatitude())
                            .longitude(result.getLongitude())
                            .agent(result.getAgent() == null ? "직접 등록한 매물" : result.getAgent())
                            .isZimmed(false)
                            .isKokked(false)
                            .build())
                    .collect(Collectors.toList());

            getTempRealEstateResponse.setRealEstateInfoList(realEstateInfoList);
            return getTempRealEstateResponse;

        } else {

            GetLoginMapRealEstateResponse getLoginMapRealEstateResponse = new GetLoginMapRealEstateResponse();

            userTransactionType = user.getTransactionType();
            userRealEstateType = user.getRealEstateType();

            //filter 정보 mapping
            getLoginMapRealEstateResponse.setFilter(GetLoginMapRealEstateResponse.Filter.builder()
                    .transactionType(userTransactionType)
                    .realEstateType(userRealEstateType)
                    .mdepositMin(user.getTransactionPriceConfig().getMDepositMin())
                    .mdepositMax(user.getTransactionPriceConfig().getMDepositMax())
                    .mpriceMin(user.getTransactionPriceConfig().getMPriceMin())
                    .mpriceMax(user.getTransactionPriceConfig().getMPriceMax())
                    .ydepositMin(user.getTransactionPriceConfig().getYDepositMin())
                    .ydepositMax(user.getTransactionPriceConfig().getYDepositMax())
                    .purchaseMin(user.getTransactionPriceConfig().getPurchaseMin())
                    .purchaseMax(user.getTransactionPriceConfig().getPurchaseMax())
                    .build()
            );

            if(userTransactionType != null && userRealEstateType != null){
                realEstateList = realEstateList
                        .stream()
                        .filter(result -> result.getTransactionType().equals(userTransactionType) && result.getRealEstateType().equals(userRealEstateType))
                        .filter(result -> filterPriceConfig(result, getLoginMapRealEstateResponse.getFilter(), true))
                        .toList();
            }

            //realEstateInfo mapping
            List<GetLoginMapRealEstateResponse.RealEstateInfo> realEstateInfoList = realEstateList
                    .stream()
                    .filter(result -> result.getUser() == null || result.getUser().getUserId().equals(userId))
                    .map(result -> GetLoginMapRealEstateResponse.RealEstateInfo.builder()
                            .realEstateId(result.getRealEstateId())
                            .imageURL(result.getImageUrl())
                            .deposit(result.getDeposit())
                            .price(result.getPrice())
                            .transactionType(result.getTransactionType().toString())
                            .realEstateType(result.getRealEstateType().toString())
                            .address(result.getAddress())
                            .detailAddress(result.getDetailAddress())
                            .latitude(result.getLatitude())
                            .longitude(result.getLongitude())
                            .agent(result.getAgent() == null ? "직접 등록한 매물" : result.getAgent())
                            .isZimmed(this.zimRepository.findFirstByUserAndRealEstate(user, result) != null)
                            .isKokked(this.kokRepository.findFirstByUserAndRealEstate(user, result) != null)
                            .build())
                    .collect(Collectors.toList());

            getLoginMapRealEstateResponse.setRealEstateInfoList(realEstateInfoList);
            return getLoginMapRealEstateResponse;
        }
    }

    private boolean filterPriceConfig(RealEstate realEstate, GetMapRealEstateResponse.Filter filter, boolean isLogin){
        String transactionType = realEstate.getTransactionType().getDescription();
        long deposit = realEstate.getDeposit();
        long price = realEstate.getPrice();

        if(isLogin){
            GetLoginMapRealEstateResponse.Filter loginFilter = (GetLoginMapRealEstateResponse.Filter) filter;

            if(transactionType.equals("월세")){
                if(deposit < loginFilter.getMdepositMin() || deposit > loginFilter.getMdepositMax()){ return false; }
                if(price < loginFilter.getMpriceMin() || price > loginFilter.getMpriceMax()) { return false; }
            }else if(transactionType.equals("전세")) {
                if(deposit < loginFilter.getYdepositMin() || deposit > loginFilter.getYdepositMax()){ return false; }
            }else if(transactionType.equals("매매")){
                if(price < loginFilter.getPurchaseMin() || price > loginFilter.getPurchaseMax()) { return false; }
            }
        }
        else{
            GetTempRealEstateResponse.Filter loginFilter = (GetTempRealEstateResponse.Filter) filter;

            if(transactionType.equals("월세")){
                if(deposit < loginFilter.getDepositMin() || deposit > loginFilter.getDepositMax()){ return false; }
                if(price < loginFilter.getPriceMin() || price > loginFilter.getPriceMax()) { return false; }
            }else if(transactionType.equals("전세")) {
                if(deposit < loginFilter.getDepositMin() || deposit > loginFilter.getDepositMax()){ return false; }
            }else if(transactionType.equals("매매")){
                if(price < loginFilter.getPriceMin() || price > loginFilter.getPriceMax()) { return false; }
            }

        }


        return true;
    }
}
