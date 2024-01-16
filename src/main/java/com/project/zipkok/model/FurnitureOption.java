package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "FurnitureOption")
@Getter
@NoArgsConstructor
public class FurnitureOption {

    @Id
    @Column(name = "furniture_option_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long furnitureOptionId;

    @Column(name ="furniture_name", nullable = false)
    private String furnitureName;

    @Column(name ="icon_url", nullable = false)
    private String iconUrl;

    @OneToMany(mappedBy = "furnitureOption", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CheckedFurniture> checkedFurnitures = new ArrayList<>();

    public FurnitureOption(String furnitureName, String iconUrl){
        this.furnitureName = furnitureName;
        this.iconUrl = iconUrl;
    }
}
