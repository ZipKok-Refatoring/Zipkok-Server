package com.project.zipkok.model;

import com.project.zipkok.dto.PostUpdateKokOptionRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DetailOption")
@Getter
@Setter
@NoArgsConstructor
public class DetailOption {

    @Id
    @Column(name ="detail_option_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long detailOptionId;

    @Column(name ="name", nullable = false)
    private String name;

    @Column(name ="is_visible", nullable = false)
    private boolean isVisible;

    @ManyToOne
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    @OneToMany(mappedBy = "detailOption", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CheckedDetailOption> checkedDetailOptions = new ArrayList<>();

    public DetailOption(String name, boolean isVisible, Option option){
        this.name =name;
        this.isVisible =isVisible;
        this.option = option;
    }

    public boolean match(PostUpdateKokOptionRequest.DetailOption requestDetailOption){
        return requestDetailOption.getDetailOptionId() == detailOptionId;
    }

    public void copyInfo(PostUpdateKokOptionRequest.DetailOption requestDetailOption){
        this.isVisible = requestDetailOption.isDetailOptionIsVisible();
    }
}
