package com.project.zipkok.model;

import com.project.zipkok.common.enums.Gender;
import com.project.zipkok.common.enums.OAuthProvider;
import com.project.zipkok.common.enums.RealEstateType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "User")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String userId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "profileimg_url", nullable = true)
    private String profileImgUrl;

    @Column(name = "o_auth_provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private OAuthProvider oAuthProvider;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "birthday", nullable = false)
    private String birthday;

    @Column(name = "realestate_type", nullable = true)
    @Enumerated(EnumType.STRING)
    private RealEstateType reslEstateType;

    @Column(name = "status", nullable = false)
    private String status;

    public User(String email, OAuthProvider oAuthProvider, String nickname, Gender gender, String birthday) {
        this.email = email;
        this.oAuthProvider = oAuthProvider;
        this.nickname = nickname;
        this.gender = gender;
        this.birthday = birthday;
        this.status = "active";
    }



}
