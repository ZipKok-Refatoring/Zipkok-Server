package com.project.zipkok.model;

import com.project.zipkok.common.enums.OptionCategory;
import com.project.zipkok.dto.PostUpdateKokOptionRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public static List<DetailOption> makeDefaultDetailOptions(List<Option> options){

        Map<Option, List<String>> detailOptions = new LinkedHashMap<Option, List<String>>();

        for(Option option : options){
            switch (option.getName()) {
                case "편의성":
                    detailOptions.put(option, List.of("학교/직장과 가깝나요?", "편의점이 근처에 있나요?", "대형마트가 근처에 있나요?", "엘리베이터가 있나요?", "무인 택배 보관함이 있나요?", "전용 분리수거장이 있나요?", "주차할 수 있는 주차장 공간이 있나요?"));
                    break;
                case "접근성":
                    detailOptions.put(option, List.of("언덕과 오르막길이 있나요?", "골목길이 많나요?", "가로등이 충분히 있나요?", "지하철역과 가깝나요?"));
                    break;
                case "보안":
                    detailOptions.put(option, List.of("공동현관이 있나요?", "거주 전용 CCTV가 있나요?", "주변에 술집/유흥시설이 있나요?", "관리자/관리시설이 있나요?"));
                    break;
                case "디테일":
                    if (option.getCategory().equals(OptionCategory.OUTER)) {
                        detailOptions.put(option, List.of("1층에 음식점이 없나요?", "지어진지 얼마 안 된 신축인가요?", "한 층에 거주하는 세대가 많아요?"));
                        break;
                    }
                    else if (option.getCategory().equals(OptionCategory.INNER)) {
                        detailOptions.put(option, List.of("콘센트 개수가 충분한가요?", "인터넷 선 위치가 적절한가요?", "난방/냉방이 중앙난방인가요?", "인터넷 통신망이 뚫려있나요?", "빨래 건조할 공간이 충분한가요?"));
                        break;
                    }
                    break;
                case "현관/보안":
                    detailOptions.put(option, List.of("별도의 현관문 잠금장치가 있나요?", "외부를 볼 수 있는 인터폰이 있나요?", "공간을 분리하는 현관 문턱이 있나요?", "로비/복도에 CCTV가 있나요?"));
                    break;
                case "부엌":
                    detailOptions.put(option, List.of("싱크대 수압이 충분한가요?", "화구(ex, 가스레인지)의 화력이 충분한가요?", "냉장고의 크기가 충분한가요?", "싱크대의 크기가 충분한가요?", "물을 틀었을 때, 싱크대 아래에 물이 새나요?", "싱크대 아래에 곰팡이 흔적이 있나요?", "수납장/냉장고 뒤에 바퀴벌레 약이 있나요?"));
                    break;
                case "화장실":
                    detailOptions.put(option, List.of("화장실에 창문이 있나요?", "변기 물을 내렸을 때 잘 내려가나요?", "세면대 물 수압이 충분한가요?", "선반과 휴지걸이에 녹이 슬어있나요?", "타일 사이에 때가 껴있나요?", "배수구에서 불쾌한 냄새가 나는가요?", "온수가 금방 나오나요?", "환풍기가 정상적으로 작동하나요?"));
                    break;
                case "방/거실":
                    detailOptions.put(option, List.of("침대 놓을 공간이 충분한가요?", "벽지에 곰팡이 흔적이 있나요?", "곰팡이 냄새, 불쾌한 냄새가 나나요?", "장판에 긁힘, 찍힘 자국이 있나요?", "벽을 두드렸을 때 가벽인 것 같나요?"));
                    break;
                case "채광/창문/환기":
                    detailOptions.put(option, List.of("방충망이 있나요?", "앞 건물에서 우리 집이 보이나요?", "앞 건물에 가려져 그늘이 지나요?", "햇빛이 잘 들어오나요?", "창문의 크기가 충분히 큰가요?", "창문에 결로현상(물맺힘)이 있나요?", "창문에서 찬바람, 찬기가 느껴지나요?"));
                    break;
                case "옵션 상태 확인":
                    detailOptions.put(option, List.of("세탁기 상태가 깨끗한가요?", "에어컨은 잘 작동하나요?", "냉장고에 성에가 끼어있나요?", "인덕션/가스레인지는 잘 작동하나요?"));
                    break;
                case "집주인/매물 관련 질문체크":
                    detailOptions.put(option, List.of("집주인이 건물에 함께 상주하나요?", "관리비 포함 여부를 질문했나요?", "새로 벽지 도배가 가능한가요?", "장판 교체가 가능한가요?", "하자있는 부분 보수해주시나요?", "입주 청소해주시나요?", "반려동물 가능한가요?"));
                    break;
                case "보증금/월세 관련 질문체크":
                    detailOptions.put(option, List.of("보증금 대출 가능한가요?", "보증 보험 가입이 되나요?", "관리비에 어떤 것이 포함되어 있나요?", "공과금은 보통 얼마정도 나오나요?"));
                    break;
                case "계약 관련 질문 체크":
                    detailOptions.put(option, List.of("중개 수수료는 얼마인가요?", "전입신고가 가능한가요?", "이사 전날까지 수도/전기요금 정산됐나요?", "집주인의 빚(근저당)이 있나요?", "계약하는 분이 집주인이 맞나요?", "특약사항 기재가 가능한가요?"));
                    break;

            }
        }

        return detailOptions.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(string -> new DetailOption(string, true, entry.getKey()))
                ).toList();
    }
}
