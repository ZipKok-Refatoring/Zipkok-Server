package com.project.zipkok.model;

import com.project.zipkok.common.enums.OptionCategory;
import jakarta.persistence.*;
import jdk.jfr.Unsigned;
import lombok.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Option {

    @Id
    @Column(name ="option_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long optionId;

    @Column(name ="name", nullable = false)
    private String name;

    @Column(name ="is_visible", nullable = false)
    private boolean isVisible;

    @Column(name ="order_num", nullable = false)
    private long orderNum;

    @Column(name ="category")
    @Enumerated(EnumType.STRING)
    private OptionCategory category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "option",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<KokImage> kokImages = new ArrayList<>();

    @OneToMany(mappedBy = "option",orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CheckedOption> checkedOptions = new ArrayList<>();

    @OneToMany(mappedBy = "option",orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<DetailOption> detailOptions = new LinkedHashSet<>();

    public Option(String name, boolean isVisible, long orderNum, OptionCategory category, User user){
        this.name = name;
        this.isVisible = isVisible;
        this.orderNum = orderNum;
        this.category = category;
        this.user = user;
    }

    public void addDetailOption(DetailOption detailOption){
        this.detailOptions.add(detailOption);
    }
}
