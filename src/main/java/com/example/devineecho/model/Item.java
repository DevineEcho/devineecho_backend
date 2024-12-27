package com.example.devineecho.model;

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

    @Column(nullable = false, unique = true)
    private String name;

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

    public enum ItemType {
        EQUIPMENT,
        SKILL,
        SKIN
    }

    public Item(String name, int requiredGold, int requiredDiamond, ItemType itemType, String skinType, String targetSkill) {
        if (requiredGold <= 0 && requiredDiamond <= 0) {
            throw new IllegalArgumentException("구매금액이 설정되어야합니다");
        }
        this.name = name;
        this.requiredGold = requiredGold;
        this.requiredDiamond = requiredDiamond;
        this.itemType = itemType;
        this.skinType = skinType;
        this.targetSkill = targetSkill;
    }
}
