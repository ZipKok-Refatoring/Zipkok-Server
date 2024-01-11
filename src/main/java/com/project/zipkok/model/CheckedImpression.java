package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CheckedImpression")
@Getter
@NoArgsConstructor
public class CheckedImpression {

    @Id
    @Column(name = "checked_impression_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long checkedImpressionId;
}
