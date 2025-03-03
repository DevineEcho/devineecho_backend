package com.example.devineecho.repository;

import com.example.devineecho.model.Player;
import com.example.devineecho.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByName(String name);
    Optional<Skill> findFirstByName(String name);

    Optional<Skill> findByNameAndPlayer(String name, Player player);

}
