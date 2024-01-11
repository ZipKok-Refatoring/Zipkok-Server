package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Kok")
@Getter
@NoArgsConstructor
public class Kok {

    @Id
    @Column(name = "kok_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long kokId;

    @Column(name = "direction", nullable = true)
    private String direction;

    @ManyToOne
    @JoinColumn(name = "realestate_id", nullable = false)
    private RealEstate realEstate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Kok(RealEstate realEstate, User user) {
        this.realEstate = realEstate;
        this.user = user;
    }

}
