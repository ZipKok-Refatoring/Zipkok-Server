package com.project.zipkok.service;

import com.project.zipkok.common.exception.zim.NoZimMatchedUser;
import com.project.zipkok.dto.GetZimLoadResponse;
import com.project.zipkok.model.RealEstate;
import com.project.zipkok.model.User;
import com.project.zipkok.model.Zim;
import com.project.zipkok.repository.RealEstateRepository;
import com.project.zipkok.repository.UserRepository;
import com.project.zipkok.repository.ZimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.FAVORITES_QUERY_FAILURE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZimService {

    private final ZimRepository zimRepository;
    private final UserRepository userRepository;
    private final RealEstateRepository realEstateRepository;

    public GetZimLoadResponse zimLoad(long userId) {
        log.info("{ZimService.zimLoad}");

        User user = this.userRepository.findByUserId(userId);
        List<Zim> zimList = this.zimRepository.findAllByUser(user);

        if(zimList == null){
            throw new NoZimMatchedUser(FAVORITES_QUERY_FAILURE);
        }

        GetZimLoadResponse getZimLoadResponse = new GetZimLoadResponse();

        for(Zim zim : zimList){
            RealEstate realEstate = zim.getRealEstate();

            if(realEstate == null){
                throw new NoZimMatchedUser(FAVORITES_QUERY_FAILURE);
            }

            getZimLoadResponse.addRealEstateInfo(realEstate.getRealEstateId(), realEstate.getImageUrl(), realEstate.getDeposit(), realEstate.getPrice(), realEstate.getAddress(), realEstate.getAgent());
        }

        return getZimLoadResponse;
    }

}
