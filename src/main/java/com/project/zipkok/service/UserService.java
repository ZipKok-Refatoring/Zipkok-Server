package com.project.zipkok.service;

import com.project.zipkok.dto.GetUserResponse;
import com.project.zipkok.model.User;
import com.project.zipkok.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    @Transactional
    public List<GetUserResponse> getUsers() {
        List<GetUserResponse> userList = userRepository.findAll()
                .stream()
                .map(user -> new GetUserResponse(
                        user.getNickname(),
                        user.getProfileImgUrl(),
                        user.getDesireResidences().get(0).getAddress(),
                        user.getReslEstateType().getDescription(),
                        user.getTransactionType().getDescription(),
                        user.getTransactionPriceConfigs().get(0).getMPriceMax(),
                        user.getTransactionPriceConfigs().get(0).getMPriceMin(),
                        user.getTransactionPriceConfigs().get(0).getMDepositMax(),
                        user.getTransactionPriceConfigs().get(0).getMDepositMin()
                )).collect(Collectors.toList());

        return userList;
    }
}
