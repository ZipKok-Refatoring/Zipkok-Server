package com.project.zipkok.service;

import com.project.zipkok.common.exception.DatabaseException;
import com.project.zipkok.common.exception.InternalServerErrorException;
import com.project.zipkok.common.exception.RealEstateException;
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

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.INVALID_PROPERTY_ID;
import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.PROPERTY_REGISTRATION_FAILURE;

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

        try {
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
                    .build();

            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RealEstateException(INVALID_PROPERTY_ID);
        }
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
}
