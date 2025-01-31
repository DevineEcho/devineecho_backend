package com.example.devineecho.service;


import com.example.devineecho.model.Player;
import com.example.devineecho.model.Skill;
import com.example.devineecho.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public Skill saveSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }

    public Skill getSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Skill with ID " + id + " not found"));
    }

    public Skill getSkillByName(String name) {
        return skillRepository.findByName(name).orElse(null);
    }


    public void equipSkills(Player player, Map<String, Long> equippedSkills) {
        player.getEquippedSkills().clear();

        if (equippedSkills.containsKey("skill1")) {
            player.getEquippedSkills().add(getSkillById(equippedSkills.get("skill1")));
        }

        if (equippedSkills.containsKey("skill2")) {
            player.getEquippedSkills().add(getSkillById(equippedSkills.get("skill2")));
        }

        if (equippedSkills.containsKey("skill3")) {
            player.getEquippedSkills().add(getSkillById(equippedSkills.get("skill3")));
        }
    }
}
