package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CheckedFurniture")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckedFurniture {

    @Id
    @Column(name = "checked_furniture_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long checkedFurnitureId;

    @ManyToOne
    @JoinColumn(name = "kok_id", nullable = false)
    private  Kok kok;

    @ManyToOne
    @JoinColumn(name = "furniture_option_id", nullable = false)
    private FurnitureOption furnitureOption;

    public CheckedFurniture(Kok kok, FurnitureOption furnitureOption){
        this.kok = kok;
        this.furnitureOption = furnitureOption;
    }
}
