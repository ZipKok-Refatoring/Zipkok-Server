package com.project.zipkok.service;

import com.project.zipkok.common.exception.PinException;
import com.project.zipkok.dto.*;
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
                                        .addressName(pin.getAddress())
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
                        .addressName(pin.getAddress())
                        .x(pin.getLatitude())
                        .y(pin.getLongitude())
                        .build())
                .build();

        return response;
    }

    @Transactional
    public PostPinResponse registerPin(long userId, PostPinRequest postPinRequest) {
        log.info("[PinService.registerPin]");

        User user = userRepository.findByUserId(userId);

        Pin pin = Pin.builder()
                .user(user)
                .pinNickname(postPinRequest.getName())
                .address(postPinRequest.getAddress().getAddressName())
                .latitude(postPinRequest.getAddress().getX())
                .longitude(postPinRequest.getAddress().getY())
                .build();

        PostPinResponse response = new PostPinResponse(pinRepository.save(pin).getPinId());

        return response;

    }

    @Transactional
    public Object updatePin(long userId, PinInfo putPinRequest) {
        log.info("[PinService.updatePin]");

        User user = userRepository.findByUserId(userId);

        Pin pin = pinRepository.findByPinId(putPinRequest.getId());

        if(pin == null) {
            throw new PinException(PIN_NOT_FOUND);
        } else if (!user.getPins().contains(pin)) {
            throw new PinException(PIN_NOT_FOUND);
        }

        pin.setPinNickname(putPinRequest.getName());
        pin.setAddress(putPinRequest.getAddress().getAddressName());
        pin.setLatitude(putPinRequest.getAddress().getX());
        pin.setLongitude(putPinRequest.getAddress().getY());

        pinRepository.save(pin);

        return null;
    }

    @Transactional
    public Object deletePin(long userId, DeletePinRequest deletePinRequest) {

        log.info("[PinService.deletePin]");

        User user = userRepository.findByUserId(userId);

        Pin pin = pinRepository.findByPinId(deletePinRequest.getId());

        if(pin == null) {
            throw new PinException(PIN_NOT_FOUND);
        } else if (!user.getPins().contains(pin)) {
            throw new PinException(PIN_NOT_FOUND);
        }

        pinRepository.delete(pin);

        return null;

    }
}
