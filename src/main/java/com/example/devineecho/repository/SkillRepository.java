package com.example.devineecho.repository;

import com.example.devineecho.model.Player;
import com.example.devineecho.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findBySkillTypeAndPlayer(Skill.SkillType skillType, Player player);
    Optional<Skill> findByName(String name);
}
