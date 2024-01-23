package com.project.zipkok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetAddressResponse {

    @JsonProperty("documents")
    private List<AddressData> documents;

    @JsonProperty("meta")
    private ResponseMetaData meta;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddressData {
        @JsonProperty("address_name")
        private String addressName;

        @JsonProperty("x")
        private String latitude;

        @JsonProperty("y")
        private String longitude;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseMetaData {

        @JsonProperty("is_end")
        private boolean isEnd;

        @JsonProperty("pageable_count")
        private int pagableCount;

        @JsonProperty("total_count")
        private int totalCount;
    }



}
