package com.example.devineecho.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class EquippedSkillDto {
    private String username;
    private List<SkillDto> equippedSkills;
}
