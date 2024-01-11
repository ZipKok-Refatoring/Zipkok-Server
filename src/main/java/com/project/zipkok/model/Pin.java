package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Pin")
@NoArgsConstructor
@Getter
public class Pin {

    @Id
    @Column(name = "pin_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long pinId;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "pin_nickname", nullable = false)
    private String pinNickname;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "status", nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
