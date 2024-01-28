package com.project.zipkok.service;

import com.project.zipkok.common.exception.KokException;
import com.project.zipkok.dto.GetKokDetailResponse;
import com.project.zipkok.dto.GetKokOuterInfoResponse;
import com.project.zipkok.dto.GetKokResponse;
import com.project.zipkok.model.*;
import com.project.zipkok.repository.KokRepository;
import com.project.zipkok.repository.UserRepository;
import com.project.zipkok.repository.ZimRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.INVALID_KOK_ACCESS;
import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.NO_MORE_KOK_DATA;

@Slf4j
@Service
@RequiredArgsConstructor
public class KokService {

    private final KokRepository kokRepository;
    private final ZimRepository zimRepository;
    private final UserRepository userRepository;


    @Transactional
    public GetKokResponse getKoks(long userId, int page, int size) {

        User user = userRepository.findByUserId(userId);

        List<Kok> koks = user.getKoks();

        int startIdx = (page - 1) * size;
        List<Kok> responseKoks = koks.stream()
                .skip(startIdx)
                .limit(size)
                .collect(Collectors.toList());

        boolean isEnd = false;
        if(startIdx + size > koks.size() - 1) {
            isEnd = true;
        }

        int totalPage = (int) Math.ceil((double) koks.size()/size);
        if(page > totalPage) {
            throw new KokException(NO_MORE_KOK_DATA);
        }


        GetKokResponse response = GetKokResponse.builder()
                .koks(responseKoks.stream().map(kok -> GetKokResponse.Koks.builder()
                        .kokId(kok.getKokId())
                        .imageUrl(Optional.ofNullable(kok.getRealEstate().getRealEstateImages())
                                .filter(images -> !images.isEmpty())
                                .map(images -> images.get(0).getImageUrl())
                                .orElse(null))
                        .address(kok.getRealEstate().getAddress())
                        .detailAddress(kok.getRealEstate().getDetailAddress())
                        .estateAgent(kok.getRealEstate().getAgent())
                        .transactionType(kok.getRealEstate().getTransactionType().getDescription())
                        .realEstateType(kok.getRealEstate().getRealEstateType().getDescription())
                        .deposit(kok.getRealEstate().getDeposit())
                        .price(kok.getRealEstate().getPrice())
                        .isZimmed(kok.getRealEstate().getZims().stream().anyMatch(a -> a.equals(zimRepository.findFirstByUserAndRealEstate(user, kok.getRealEstate()))))
                        .build())
                        .collect(Collectors.toList()))
                .meta(GetKokResponse.Meta.builder()
                        .isEnd(isEnd)
                        .currentPage(page)
                        .totalPage(totalPage)
                        .build())
                .build();

        return response;

    }

    public GetKokDetailResponse getKokDetail(long userId, long kokId) {

        User user = userRepository.findByUserId(userId);

        Kok kok = kokRepository.findById(kokId).get();

        if (!kok.getUser().equals(user)) {
            throw new KokException(INVALID_KOK_ACCESS);
        }

        GetKokDetailResponse response = GetKokDetailResponse.builder()
                .kokId(kok.getKokId())
                .imageInfo(GetKokDetailResponse.ImageInfo.builder().
                        imageNumber(kok.getKokImages().size())
                        .imageUrls(kok.getKokImages().stream().map(KokImage::getImageUrl).collect(Collectors.toList()))
                        .build())
                .address(kok.getRealEstate().getAddress())
                .detailAddress(kok.getRealEstate().getDetailAddress())
                .transactionType(kok.getRealEstate().getTransactionType().getDescription())
                .deposit(kok.getRealEstate().getDeposit())
                .price(kok.getRealEstate().getPrice())
                .detail(kok.getRealEstate().getDetail())
                .areaSize(kok.getRealEstate().getAreaSize())
                .pyeongsu((int) kok.getRealEstate().getPyeongsu())
                .realEstateType(kok.getRealEstate().getRealEstateType().getDescription())
                .floorNum(kok.getRealEstate().getFloorNum())
                .administrativeFee(kok.getRealEstate().getAdministrativeFee())
                .latitude(kok.getRealEstate().getLatitude())
                .longitude(kok.getRealEstate().getLongitude())
                .isZimmed(zimRepository.findByUser(user).getRealEstate().equals(kok.getRealEstate()))
                .build();

        return response;
    }

    public GetKokOuterInfoResponse getKokOuterInfo(long userId, long kokId) {

        User user = userRepository.findByUserId(userId);

        Kok kok = kokRepository.findById(kokId).get();

        if (!kok.getUser().equals(user)) {
            throw new KokException(INVALID_KOK_ACCESS);
        }

        GetKokOuterInfoResponse response = GetKokOuterInfoResponse.builder()
                .hilights(kok.getCheckedHighlights()
                        .stream()
                        .map(CheckedHighlight::getHighlight)
                        .map(Highlight::getTitle)
                        .collect(Collectors.toList()))
                .options(kok.getCheckedOptions()
                        .stream()
                        .map(checkedOption -> GetKokOuterInfoResponse.OuterOption.builder()
                                .option(checkedOption.getOption().getName())
                                .orderNumber((int) checkedOption.getOption().getOrderNum())
                                .detailOptions(kok.getCheckedDetailOptions()
                                        .stream()
                                        .filter(checkedDetailOption -> checkedDetailOption.getDetailOption().getOption().equals(checkedOption.getOption()))
                                        .map(CheckedDetailOption::getDetailOption)
                                        .map(DetailOption::getName)
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return response;
    }
}
