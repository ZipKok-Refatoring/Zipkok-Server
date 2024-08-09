package com.project.zipkok.model;

import com.project.zipkok.common.enums.OptionCategory;
import com.project.zipkok.dto.PostUpdateKokOptionRequest;
import jakarta.persistence.*;
import jdk.jfr.Unsigned;
import lombok.*;

import java.util.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
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

    public boolean match(PostUpdateKokOptionRequest.Option requestOption){
        return requestOption.getOptionId() == this.optionId;
    }

    public void copyInfo(PostUpdateKokOptionRequest.Option requestOption){
        if(requestOption.getOptionId().equals(this.optionId)){
            this.orderNum = requestOption.getOrderNumber();
            this.isVisible = requestOption.isVisible();
        }
    }

    public static List<Option> makeDefaultOptions(User user){

        List<String> outerOptions = new ArrayList<>();

        outerOptions.add("편의성");
        outerOptions.add("접근성");
        outerOptions.add("보안");
        outerOptions.add("디테일");

        List<String> innerOptions = new ArrayList<>();

        innerOptions.add("현관/보안");
        innerOptions.add("부엌");
        innerOptions.add("화장실");
        innerOptions.add("방/거실");
        innerOptions.add("채광/창문/환기");
        innerOptions.add("옵션 상태 확인");
        innerOptions.add("디테일");

        List<String> contractOptions = new ArrayList<>();

        contractOptions.add("집주인/매물 관련 질문체크");
        contractOptions.add("보증금/월세 관련 질문체크");
        contractOptions.add("계약 관련 질문 체크");

        List<Option> defaultOptions = new ArrayList<>();

        int orderNumber =1;
        for(String optionName : outerOptions) {
            Option option = new Option(optionName, true, orderNumber++, OptionCategory.OUTER, user);
            defaultOptions.add(option);
        }
        orderNumber =1;
        for(String optionName : innerOptions){
            Option option = new Option(optionName, true, orderNumber++, OptionCategory.INNER, user);
            defaultOptions.add(option);
        }
        orderNumber =1;
        for(String optionName : contractOptions){
            Option option = new Option(optionName, true, orderNumber++, OptionCategory.CONTRACT, user);
            defaultOptions.add(option);
        }

        return defaultOptions;
    }
}
