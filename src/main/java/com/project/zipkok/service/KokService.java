package com.project.zipkok.service;

import com.project.zipkok.common.enums.OptionCategory;
import com.project.zipkok.common.exception.KokException;
import com.project.zipkok.common.exception.NoExistUserException;
import com.project.zipkok.dto.*;
import com.project.zipkok.model.*;
import com.project.zipkok.repository.FurnitureOptionRepository;
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

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KokService {

    private final KokRepository kokRepository;
    private final ZimRepository zimRepository;
    private final UserRepository userRepository;
    private final FurnitureOptionRepository furnitureOptionRepository;


    @Transactional
    public GetKokResponse getKoks(long userId, int page, int size) {

        log.info("[KokService.getKoks]");

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

        log.info("[KokService.getKokDetail]");

        User user = userRepository.findByUserId(userId);
        Kok kok = kokRepository.findById(kokId).get();

        validateUserAndKok(user, kok);

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

        log.info("[KokService.getKokOuterInfo]");

        User user = userRepository.findByUserId(userId);
        Kok kok = kokRepository.findById(kokId).get();

        validateUserAndKok(user, kok);

        GetKokOuterInfoResponse response = GetKokOuterInfoResponse.builder()
                .hilights(kok.getCheckedHighlights()
                        .stream()
                        .map(CheckedHighlight::getHighlight)
                        .map(Highlight::getTitle)
                        .collect(Collectors.toList()))
                .options(kok.getCheckedOptions()
                        .stream()
                        .filter(checkedOption -> checkedOption.getOption().getCategory().equals(OptionCategory.OUTER))
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

    public GetKokInnerInfoResponse getKokInnerInfo(long userId, long kokId) {

        log.info("[KokService.getKokInnerInfo]");

        User user = userRepository.findByUserId(userId);
        Kok kok = kokRepository.findById(kokId).get();

        validateUserAndKok(user, kok);

        GetKokInnerInfoResponse response = GetKokInnerInfoResponse.builder()
                .furnitureOptions(kok.getCheckedFurniturs()
                        .stream()
                        .map(CheckedFurniture::getFurnitureOption)
                        .map(FurnitureOption::getFurnitureName)
                        .collect(Collectors.toList()))
                .direction(kok.getDirection())
                .options(kok.getCheckedOptions()
                        .stream()
                        .filter(checkedOption -> checkedOption.getOption().getCategory().equals(OptionCategory.INNER))
                        .map(checkedOption -> GetKokInnerInfoResponse.InnerOption.builder()
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

    public GetKokContractResponse getKokContractInfo(long userId, long kokId) {

        log.info("[KokService.getKokContractInfo]");

        User user = userRepository.findByUserId(userId);
        Kok kok = kokRepository.findById(kokId).get();

        validateUserAndKok(user, kok);

        List<String> contractImages = kok.getKokImages()
                .stream()
                .filter(kokImage -> kokImage.getOption().getCategory().equals(OptionCategory.CONTRACT))
                .map(KokImage::getImageUrl)
                .toList();

        GetKokContractResponse response = GetKokContractResponse.builder()
                .options(kok.getCheckedOptions()
                        .stream()
                        .filter(checkedOption -> checkedOption.getOption().getCategory().equals(OptionCategory.CONTRACT))
                        .map(checkedOption -> GetKokContractResponse.ContractOptions.builder()
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
                .imageInfo(GetKokContractResponse.ImageInfo.builder()
                        .imageNumber(contractImages.size())
                        .imageUrls(contractImages)
                        .build())
                .build();

        return response;
    }

    public GetKokReviewInfoResponse getKokReviewInfo(long userId, long kokId) {

        log.info("[KokService.getKokContractInfo]");

        User user = userRepository.findByUserId(userId);
        Kok kok = kokRepository.findById(kokId).get();

        validateUserAndKok(user, kok);

        GetKokReviewInfoResponse response = GetKokReviewInfoResponse.builder()
                .impressions(kok.getCheckedImpressions().stream().map(checkedImpression -> checkedImpression.getImpression().getImpressionTitle()).collect(Collectors.toList()))
                .facilityStarCount(kok.getStar().getFacilityStar())
                .infraStarCount(kok.getStar().getInfraStar())
                .structureStarCount(kok.getStar().getStructureStar())
                .vibeStarCount(kok.getStar().getVibeStar())
                .reviewText(kok.getReview())
                .build();

        return response;
    }

    public GetKokConfigInfoResponse getKokConfigInfo(long userId, Long kokId) {

        log.info("[KokService.getKokConfigInfo]");

        User user = userRepository.findByUserId(userId);

        if(kokId != null) {
            Kok kok = kokRepository.findByKokId(kokId);
            validateUserAndKok(user, kok);
            return makeKokConfigResponse(user, kok);
        }

        return makeKokConfigResponse(user, null);

    }

    private GetKokConfigInfoResponse makeKokConfigResponse(User user, Kok kok) {

        List<String> hilightsResponse = makeHilightTitleList(user.getHighlights());
        List<String> checkedHilightsResponse = null;
        List<String> furnitureOptionsResponse = makeFurnitureNameList(furnitureOptionRepository.findAll());
        List<String> checkedFurinirureOptionsResponse = null;
        GetKokConfigInfoResponse.ReviewInfo reviewInfoResponse = null;

        List<GetKokConfigInfoResponse.Option> outerOptionsResponse = makeOptionResponseList(filterOption(user.getOptions(), OptionCategory.OUTER), kok);
        List<GetKokConfigInfoResponse.Option> innerOptionsResponse = makeOptionResponseList(filterOption(user.getOptions(), OptionCategory.INNER), kok);
        List<GetKokConfigInfoResponse.Option> contractOptionsResponse = makeOptionResponseList(filterOption(user.getOptions(), OptionCategory.CONTRACT), kok);

        if (kok != null) {
            checkedHilightsResponse = makeHilightTitleList(kok.getCheckedHighlights().stream().map(CheckedHighlight::getHighlight).toList());
            checkedFurinirureOptionsResponse = makeFurnitureNameList(kok.getCheckedFurniturs().stream().map(CheckedFurniture::getFurnitureOption).toList());
            reviewInfoResponse = makeReviewInfoResponseList(user, kok);
        }


        GetKokConfigInfoResponse response = GetKokConfigInfoResponse.builder()
                .hilights(hilightsResponse)
                .checkedHilighs(checkedHilightsResponse)
                .furnitureOptions(furnitureOptionsResponse)
                .checkedFurnitureOptions(checkedFurinirureOptionsResponse)
                .reviewInfo(reviewInfoResponse)
                .outerOptions(outerOptionsResponse)
                .innerOptions(innerOptionsResponse)
                .contractOptions(contractOptionsResponse)
                .build();

        return response;
    }

    private static GetKokConfigInfoResponse.ReviewInfo makeReviewInfoResponseList(User user, Kok kok) {
        GetKokConfigInfoResponse.ReviewInfo reviewInfoResponse = GetKokConfigInfoResponse.ReviewInfo.builder()
                .impressions(user.getImpressions().stream().map(Impression::getImpressionTitle).collect(Collectors.toList()))
                .checkedImpressions(kok.getCheckedImpressions().stream().map(CheckedImpression::getImpression).map(Impression::getImpressionTitle).collect(Collectors.toList()))
                .facilityStarCount(kok.getStar().getFacilityStar())
                .infraStarCount(kok.getStar().getInfraStar())
                .structureStarCount(kok.getStar().getStructureStar())
                .vibeStarCount(kok.getStar().getVibeStar())
                .reviewText(kok.getReview())
                .build();
        return reviewInfoResponse;
    }

    private static void validateUserAndKok(User user, Kok kok) {

        if (!kok.getUser().equals(user)) {
            throw new KokException(INVALID_KOK_ACCESS);
        }
    }

    private List<Option> filterOption(List<Option> optionList, OptionCategory category) {
        List<Option> filteredOptions = optionList
                .stream()
                .filter(option -> option.getCategory().equals(category) && option.isVisible())
                .toList();
        
        return filteredOptions;
    }

    private List<GetKokConfigInfoResponse.Option> makeOptionResponseList(List<Option> options, Kok kok) {

        List<GetKokConfigInfoResponse.Option> response = options.stream().map(option -> GetKokConfigInfoResponse.Option.builder()
                        .optionId(option.getOptionId())
                        .optionTitle(option.getName())
                        .orderNumber((int) option.getOrderNum())
                        .isChecked(Optional.ofNullable(kok)
                                .map(k -> k.getCheckedOptions().stream()
                                        .anyMatch(checkedOption -> checkedOption.getOption().equals(option)))
                                .orElse(false))
                        .detailOptions(option.getDetailOptions()
                                .stream()
                                .filter(DetailOption::isVisible)
                                .map(detailOption -> GetKokConfigInfoResponse.DetailOption.builder()
                                        .detailOptionId(detailOption.getDetailOptionId())
                                        .detailOptionTitle(detailOption.getName())
                                        .isChecked(Optional.ofNullable(kok)
                                                .map(k -> k.getCheckedDetailOptions().stream()
                                                        .anyMatch(checkedDetailOption -> checkedDetailOption.getDetailOption().equals(detailOption)))
                                                .orElse(false))
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .toList();
        return response;
    }

    private static List<String> makeHilightTitleList(List<Highlight> highlights) {
        List<String> hilightsResponse = highlights
                .stream()
                .map(Highlight::getTitle)
                .toList();
        return hilightsResponse;
    }

    private static List<String> makeFurnitureNameList(List<FurnitureOption> furnitures) {
        List<String> furnitureStringList = furnitures
                .stream()
                .map(FurnitureOption::getFurnitureName)
                .toList();
        return furnitureStringList;
    }
}
