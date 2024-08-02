package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Impression")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Impression {

    @Id
    @Column(name ="impression_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long impressionId;

    @Column(name ="impression_title", nullable = false)
    private String impressionTitle;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @OneToMany(mappedBy = "impression", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CheckedImpression> checkedImpressions = new ArrayList<>();

    public Impression(String impressionTitle){
        this.impressionTitle = impressionTitle;
    }

    public static Set<Impression> makeDefaultImpressions(User user) {
        List<String> impressionNames = List.of("깔끔해요", "조용해요", "세련돼요", "심플해요", "더러워요", "냄새나요", "시끄러워요", "좁아요", "그냥 그래요", "마음에 들어요", "별로예요");

        Set<Impression> defaultImpressions = new LinkedHashSet<>();

        for(String impressionName : impressionNames) {
            defaultImpressions.add(Impression.builder()
                    .user(user)
                    .impressionTitle(impressionName)
                    .build());
        }

        return defaultImpressions;
    }
}
