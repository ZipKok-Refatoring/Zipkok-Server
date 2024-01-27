package com.project.zipkok.service;

import com.project.zipkok.dto.GetKokResponse;
import com.project.zipkok.repository.KokRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KokService {

    private final KokRepository kokRepository;


    public GetKokResponse getKoks(long userId, int page, int size) {
    }
}
