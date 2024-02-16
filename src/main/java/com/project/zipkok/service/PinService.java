package com.project.zipkok.service;

import com.project.zipkok.dto.GetPinResponse;
import com.project.zipkok.dto.PinInfo;
import com.project.zipkok.model.User;
import com.project.zipkok.repository.PinRepository;
import com.project.zipkok.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PinService {

    private final PinRepository pinRepository;
    private final UserRepository userRepository;

    @Transactional
    public GetPinResponse getPin(long userId) {

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
}
