package com.project.zipkok.service;

import com.project.zipkok.dto.GetAddressResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {

    @Value("${daum.url.address}")
    private String uri;

    @Value("${oauth.kakao.client-id}")
    private String key;

    public GetAddressResponse getAddresses(String query, int page, int size) {
        log.info("[AddressService.getAddress]");

        RestTemplate restTemplate = new RestTemplate();

        String apiKey = "KakaoAK " + key;


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        UriComponents uriComponents = UriComponentsBuilder
                .fromHttpUrl(uri)
                .queryParam("query", query)
                .queryParam("page", page)
                .queryParam("size", size)
                .build();

        GetAddressResponse getAddressResponse = restTemplate.exchange(uriComponents.toString(), HttpMethod.GET, entity, GetAddressResponse.class).getBody();

        return getAddressResponse;
    }
}
