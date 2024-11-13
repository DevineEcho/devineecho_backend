package com.example.devineecho.repository;

import com.example.devineecho.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SkillRepository extends JpaRepository<Skill, Long> {
}
