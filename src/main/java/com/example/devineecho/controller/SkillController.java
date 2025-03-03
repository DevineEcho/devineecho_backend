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
    public ResponseEntity<List<Skill>> getOwnedSkills() {
        String username = getAuthenticatedUsername();
        return playerService.findByUsername(username)
                .map(player -> {
                    List<Skill> equippedSkills = player.getEquippedSkills();
                    List<Skill> ownedSkills = player.getPurchasedSkills()
                            .stream()
                            .filter(skill -> !equippedSkills.contains(skill))
                            .filter(skill -> skill.getSkillType() != Skill.SkillType.ENEMY)
                            .toList();

                    return ResponseEntity.ok(ownedSkills);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/equipped-skills")
    public ResponseEntity<List<Skill>> getEquippedSkills() {
        String username = getAuthenticatedUsername();
        return playerService.findByUsername(username)
                .map(player -> {
                    List<Skill> equippedSkills = player.getEquippedSkills();
                    
                    if (equippedSkills.isEmpty()) {
                        equippedSkills = skillService.getDefaultSkills(player);
                        player.updateEquippedSkills(equippedSkills);
                        playerService.savePlayer(player);
                    }
                    return ResponseEntity.ok(equippedSkills);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/equip-skills")
    public ResponseEntity<?> saveEquippedSkills(@RequestBody Map<String, Long> equippedSkills) {
        String username = getAuthenticatedUsername();
        return playerService.findByUsername(username)
                .map(player -> {
                    skillService.equipSkills(player, equippedSkills);
                    playerService.savePlayer(player);
                    return ResponseEntity.ok("스킬 장착 완료");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
