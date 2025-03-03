package com.example.devineecho.controller;

import com.example.devineecho.dto.EquippedSkillDto;
import com.example.devineecho.dto.StageCompleteRequest;
import com.example.devineecho.model.Item;
import com.example.devineecho.model.Player;
import com.example.devineecho.service.PlayerService;
import com.example.devineecho.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;
    private final SkillService skillService;

    @Autowired
    public PlayerController(PlayerService playerService, SkillService skillService) {
        this.playerService = playerService;
        this.skillService = skillService;
    }

    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @GetMapping("/api/players/{id}")
    public ResponseEntity<EquippedSkillDto> getPlayer(@PathVariable Long id) {
        Player player = playerService.getPlayerById(id);

        EquippedSkillDto dto = new EquippedSkillDto();
        dto.setUsername(player.getUsername());
        dto.setEquippedSkills(skillService.convertToSkillDtoList(player.getEquippedSkills()));

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/reset")
    public ResponseEntity<Player> resetPlayerData() {
        String username = getAuthenticatedUsername();

        return playerService.findByUsername(username)
                .map(player -> {
                    playerService.resetPlayerData(username);
                    return ResponseEntity.ok(player);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/load")
    public ResponseEntity<Player> loadPlayerData() {
        String username = getAuthenticatedUsername();
        return playerService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping("/items")
    public ResponseEntity<List<Item>> getPlayerItems() {
        String username = getAuthenticatedUsername();
        return playerService.findByUsername(username)
                .map(player -> ResponseEntity.ok(player.getInventory()))
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/stageClear")
    public ResponseEntity<String> saveStageData(@RequestBody StageCompleteRequest request) {
        String username = getAuthenticatedUsername();
        return playerService.findByUsername(username)
                .map(player -> {
                    playerService.updateStageData(player, request);
                    return ResponseEntity.ok("스테이지 저장 완료");
                })
                .orElse(ResponseEntity.notFound().build());
    }



}
