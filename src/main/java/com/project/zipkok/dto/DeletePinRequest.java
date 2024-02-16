package com.project.zipkok.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.checkerframework.checker.units.qual.N;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeletePinRequest {

    @NotNull @Positive
    private Long id;
}
