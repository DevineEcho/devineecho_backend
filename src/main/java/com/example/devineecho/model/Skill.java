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
        PLAYER,
        ENEMY
    }
}
