package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CheckedFurniture")
@Getter
@NoArgsConstructor
public class CheckedFurniture {

    @Id
    @Column(name = "checked_furniture_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long checkedFurnitureId;
}
