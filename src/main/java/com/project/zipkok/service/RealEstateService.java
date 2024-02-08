package com.project.zipkok.service;

import com.project.zipkok.common.exception.DatabaseException;
import com.project.zipkok.common.exception.InternalServerErrorException;
import com.project.zipkok.common.exception.RealEstateException;
import com.project.zipkok.dto.GetRealEstateOnMapResponse;
import com.project.zipkok.dto.GetRealEstateResponse;
import com.project.zipkok.dto.PostRealEstateRequest;
import com.project.zipkok.dto.PostRealEstateResponse;
import com.project.zipkok.model.RealEstate;
import com.project.zipkok.model.RealEstateImage;
import com.project.zipkok.model.User;
import com.project.zipkok.model.Zim;
import com.project.zipkok.repository.KokRepository;
import com.project.zipkok.repository.RealEstateRepository;
import com.project.zipkok.repository.UserRepository;
import com.project.zipkok.repository.ZimRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

            List<String> realEstateImages = realEstate.getRealEstateImages()
                    .stream()
                    .map(RealEstateImage::getImageUrl)
                    .collect(Collectors.toList());

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
                    .transactionType(realEstate.getTransactionType().getDescription())
                    .deposit(realEstate.getDeposit())
                    .price(realEstate.getPrice())
                    .detail(realEstate.getDetail())
                    .areaSize(realEstate.getAreaSize())
                    .pyeongsu(realEstate.getPyeongsu())
                    .realEstateType(realEstate.getRealEstateType().getDescription())
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

        try {
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
        } catch (Exception e) {
            throw new RealEstateException(PROPERTY_REGISTRATION_FAILURE);
        }
    }

    public GetRealEstateOnMapResponse getRealEstate(long userId, Double southWestLat, Double southWestLon, Double northEastLat, Double northEastLon) {
        log.info("{UserService.getRealEstate}");

        List<RealEstate> realEstateList = this.realEstateRepository.findByLatitudeBetweenAndLongitudeBetween(southWestLat,northEastLat,southWestLon,northEastLon);
        User user = this.userRepository.findByUserId(userId);

        if(realEstateList == null || realEstateList.isEmpty()){
            throw new RealEstateException(PROPERTY_NOT_FOUND);
        }

        GetRealEstateOnMapResponse getRealEstateOnMapResponse = new GetRealEstateOnMapResponse();

        String userTransactionType = user.getTransactionType().getDescription();
        String userRealEstateType = user.getReslEstateType().getDescription();

        //filter 정보 mapping
        getRealEstateOnMapResponse.setFilter(GetRealEstateOnMapResponse.Filter.builder()
                .transactionType(userTransactionType)
                .realEstateType(userRealEstateType)
                .depositMin(null)
                .depositMax(null)
                .priceMin(null)
                .priceMax(null).build()
        );

        if(userTransactionType.equals("월세")){
            getRealEstateOnMapResponse.getFilter().setDepositMin(user.getTransactionPriceConfig().getMDepositMin());
            getRealEstateOnMapResponse.getFilter().setDepositMax(user.getTransactionPriceConfig().getMDepositMax());
            getRealEstateOnMapResponse.getFilter().setPriceMin(user.getTransactionPriceConfig().getMPriceMin());
            getRealEstateOnMapResponse.getFilter().setPriceMax(user.getTransactionPriceConfig().getMPriceMax());
        }else if(userTransactionType.equals("전세")) {
            getRealEstateOnMapResponse.getFilter().setDepositMin(user.getTransactionPriceConfig().getYDepositMin());
            getRealEstateOnMapResponse.getFilter().setDepositMax(user.getTransactionPriceConfig().getYDepositMax());
        }else if(userTransactionType.equals("매매")){
            getRealEstateOnMapResponse.getFilter().setPriceMin(user.getTransactionPriceConfig().getPurchaseMin());
            getRealEstateOnMapResponse.getFilter().setPriceMax(user.getTransactionPriceConfig().getPurchaseMax());
        }


        //realEstateInfo mapping
        List<GetRealEstateOnMapResponse.RealEstateInfo> realEstateInfoList = realEstateList
                .stream()
                .filter(result -> result.getTransactionType().getDescription().equals(userTransactionType) && result.getRealEstateType().getDescription().equals(userRealEstateType))
                .filter(result -> filterPriceConfig(result, getRealEstateOnMapResponse.getFilter()))
                .filter(result -> result.getUser() == null || result.getUser().getUserId().equals(userId))
                .map(result -> GetRealEstateOnMapResponse.RealEstateInfo.builder()
                        .realEstateId(result.getRealEstateId())
                        .imageURL(result.getImageUrl())
                        .deposit(result.getDeposit())
                        .price(result.getPrice())
                        .transactionType(result.getTransactionType().getDescription())
                        .realEstateType(result.getRealEstateType().getDescription())
                        .address(result.getAddress())
                        .detailAddress(result.getDetailAddress())
                        .latitude(result.getLatitude())
                        .longitude(result.getLongitude())
                        .agent(result.getAgent())
                        .isZimmed(this.zimRepository.findFirstByUserAndRealEstate(user, result))
                        .isKokked(this.kokRepository.findFirstByUserAndRealEstate(user, result))
                        .build())
                .toList();


        getRealEstateOnMapResponse.setRealEstateInfoList(realEstateInfoList);

        return getRealEstateOnMapResponse;
    }

    private boolean filterPriceConfig(RealEstate realEstate, GetRealEstateOnMapResponse.Filter filter){
        String transactionType = realEstate.getTransactionType().getDescription();
        long deposit = realEstate.getDeposit();
        long price = realEstate.getPrice();

        if(transactionType.equals("월세")){
            if(deposit < filter.getDepositMin() || deposit > filter.getDepositMax()){ return false; }
            if(price < filter.getPriceMin() || price > filter.getPriceMax()) { return false; }
        }else if(transactionType.equals("전세")) {
            if(deposit < filter.getDepositMin() || deposit > filter.getDepositMax()){ return false; }
        }else if(transactionType.equals("매매")){
            if(price < filter.getPriceMin() || price > filter.getPriceMax()) { return false; }
        }

        return true;
    }
}
