package com.project.zipkok.dto;

import lombok.Builder;

import java.util.List;

@Builder
public class GetPinResponse {
    private List<PinInfo> pinList;
}
