package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Zim")
@Getter
@NoArgsConstructor
public class Zim {

    @Id
    @Column(name = "zim_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long zimId;

    @Column(name = "status", nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "realestate_id", nullable = false)
    private RealEstate realEstate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Zim(RealEstate realEstate, User user) {
        this.status = "active";
        this.realEstate = realEstate;
        this.user = user;
    }
}
