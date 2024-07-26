package com.project.zipkok.service;

import com.project.zipkok.common.enums.OptionCategory;
import com.project.zipkok.common.exception.KokException;
import com.project.zipkok.dto.*;
import com.project.zipkok.model.*;
import com.project.zipkok.repository.*;
import com.project.zipkok.util.FileUploadUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.*;
import static com.project.zipkok.service.UserService.extractKeyFromUrl;
import static java.time.LocalTime.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class KokService {

    private final KokRepository kokRepository;
    private final UserRepository userRepository;
    private final RealEstateRepository realEstateRepository;
    private final FurnitureOptionRepository furnitureOptionRepository;
    private final OptionRepository optionRepository;
    private final DetailOptionRepository detailOptionRepository;
    private final FileUploadUtils fileUploadUtils;
    private final StarRepository starRepository;

    @Transactional
    public GetKokResponse getKoks(long userId, int page, int size) {

        log.info("[KokService.getKoks]");

        User user = userRepository.findByUserId(userId);

        List<Kok> koks = user.getKoks().stream().toList();
        List<Zim> zims = user.getZims().stream().toList();

        int startIdx = (page - 1) * size;
        GetKokResponse.Meta meta = calculateIndexingOfKokList(koks, page, size, startIdx);

        List<Kok> responseKoks = koks.stream()
                .skip(startIdx)
                .limit(size)
                .toList();

        return GetKokResponse.of(responseKoks, meta, zims);
    }

    private GetKokResponse.Meta calculateIndexingOfKokList(List<Kok> koks, int page, int size, int startIdx) {

        boolean isEnd = false;
        if(startIdx + size > koks.size() - 1) {
            isEnd = true;
        }

        int totalPage = (int) Math.ceil((double) koks.size()/size);
        if(page > totalPage) {
            throw new KokException(NO_MORE_KOK_DATA);
        }

        return GetKokResponse.Meta.builder()
                .isEnd(isEnd)
                .currentPage(page)
                .totalPage(totalPage)
                .build();
    }

    public GetKokDetailResponse getKokDetail(long userId, long kokId) {

        log.info("[KokService.getKokDetail]");

        User user = userRepository.findByUserId(userId);
        Kok kok = kokRepository.findById(kokId).get();

        boolean isZimmed = judgeIsZimmedRealEstate(user, kok.getRealEstate());

        validateUserAndKok(user, kok);

        return GetKokDetailResponse.of(kok, isZimmed);
    }

    private boolean judgeIsZimmedRealEstate(User user, RealEstate realEstate) {
        log.info("[KokService.judgeIsZimmedRealEstate]");

        return user.getZims().stream().anyMatch(zim -> zim.getRealEstate().getRealEstateId() == realEstate.getRealEstateId());
    }

    public GetKokOuterInfoResponse getKokOuterInfo(long userId, long kokId) {

        log.info("[KokService.getKokOuterInfo]");

        User user = userRepository.findByUserId(userId);
        Kok kok = kokRepository.findKokWithCheckedOptionAndCheckedDetailOption(kokId);

        validateUserAndKok(user, kok);

        return GetKokOuterInfoResponse.of(kok);
    }

    public GetKokInnerInfoResponse getKokInnerInfo(long userId, long kokId) {

        log.info("[KokService.getKokInnerInfo]");

        User user = userRepository.findByUserId(userId);
        Kok kok = kokRepository.findKokWithCheckedOptionAndCheckedDetailOption(kokId);

        validateUserAndKok(user, kok);

        return GetKokInnerInfoResponse.of(kok);
    }

    public GetKokContractResponse getKokContractInfo(long userId, long kokId) {

        log.info("[KokService.getKokContractInfo]");

        User user = userRepository.findByUserId(userId);
        Kok kok = kokRepository.findKokWithCheckedOptionAndCheckedDetailOption(kokId);

        validateUserAndKok(user, kok);

        return GetKokContractResponse.of(kok);
    }

    public GetKokReviewInfoResponse getKokReviewInfo(long userId, long kokId) {

        log.info("[KokService.getKokReviewInfo]");

        User user = userRepository.findByUserId(userId);
        Kok kok = kokRepository.findKokWithImpressionAndStar(kokId);

        validateUserAndKok(user, kok);

        return GetKokReviewInfoResponse.of(kok);
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

        Set<String> hilightsResponse = makeHilightTitleList(user.getHighlights());
        Set<String> checkedHilightsResponse = null;
        List<String> furnitureOptionsResponse = makeFurnitureNameList(furnitureOptionRepository.findAll());
        List<String> checkedFurinirureOptionsResponse = null;
        GetKokConfigInfoResponse.ReviewInfo reviewInfoResponse = null;
        String directionResponse = null;

        List<String> outerKokImagesResponse = null;
        List<String> innerKokImagesResponse = null;
        List<String> contractKokImagesResponse = null;

        List<GetKokConfigInfoResponse.Option> outerOptionsResponse = makeOptionResponseList(filterOption(user.getOptions(), OptionCategory.OUTER), kok);
        List<GetKokConfigInfoResponse.Option> innerOptionsResponse = makeOptionResponseList(filterOption(user.getOptions(), OptionCategory.INNER), kok);
        List<GetKokConfigInfoResponse.Option> contractOptionsResponse = makeOptionResponseList(filterOption(user.getOptions(), OptionCategory.CONTRACT), kok);

        if (kok != null) {
            checkedHilightsResponse = makeHilightTitleList(kok.getCheckedHighlights().stream().map(CheckedHighlight::getHighlight).collect(Collectors.toSet()));
            checkedFurinirureOptionsResponse = makeFurnitureNameList(kok.getCheckedFurnitures().stream().map(CheckedFurniture::getFurnitureOption).toList());
            reviewInfoResponse = makeReviewInfoResponseList(user, kok);
            directionResponse = kok.getDirection();
            outerKokImagesResponse = makeKokImagesUrlList(kok.getKokImages(), OptionCategory.OUTER);
            innerKokImagesResponse = makeKokImagesUrlList(kok.getKokImages(), OptionCategory.INNER);
            contractKokImagesResponse = makeKokImagesUrlList(kok.getKokImages(), OptionCategory.CONTRACT);
        }


        GetKokConfigInfoResponse response = GetKokConfigInfoResponse.builder()
                .hilights(hilightsResponse.stream().toList())
                .checkedHilights(checkedHilightsResponse.stream().toList())
                .furnitureOptions(furnitureOptionsResponse.stream().toList())
                .checkedFurnitureOptions(checkedFurinirureOptionsResponse.stream().toList())
                .reviewInfo(reviewInfoResponse)
                .direction(directionResponse)
                .outerImageUrls(outerKokImagesResponse)
                .innerImageUrls(innerKokImagesResponse)
                .contractImageUrls(contractKokImagesResponse)
                .outerOptions(outerOptionsResponse)
                .innerOptions(innerOptionsResponse)
                .contractOptions(contractOptionsResponse)
                .build();

        return response;
    }

    private List<String> makeKokImagesUrlList(List<KokImage> kokImages, OptionCategory optionCategory) {
        if (kokImages != null) {
            List<String> urlList = kokImages.stream()
                    .filter(kokImage -> kokImage.getCategory().equals(optionCategory.getDescription()))
                    .map(KokImage::getImageUrl)
                    .toList();
            return urlList;
        }
        return null;
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

        if (kok == null) {
            throw new KokException(KOK_ID_NOT_FOUND);
        }

        if (!kok.getUser().equals(user)) {
            throw new KokException(INVALID_KOK_ACCESS);
        }
    }

    private List<Option> filterOption(Set<Option> optionList, OptionCategory category) {
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

    private static Set<String> makeHilightTitleList(Set<Highlight> highlights) {
        Set<String> hilightsResponse = highlights
                .stream()
                .map(Highlight::getTitle)
                .collect(Collectors.toSet());
        return hilightsResponse;
    }

    private static List<String> makeFurnitureNameList(List<FurnitureOption> furnitures) {
        List<String> furnitureStringList = furnitures
                .stream()
                .map(FurnitureOption::getFurnitureName)
                .toList();
        return furnitureStringList;
    }

    @Transactional
    public PostOrPutKokResponse createOrUpdateKok(long userId, List<MultipartFile> multipartFiles, PostOrPutKokRequest postOrPutKokRequest) {
        log.info("KokService.postOrPutKok");

        Kok kok = settingKok(userId, multipartFiles, postOrPutKokRequest);

        kokRepository.save(kok);

        return PostOrPutKokResponse.builder()
                .kokId(kok.getKokId())
                .build();
    }

    private Kok settingKok(long userId, List<MultipartFile> multipartFiles, PostOrPutKokRequest postOrPutKokRequest){
        log.info("KokService.settingKok");

        User user = userRepository.findByUserId(userId);
        Kok kok = getInitializeKok(user, postOrPutKokRequest);

        if(kok == null){
            throw new KokException(KOK_ID_NOT_FOUND);
        }

        fillKokFieldValue(user, kok, postOrPutKokRequest);
        handleKokImage(user, multipartFiles, kok);

        return kok;
    }

    private Kok getInitializeKok(User user, PostOrPutKokRequest postOrPutKokRequest) {
        log.info("KokService.getInitializeKok");

        return (postOrPutKokRequest.getKokId() == null)
                ? Kok.builder()
                    .realEstate(realEstateRepository.findById(postOrPutKokRequest.getRealEstateId()).get())
                    .user(user)
                    .checkedHighlights(new ArrayList<>())
                    .checkedFurnitures(new ArrayList<>())
                    .checkedImpressions(new ArrayList<>())
                    .checkedOptions(new LinkedHashSet<>())
                    .checkedDetailOptions(new LinkedHashSet<>())
                    .kokImages(new ArrayList<>())
                    .build()
                : clearKok(Objects.requireNonNull(kokRepository.findById(postOrPutKokRequest.getKokId()).orElse(null)));
    }

    private Kok clearKok(Kok kok) {
        log.info("KokService.clearKok");

        kok.getCheckedHighlights().clear();
        kok.getCheckedFurnitures().clear();
        kok.getCheckedImpressions().clear();
        kok.getCheckedOptions().clear();
        kok.getCheckedDetailOptions().clear();

        if(!kok.getKokImages().isEmpty()) {
            kok.getKokImages().forEach(kokImage -> fileUploadUtils.deleteFile(extractKeyFromUrl(kokImage.getImageUrl())));
            kok.getKokImages().clear();
        }

        return kok;
    }

    private void fillKokFieldValue(User user, Kok kok, PostOrPutKokRequest postOrPutKokRequest) {
        log.info("KokService.fillKokFieldValue");

        kok.setDirection(postOrPutKokRequest.getDirection());
        kok.setReview(postOrPutKokRequest.getReviewInfo().getReviewText());

        Star star = Star.builder()
                .facilityStar(postOrPutKokRequest.getReviewInfo().getFacilityStarCount())
                .infraStar(postOrPutKokRequest.getReviewInfo().getInfraStarCount())
                .structureStar(postOrPutKokRequest.getReviewInfo().getStructureStarCount())
                .vibeStar(postOrPutKokRequest.getReviewInfo().getVibeStarCount())
                .build();

        starRepository.save(star);
        kok.setStar(star);

        kokRepository.save(kok);

        postOrPutKokRequest.getCheckedHighlights()
                .forEach(checkedHighlight ->
                        kok.getCheckedHighlights().add(CheckedHighlight.builder()
                                .kok(kok)
                                .highlight(
                                        user.getHighlights().stream()
                                                .filter(highlight -> highlight.getTitle().equals(checkedHighlight))
                                                .findFirst()
                                                .orElse(null)
                                )
                                .build()));

        List<FurnitureOption> furnitureOptionList = furnitureOptionRepository.findAll();
        postOrPutKokRequest.getCheckedFurnitureOptions()
                .forEach(checkedFurniture ->
                        kok.getCheckedFurnitures().add(CheckedFurniture.builder()
                                .furnitureOption(
                                        furnitureOptionList.stream()
                                                .filter(furnitureOption -> furnitureOption.getFurnitureName().equals(checkedFurniture))
                                                .findFirst()
                                                .orElse(null)
                                )
                                .kok(kok)
                                .build()));

        postOrPutKokRequest.getReviewInfo().getCheckedImpressions()
                .forEach(checkedImpression ->
                        kok.getCheckedImpressions().add(CheckedImpression.builder()
                                .impression(user.getImpressions().stream()
                                        .filter(highlight -> highlight.getImpressionTitle().equals(checkedImpression))
                                        .findFirst()
                                        .orElse(null))
                                .kok(kok)
                                .build()));

        List<PostOrPutKokRequest.Option> kokOptions = Stream.of(postOrPutKokRequest.getCheckedOuterOptions(), postOrPutKokRequest.getCheckedInnerOptions(), postOrPutKokRequest.getCheckedContractOptions())
                .flatMap(Collection::stream)
                .toList();

        List<Option> optionList = optionRepository.findAll();
        kokOptions.forEach(kokOption -> kok.getCheckedOptions().add(
                CheckedOption.builder()
                    .option(
                        optionList.stream()
                                .filter(option -> option.getOptionId() == kokOption.getOptionId())
                                .findFirst()
                                .orElse(null)
                    )
                    .kok(kok)
                    .build()
            )
        );

        List<Long> detailOptionIds = kokOptions.stream()
                .flatMap(option -> option.getCheckedDetailOptionIds().stream())
                .toList();

        List<DetailOption> detailOptionList = detailOptionRepository.findAll();
        detailOptionIds.forEach(id -> kok.getCheckedDetailOptions().add(
                CheckedDetailOption.builder()
                    .detailOption(
                            detailOptionList.stream()
                                    .filter(detailOption -> detailOption.getDetailOptionId() == id)
                                    .findFirst()
                                    .orElse(null)
                    )
                    .kok(kok)
                    .build()
            )
        );

    }

    private void handleKokImage(User user, List<MultipartFile> multipartFiles, Kok kok) {
        log.info("KokService.handleKokImage");

        if(multipartFiles != null && !multipartFiles.isEmpty()) {

            List<KokImage> kokImages = multipartFiles.stream()
                    .map(file -> {
                        String url = file.getOriginalFilename();
                        OptionCategory category = determineCategory(url);

                        url = fileUploadUtils.uploadFile(user.getUserId().toString() + "/" + System.currentTimeMillis(), file);

                        return KokImage.builder()
                                .category(category.getDescription())
                                .imageUrl(url)
                                .kok(kok)
                                .option(null)
                                .build();
                    }).toList();

            kok.setKokImages(kokImages);
        }
    }

    private OptionCategory determineCategory(String url) {
        if (url.contains("OUTER")) {
            return OptionCategory.OUTER;
        } else if (url.contains("INNER")) {
            return OptionCategory.INNER;
        } else if (url.contains("CONTRACT")) {
            return OptionCategory.CONTRACT;
        }
        return OptionCategory.OUTER;
    }
}
