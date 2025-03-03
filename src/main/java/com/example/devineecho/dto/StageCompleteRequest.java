package com.example.devineecho.dto;

import com.example.devineecho.model.Skill;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StageCompleteRequest {
    private Long playerId;
    private int level;
    private int exp;
    private int stage;
    private int health;
    private List<Skill> playerSkills;
    private List<Skill> enemySkills;
}
