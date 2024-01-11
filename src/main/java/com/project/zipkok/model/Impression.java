package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Impression")
@Getter
@NoArgsConstructor
public class Impression {

    @Id
    @Column(name ="impression_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long impressionId;

    @Column(name ="impression_title", nullable = false)
    private String impressionTitle;

    public Impression(String impressionTitle){
        this.impressionTitle = impressionTitle;
    }
}
