package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DetailOption")
@Getter
@NoArgsConstructor
public class DetailOption {

    @Id
    @Column(name ="detail_option_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long detailOptionId;

    @Column(name ="name", nullable = false)
    private String name;

    @Column(name ="is_visible", nullable = false)
    private boolean isVisible;

    public DetailOption(String name, boolean isVisible){
        this.name =name;
        this.isVisible =isVisible;
    }
}
