package com.project.zipkok.common.enums;

import lombok.Getter;

@Getter
public enum RealEstateType {
    APARTMENT("아파트"), ONEROOM("원룸"), TWOROOM("투룸"), OFFICETELL("오피스텔");

    private String description;

    RealEstateType(String description) {
        this.description = description;
    }

}
