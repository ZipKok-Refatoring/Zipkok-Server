package com.project.zipkok.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CheckedOption")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckedOption {

    @Id
    @Column(name = "checked_option_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long checkedOptionId;

    @ManyToOne
    @JoinColumn(name ="kok_id",  nullable = false)
    private Kok kok;

    @ManyToOne
    @JoinColumn(name ="option_id", nullable = false)
    private Option option;

    public CheckedOption(Kok kok, Option option){
        this.kok = kok;
        this.option = option;
    }


}
