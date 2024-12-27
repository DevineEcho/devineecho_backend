package com.example.devineecho.controller;

import com.example.devineecho.model.Player;
import com.example.devineecho.model.Skill;
import com.example.devineecho.model.StageCompleteRequest;
import com.example.devineecho.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }


    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        return playerService.getPlayerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        Player savedPlayer = playerService.savePlayer(player);
        return ResponseEntity.ok(savedPlayer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/profile")
    public ResponseEntity<Player> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Player player = playerService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return ResponseEntity.ok(player);
    }


    @PostMapping("/reset")
    public ResponseEntity<Player> resetPlayerData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Player player = playerService.resetPlayerData(username);
        return ResponseEntity.ok(player);
    }

    @GetMapping("/load")
    public ResponseEntity<Player> loadPlayerData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return playerService.loadPlayerData(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/stageClear")
    public ResponseEntity<String> completeStage(@RequestBody StageCompleteRequest request) {
        // 현재 인증된 사용자의 username 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 스테이지 완료 처리
        playerService.completeStageWithSkills(username, request);
        return ResponseEntity.ok("Stage complete data (including skills) saved!");
    }

    @PostMapping("/updateSkill")
    public ResponseEntity<Player> updateSkills(@RequestBody List<Skill> newSkills) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 스킬 업데이트 처리
        Player updatedPlayer = playerService.updatePlayerSkills(username, newSkills);
        return ResponseEntity.ok(updatedPlayer);
    }

    @PostMapping("/equip-skin")
    public ResponseEntity<String> equipSkin(@RequestParam Long itemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        playerService.equipSkin(username, itemId);
        return ResponseEntity.ok("Skin equipped successfully!");
    }

    @PostMapping("/save-skins")
    public ResponseEntity<String> saveSkins(@RequestBody Player updatedPlayer) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        playerService.saveSkins(username, updatedPlayer);
        return ResponseEntity.ok("Skins saved successfully!");
    }


}
