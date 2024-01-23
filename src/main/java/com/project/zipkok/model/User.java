package com.project.zipkok.model;

import com.project.zipkok.common.enums.Gender;
import com.project.zipkok.common.enums.OAuthProvider;
import com.project.zipkok.common.enums.RealEstateType;
import com.project.zipkok.common.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "User")
@Getter
@NoArgsConstructor
@Setter
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

    @Column(name = "transaction_type", nullable = true)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "status", nullable = false)
    private String status = "active";

    @OneToOne(mappedBy = "user",orphanRemoval = true, cascade = CascadeType.ALL)
    private DesireResidence desireResidence;

    @OneToOne(mappedBy = "user",orphanRemoval = true, cascade = CascadeType.ALL)
    private TransactionPriceConfig transactionPriceConfig;

    @OneToMany(mappedBy = "user",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Zim> zims = new ArrayList<>();

    @OneToMany(mappedBy = "user",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<RealEstate> realEstates = new ArrayList<>();

    @OneToMany(mappedBy = "user",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Kok> koks = new ArrayList<>();

    @OneToMany(mappedBy = "user",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Option> options = new ArrayList<>();

    @OneToMany(mappedBy = "user",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Pin> pins = new ArrayList<>();

    @OneToMany(mappedBy = "user",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Highlight> highlights = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Impression> impressions  = new ArrayList<>();

    @Builder
    public User(String email, OAuthProvider oAuthProvider, String nickname, Gender gender, String birthday) {
        this.email = email;
        this.oAuthProvider = oAuthProvider;
        this.nickname = nickname;
        this.gender = gender;
        this.birthday = birthday;
    }
}
