package com.example.devineecho.controller;

import com.example.devineecho.model.Item;
import com.example.devineecho.model.Player;
import com.example.devineecho.service.PlayerService;
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
        try {
            Player player = playerService.getPlayerById(id);
            return ResponseEntity.ok(player);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<Player> resetPlayerData() {
        String username = getAuthenticatedUsername();
        try {
            Player player = playerService.resetPlayerData(username);
            return ResponseEntity.ok(player);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
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
        System.out.println("Authenticated Username: " + username);
        return playerService.findByUsername(username)
                .map(player -> ResponseEntity.ok(player.getOwnedItems()))
                .orElse(ResponseEntity.notFound().build());
    }



}
