package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Kok")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Kok {

    @Id
    @Column(name = "kok_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long kokId;

    @Column(name = "direction", nullable = true)
    private String direction;

    @Column(name = "review", nullable = false)
    private String review;

    @ManyToOne
    @JoinColumn(name = "realestate_id", nullable = false)
    private RealEstate realEstate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "kok", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CheckedFurniture> checkedFurniturs = new ArrayList<>();

    @OneToMany(mappedBy = "kok", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CheckedImpression> checkedImpressions = new ArrayList<>();

    @OneToMany(mappedBy = "kok", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CheckedHighlight> checkedHighlights = new ArrayList<>();

    @OneToMany(mappedBy = "kok", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CheckedDetailOption> checkedDetailOptions = new ArrayList<>();

    @OneToMany(mappedBy = "kok", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CheckedOption> checkedOptions = new ArrayList<>();

    @OneToMany(mappedBy = "kok", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<KokImage> kokImages = new ArrayList<>();

    @OneToOne(mappedBy = "kok", orphanRemoval = true, cascade = CascadeType.ALL)
    private Star star = new Star();


    public Kok(RealEstate realEstate, String review, User user) {
        this.realEstate = realEstate;
        this.review = review;
        this.user = user;
    }

}
