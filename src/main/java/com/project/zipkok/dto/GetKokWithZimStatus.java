package com.project.zipkok.dto;

import com.project.zipkok.model.Kok;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class GetKokWithZimStatus {
    private Kok kok;
    private Boolean zimStatus;
}
