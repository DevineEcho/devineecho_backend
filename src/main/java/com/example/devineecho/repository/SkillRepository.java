package com.example.devineecho.repository;

import com.example.devineecho.model.Player;
import com.example.devineecho.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findBySkillTypeAndPlayer(Skill.SkillType skillType, Player player);
}
