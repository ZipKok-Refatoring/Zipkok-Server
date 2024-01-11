package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CheckedDetailOption")
@Getter
@NoArgsConstructor
public class CheckedDetailOption {

    @Id
    @Column(name = "checked_detail_option_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long checkedDetailOptionId;
}
