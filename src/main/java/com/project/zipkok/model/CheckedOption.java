package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CheckedOption")
@Getter
@NoArgsConstructor
public class CheckedOption {

    @Id
    @Column(name = "checked_option_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long checkedOptionId;
}
