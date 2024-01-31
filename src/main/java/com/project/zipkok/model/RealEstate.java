package com.project.zipkok.model;

import com.project.zipkok.common.enums.RealEstateType;
import com.project.zipkok.common.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "RealEstate")
@Getter
@NoArgsConstructor
public class RealEstate {

    @Id
    @Column(name = "realestate_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long realEstateId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "deposit", nullable = false)
    private long deposit;

    @Column(name = "price", nullable = false)
    private long price;

    @Column(name = "administrative_fee")
    private int administrativeFee;

    @Column(name = "detail")
    private String detail;

    @Column(name = "area_size")
    private float areaSize;

    @Column(name = "pyeongsu")
    private long pyeongsu;

    @Column(name = "realestate_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RealEstateType realEstateType;

    @Column(name = "floor_num")
    private long floorNum;

    @Column(name = "status", nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "agent")
    private String agent;

    @Column(name = "detail_address")
    private String detailAddress;

    @OneToMany(mappedBy = "realEstate",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Zim> zims = new ArrayList<>();

    @OneToMany(mappedBy = "realEstate",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<RealEstateImage> realEstateImages = new ArrayList<>();

    @OneToMany(mappedBy = "realEstate",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Kok> koks = new ArrayList<>();

    public RealEstate(String address, double latitude, double longitude, TransactionType transactionType, long deposit, long price, RealEstateType realEstateType) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.transactionType = transactionType;
        this.deposit = deposit;
        this.price = price;
        this.realEstateType = realEstateType;
        this.status = "active";
    }
}
