package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "Kok", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CheckedFurniture> checkedFurniturs = new ArrayList<>();

    @OneToMany(mappedBy = "Kok", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CheckedImpression> checkedImpressions = new ArrayList<>();

    @OneToMany(mappedBy = "Kok", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CheckedHighlight> checkedHighlights = new ArrayList<>();

    @OneToMany(mappedBy = "Kok", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CheckedDetailOption> checkedDetailOptions = new ArrayList<>();

    @OneToMany(mappedBy = "Kok", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CheckedOption> checkedOptions = new ArrayList<>();

    @OneToMany(mappedBy = "Kok", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<KokImage> kokImages = new ArrayList<>();

    @OneToMany(mappedBy = "Kok", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Star> stars = new ArrayList<>();





    public Kok(RealEstate realEstate, User user) {
        this.realEstate = realEstate;
        this.user = user;
    }

}
