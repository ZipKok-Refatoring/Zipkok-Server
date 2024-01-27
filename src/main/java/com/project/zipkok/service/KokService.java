package com.project.zipkok.service;

import com.project.zipkok.dto.GetKokResponse;
import com.project.zipkok.model.Kok;
import com.project.zipkok.model.User;
import com.project.zipkok.repository.UserRepository;
import com.project.zipkok.repository.ZimRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KokService {

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
}
