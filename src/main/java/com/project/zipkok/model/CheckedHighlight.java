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

    @ManyToOne
    @JoinColumn(name = "kok_id", nullable = false)
    private Kok kok;

    @ManyToOne
    @JoinColumn(name ="highlight_id", nullable = false)
    private Highlight highlight;

    public CheckedHighlight(Kok kok, Highlight highlight){
        this.kok = kok;
        this.highlight = highlight;
    }
}
