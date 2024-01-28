package com.project.zipkok.common.enums;

import lombok.Getter;

@Getter
public enum OptionCategory {
    OUTER("집 주변"), INNER("집 내부"), CONTRACT("중개 / 계약");

    private String description;

    OptionCategory(String description) {
        this.description = description;
    }
}
