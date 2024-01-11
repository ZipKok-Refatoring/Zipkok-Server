package com.project.zipkok.model;

import com.project.zipkok.common.enums.Gender;
import com.project.zipkok.common.enums.OAuthProvider;
import com.project.zipkok.common.enums.RealEstateType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "User")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

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

    @OneToMany(mappedBy = "User",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Zim> zims = new ArrayList<>();

    @OneToMany(mappedBy = "User",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<RealEstate> realEstates = new ArrayList<>();

    @OneToMany(mappedBy = "User",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Kok> koks = new ArrayList<>();

    @OneToMany(mappedBy = "User",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<TransactionPriceConfig> transactionPriceConfigs = new ArrayList<>();

    @OneToMany(mappedBy = "User",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Option> options = new ArrayList<>();

    @OneToMany(mappedBy = "User",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Pin> pins = new ArrayList<>();

    @OneToMany(mappedBy = "User",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<DesireResidence> desireResidences = new ArrayList<>();

    @OneToMany(mappedBy = "User",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Highlight> highlights = new ArrayList<>();

    public User(String email, OAuthProvider oAuthProvider, String nickname, Gender gender, String birthday) {
        this.email = email;
        this.oAuthProvider = oAuthProvider;
        this.nickname = nickname;
        this.gender = gender;
        this.birthday = birthday;
        this.status = "active";
    }



}
