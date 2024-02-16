package com.project.zipkok.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DeletePinRequest {

    @NotNull @Positive
    private Long id;
}
