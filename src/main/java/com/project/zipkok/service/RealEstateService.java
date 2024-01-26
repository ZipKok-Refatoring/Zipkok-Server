package com.project.zipkok.service;

import com.project.zipkok.common.exception.DatabaseException;
import com.project.zipkok.common.exception.InternalServerErrorException;
import com.project.zipkok.common.exception.RealEstateException;
import com.project.zipkok.dto.GetRealEstateResponse;
import com.project.zipkok.model.RealEstate;
import com.project.zipkok.model.RealEstateImage;
import com.project.zipkok.repository.RealEstateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.INVALID_PROPERTY_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealEstateService {

    private final RealEstateRepository realEstateRepository;


    @Transactional
    public GetRealEstateResponse getRealEstateInfo(Long realEstateId) {

        log.info("[RealEstateService.getRealEstateInfo]");

        try {
            RealEstate realEstate = realEstateRepository.findById(realEstateId).get();

            List<String> realEstateImages = realEstate.getRealEstateImages()
                    .stream()
                    .map(RealEstateImage::getImageUrl)
                    .collect(Collectors.toList());

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
                    .isZimmed(!realEstate.getZims().isEmpty())
                    .isKokked(!realEstate.getKoks().isEmpty())
                    .build();

            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RealEstateException(INVALID_PROPERTY_ID);
        }
    }
}
