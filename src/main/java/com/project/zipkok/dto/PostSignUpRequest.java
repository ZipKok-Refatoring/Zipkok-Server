package com.project.zipkok.dto;

import com.project.zipkok.common.enums.Gender;
import com.project.zipkok.common.enums.OAuthProvider;
import com.project.zipkok.common.enums.ValidEnum;
import com.project.zipkok.model.DesireResidence;
import com.project.zipkok.model.User;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostSignUpRequest {

    @NotBlank(message = "nickname: {NotBlank}")
    @Size(max = 12)
    private String nickname;

    @ValidEnum(enumClass = OAuthProvider.class)
    private OAuthProvider oauthProvider;

    @NotBlank
    @Email
    private String email;

    @ValidEnum(enumClass = Gender.class)
    private Gender gender;

    @NotBlank
    @Size(max =6)
    private String birthday;

    public User toEntity() {
        return User.builder()
                .nickname(nickname)
                .oAuthProvider(oauthProvider)
                .email(email)
                .birthday(birthday)
                .gender(gender)
                .status("active")
                .build();
    }
}
