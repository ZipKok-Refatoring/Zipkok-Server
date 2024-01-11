package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Highlight")
@Getter
@NoArgsConstructor
public class Highlight {

    @Id
    @Column(name ="highlight_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long highlightId;

    @Column(name ="title", nullable = false)
    private String title;

    public Highlight(String title){
        this.title = title;
    }
}
