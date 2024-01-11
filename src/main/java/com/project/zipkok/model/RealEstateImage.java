package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "RealEstateImage")
@NoArgsConstructor
@Getter
public class RealEstateImage {

    @Id
    @Column(name = "realestate_img_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long realEstateImgId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "status", nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "realestate_id", nullable = false)
    private RealEstate realEstate;

    public RealEstateImage(String imageUrl, RealEstate realEstate) {
        this.status = "active";
        this.imageUrl = imageUrl;
        this.realEstate = realEstate;
    }
}
