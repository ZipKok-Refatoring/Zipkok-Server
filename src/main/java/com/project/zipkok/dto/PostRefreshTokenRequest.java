package com.project.zipkok.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostRefreshTokenRequest {

    @NotBlank(message = "refreshToken: {NotBlank}")
    private String refreshToken;
}
