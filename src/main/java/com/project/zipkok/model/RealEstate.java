package com.project.zipkok.model;

import com.project.zipkok.common.enums.RealEstateType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "RealEstate")
@Getter
@NoArgsConstructor
public class RealEstate {

    @Id
    @Column(name = "realestate_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long realEstateId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "deposit", nullable = false)
    private long deposit;

    @Column(name = "price", nullable = false)
    private long price;

    @Column(name = "administrative_fee")
    private long administrativeFee;

    @Column(name = "detail")
    private String detail;

    @Column(name = "area_size")
    private float areaSize;

    @Column(name = "pyeongsu")
    private long pyeongsu;

    @Column(name = "realestate_type", nullable = false)
    private RealEstateType realEstateType;

    @Column(name = "floor_num")
    private long floorNum;

    @Column(name = "is_temporal", nullable = false)
    private boolean isTemporal;
}
