package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Highlight")
@Getter
@NoArgsConstructor
@Setter
@AllArgsConstructor
@Builder
public class Highlight {

    @Id
    @Column(name ="highlight_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long highlightId;

    @Column(name ="title", nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "status", nullable = false)
    private String status = "active";

    @OneToMany(mappedBy = "highlight", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CheckedHighlight> checkedHighlights = new ArrayList<>();

    @Builder
    public Highlight(String title, User user){
        this.title = title;
        this.user = user;
    }
}
