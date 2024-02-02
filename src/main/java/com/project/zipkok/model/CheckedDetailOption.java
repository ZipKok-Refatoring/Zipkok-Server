package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.mapping.Join;

@Entity
@Table(name = "CheckedDetailOption")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckedDetailOption {

    @Id
    @Column(name = "checked_detail_option_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long checkedDetailOptionId;

    @ManyToOne
    @JoinColumn(name = "kok_id", nullable = false)
    private Kok kok;

    @ManyToOne
    @JoinColumn(name ="detail_option_id", nullable = false)
    private DetailOption detailOption;

    public CheckedDetailOption(Kok kok, DetailOption detailOption){
        this.kok = kok;
        this.detailOption = detailOption;
    }
}
