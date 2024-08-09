package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "status", nullable = false)
    private String status = "active";

    @OneToMany(mappedBy = "highlight", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CheckedHighlight> checkedHighlights = new ArrayList<>();

    @Builder
    public static Highlight of(String title, User user){
        return Highlight.builder()
                .title(title)
                .user(user)
                .status("active")
                .build();
    }

    public static Set<Highlight> makeDefaultHighlights(User user){
        Set<String> highlightNames = Set.of("CCTV", "주변공원", "현관보안", "편세권", "주차장", "역세권", "더블역세권", "트리플역세권");

        Set<Highlight> defaultHighlights = new LinkedHashSet<>();

        for(String highlightTitle : highlightNames){
            defaultHighlights.add(Highlight.of(highlightTitle, user));
        }

        return defaultHighlights;
    }
}
