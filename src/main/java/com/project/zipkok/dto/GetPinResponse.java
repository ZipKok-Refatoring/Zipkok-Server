package com.project.zipkok.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GetPinResponse {
    private List<PinInfo> pinList;
}
