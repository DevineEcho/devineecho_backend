package com.example.devineecho.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    @JsonBackReference
    private Player player;

    @Column(nullable = false, unique = true)
    private String name; // 이미지명 (영어)

    @Column(nullable = false)
    private String displayName; // 아이템명 (한글)

    @Column(nullable = false)
    private int requiredGold;

    @Column(nullable = false)
    private int requiredDiamond;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    @Column
    private String skinType;

    @Column
    private String targetSkill;

    @Column(length = 1000) // 설명은 최대 1000자로 설정
    private String description;

    public enum ItemType {
        EQUIPMENT,
        SKILL,
        SKIN
    }

    public Item(String name, String displayName, int requiredGold, int requiredDiamond, ItemType itemType, String skinType, String targetSkill, String description) {
        if (requiredGold <= 0 && requiredDiamond <= 0) {
            throw new IllegalArgumentException("구매금액이 설정되어야합니다");
        }
        this.name = name;
        this.displayName = displayName;
        this.requiredGold = requiredGold;
        this.requiredDiamond = requiredDiamond;
        this.itemType = itemType;
        this.skinType = skinType;
        this.targetSkill = targetSkill;
        this.description = description;
    }

    public void assignToPlayer(Player player) {
        this.player = player;
    }
}
