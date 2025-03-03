package com.example.devineecho.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JoinColumn(name = "player_id", nullable = true)
    @JsonBackReference
    private Player player;


    private String name;

    private int level;

    @Enumerated(EnumType.STRING)
    private SkillType skillType;

    public enum SkillType {
        PLAYER,
        ENEMY
    }

    public Skill(String name, int level, SkillType skillType) {
        this.name = name;
        this.level = level;
        this.skillType = skillType;
    }


}
