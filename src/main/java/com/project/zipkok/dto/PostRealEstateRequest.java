package com.project.zipkok.dto;

import com.project.zipkok.common.enums.RealEstateType;
import com.project.zipkok.common.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostRealEstateRequest {
    @NotBlank
    @Size(max = 30)
    private String realEstateName;
    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @NotNull
    @Enumerated(EnumType.STRING)
    private RealEstateType realEstateType;

    private Long deposit;

    private Long price;

    @NotNull
    private Integer administrativeFee;
    @NotBlank
    private String address;
    private String detailAddress;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
    private Integer pyeongsu;
    private Integer floorNum;
}
