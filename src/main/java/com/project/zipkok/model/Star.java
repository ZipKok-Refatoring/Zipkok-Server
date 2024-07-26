package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.type.descriptor.jdbc.TinyIntJdbcType;

@Entity
@Table(name = "Star")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Star {

    @Id
    @Column(name = "star_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long starId;

    @Column(name ="facility_star", nullable = false)
    private int facilityStar;

    @Column(name = "infra_star", nullable = false)
    private int infraStar;

    @Column(name = "structure_star", nullable = false)
    private int structureStar;

    @Column(name = "vibe_star", nullable = false)
    private int vibeStar;

    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "kok_id", nullable = true)
    private Kok kok;

    public Star(int facilityStar, int infraStar, int structureStar, int vibeStar, Kok kok){
        this.facilityStar = facilityStar;
        this.infraStar = infraStar;
        this.structureStar = structureStar;
        this.vibeStar = vibeStar;
        this.kok = kok;
    }
}
