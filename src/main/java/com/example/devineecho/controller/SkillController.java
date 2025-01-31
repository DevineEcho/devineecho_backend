package com.example.devineecho.controller;

import com.example.devineecho.model.Skill;
import com.example.devineecho.service.PlayerService;
import com.example.devineecho.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;
    private final PlayerService playerService;

    @Autowired
    public SkillController(SkillService skillService, PlayerService playerService) {
        this.skillService = skillService;
        this.playerService = playerService;
    }

    @GetMapping
    public List<Skill> getAllSkills() {
        return skillService.getAllSkills();
    }

    @PostMapping
    public Skill createSkill(@RequestBody Skill skill) {
        return skillService.saveSkill(skill);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/equip-skills")
    public ResponseEntity<?> saveEquippedSkills(@RequestBody Map<String, Long> equippedSkills) {
        String username = getAuthenticatedUsername();
        return playerService.findByUsername(username)
                .map(player -> {
                    skillService.equipSkills(player, equippedSkills);

                    // Holy Circle이 없는 경우 기본으로 추가
                    if (!player.getSkills().stream().anyMatch(skill -> skill.getName().equals("Holy Circle"))) {
                        Skill holyCircle = skillService.getSkillByName("Holy Circle");
                        if (holyCircle != null) {
                            player.getSkills().add(holyCircle);
                        }
                    }

                    playerService.savePlayer(player);
                    return ResponseEntity.ok("Skills saved successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/equipped-skills")
    public ResponseEntity<List<Skill>> getEquippedSkills() {
        String username = getAuthenticatedUsername();
        return playerService.findByUsername(username)
                .map(player -> ResponseEntity.ok(player.getSkills()))
                .orElse(ResponseEntity.notFound().build());
    }


    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
