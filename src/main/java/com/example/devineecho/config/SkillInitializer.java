package com.example.devineecho.config;

import com.example.devineecho.model.Skill;
import com.example.devineecho.model.Skill.SkillType;
import com.example.devineecho.repository.SkillRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class SkillInitializer {

    @Bean
    @Transactional
    public CommandLineRunner initializeSkills(SkillRepository skillRepository) {
        return args -> {
            if (skillRepository.count() == 0) {
                Skill holyCircle = skillRepository.save(Skill.builder()
                        .name("HolyCircle")
                        .level(1)
                        .skillType(SkillType.PLAYER)
                        .player(null)
                        .build());

                Skill saintAura = skillRepository.save(Skill.builder()
                        .name("SaintAura")
                        .level(1)
                        .skillType(SkillType.PLAYER)
                        .player(null)
                        .build());

                Skill godsHammer = skillRepository.save(Skill.builder()
                        .name("GodsHammer")
                        .level(1)
                        .skillType(SkillType.PLAYER)
                        .player(null)
                        .build());
                
                skillRepository.save(Skill.builder()
                        .name("EnemyGrowth")
                        .level(1)
                        .skillType(SkillType.ENEMY)
                        .player(null)
                        .build());

                skillRepository.save(Skill.builder()
                        .name("SpeedBoost")
                        .level(1)
                        .skillType(SkillType.ENEMY)
                        .player(null)
                        .build());

                skillRepository.save(Skill.builder()
                        .name("SpawnBoost")
                        .level(1)
                        .skillType(SkillType.ENEMY)
                        .player(null)
                        .build());

                System.out.println("공통 및 적 스킬 초기화");
            }
        };
    }

    private void saveSkillIfNotExists(SkillRepository skillRepository, String name, SkillType type) {
        if (skillRepository.findFirstByName(name).isEmpty()) {
            Skill skill = new Skill();
            skill.setName(name);
            skill.setLevel(1);
            skill.setSkillType(type);
            skillRepository.save(skill);
        }
    }
}
