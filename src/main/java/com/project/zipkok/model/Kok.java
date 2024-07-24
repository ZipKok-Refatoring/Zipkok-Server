package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "star_id", nullable = true)
    private Star star;


    public Kok(RealEstate realEstate, String review, User user) {
        this.realEstate = realEstate;
        this.review = review;
        this.user = user;
    }

}
