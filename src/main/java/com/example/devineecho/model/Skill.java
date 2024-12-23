package com.example.devineecho.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    private String name;

    private int level;

    @Enumerated(EnumType.STRING)
    private SkillType skillType;

    public enum SkillType {
        PLAYER, // 플레이어가 업그레이드한 스킬
        ENEMY   // 적이 업그레이드한 스킬
    }
}
