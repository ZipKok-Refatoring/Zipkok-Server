package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CheckedImpression")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckedImpression {

    @Id
    @Column(name = "checked_impression_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long checkedImpressionId;

    @ManyToOne
    @JoinColumn(name = "kok_id", nullable = false)
    private Kok kok;

    @ManyToOne
    @JoinColumn(name = "impression_id", nullable = false)
    private Impression impression;

    public CheckedImpression(Kok kok, Impression impression){
        this.kok = kok;
        this.impression = impression;
    }
}
