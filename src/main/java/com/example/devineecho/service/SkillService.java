package com.example.devineecho.service;


import com.example.devineecho.dto.SkillDto;
import com.example.devineecho.model.Player;
import com.example.devineecho.model.Skill;
import com.example.devineecho.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Skill getSkillByName(String skillName) {
        Skill skill = skillRepository.findByName(skillName)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found: " + skillName));
        return Skill.builder()
                .name(skill.getName())
                .level(skill.getLevel())
                .skillType(skill.getSkillType())
                .build();
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

    public SkillDto convertToSkillDto(Skill skill) {
        SkillDto skillDto = new SkillDto();
        skillDto.setName(skill.getName());
        skillDto.setLevel(skill.getLevel());
        return skillDto;
    }

    public List<SkillDto> convertToSkillDtoList(List<Skill> skills) {
        return skills.stream()
                .map(this::convertToSkillDto)
                .collect(Collectors.toList());
    }

    public Skill getOrCreateSkill(String name, Player player) {
        Skill skill = skillRepository.findByNameAndPlayer(name, player)
                .orElse(null);

        if (skill != null) {
            return skill;
        }

        Skill newSkill = new Skill();
        newSkill.setName(name);
        newSkill.setLevel(1);
        newSkill.setSkillType(Skill.SkillType.PLAYER);
        newSkill.setPlayer(player);

        return skillRepository.save(newSkill);
    }





    public List<Skill> getDefaultSkills(Player player) {
        System.out.println("기본 스킬 가져오기 실행됨 for player: " + player.getUsername());

        List<Skill> defaultSkills = Arrays.asList(
                getOrCreateSkill("HolyCircle", player),
                getOrCreateSkill("SaintAura", player),
                getOrCreateSkill("GodsHammer", player)
        );

        player.updatePurchasedSkills(defaultSkills);
        return defaultSkills;
    }


    }




