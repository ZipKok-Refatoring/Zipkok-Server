package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CheckedHighlight")
@Getter
@NoArgsConstructor
public class CheckedHighlight {

    @Id
    @Column(name = "checked_highlight_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long checkedHighlightId;

}
