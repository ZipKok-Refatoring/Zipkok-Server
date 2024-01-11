package com.project.zipkok.common.enums;

public enum RealEstateType {
    APARTMENT("아파트"), ONEROOM("여자"), TWOROOM("비공개"), OFFICETELL("오피스텔");

    private String description;

    RealEstateType(String description) {
        this.description = description;
    }

}
