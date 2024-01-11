package com.project.zipkok.model;

import jakarta.persistence.*;
import jdk.jfr.Unsigned;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Option")
@Getter
@NoArgsConstructor
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
    private String category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public Option(String name, boolean isVisible, long orderNum, String category, User user){
        this.name = name;
        this.isVisible = isVisible;
        this.orderNum = orderNum;
        this.category = category;
        this.user = user;
    }
}
