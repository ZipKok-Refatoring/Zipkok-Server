package com.project.zipkok.common.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    MONTHLY("월세"), YEARLY("전세"), PURCHASE("매매");

    private String description;

     TransactionType(String description) {
        this.description = description;
    }
}
