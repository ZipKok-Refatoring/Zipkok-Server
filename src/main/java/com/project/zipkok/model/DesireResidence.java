package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DesireResidence")
@NoArgsConstructor
@Getter
@Setter
public class DesireResidence {

    @Id
    @Column(name = "desire_residence_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long desireResidenceId;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "status", nullable = false)
    private String status = "active";

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public DesireResidence(User user) {
        this.user = user;
    }

}
