package com.project.zipkok.service;

import com.project.zipkok.common.exception.PinException;
import com.project.zipkok.dto.GetPinResponse;
import com.project.zipkok.dto.PinInfo;
import com.project.zipkok.model.Pin;
import com.project.zipkok.model.User;
import com.project.zipkok.repository.PinRepository;
import com.project.zipkok.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static com.project.zipkok.common.response.status.BaseExceptionResponseStatus.PIN_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PinService {

    private final PinRepository pinRepository;
    private final UserRepository userRepository;

    @Transactional
    public GetPinResponse getPin(long userId) {

        log.info("[PinService.getPin]");

        User user = userRepository.findByUserId(userId);

        GetPinResponse response = GetPinResponse.builder()
                .pinList(user.getPins()
                        .stream()
                        .map(pin -> PinInfo.builder()
                                .id(pin.getPinId())
                                .name(pin.getPinNickname())
                                .address(PinInfo.PinAddressInfo.builder()
                                        .addressName(pin.getAddress() + " " + pin.getDetailAddress())
                                        .x(pin.getLatitude())
                                        .y(pin.getLongitude())
                                        .build())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return response;
    }

    @Transactional
    public PinInfo getPinDetail(long userId, Long pinId) {

        log.info("[PinService.getPinDetail]");

        User user = userRepository.findByUserId(userId);
        Pin pin = pinRepository.findByPinId(pinId);

        if(pin == null) {
            throw new PinException(PIN_NOT_FOUND);
        } else if (!user.getPins().contains(pin)) {
            throw new PinException(PIN_NOT_FOUND);
        }

        PinInfo response = PinInfo.builder()
                .id(pin.getPinId())
                .name(pin.getPinNickname())
                .address(PinInfo.PinAddressInfo.builder()
                        .addressName(pin.getAddress() + " " + pin.getDetailAddress())
                        .x(pin.getLatitude())
                        .y(pin.getLongitude())
                        .build())
                .build();

        return response;
    }
}
