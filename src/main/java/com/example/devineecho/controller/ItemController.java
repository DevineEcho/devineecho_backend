package com.example.devineecho.controller;

import com.example.devineecho.exception.InsufficientCurrencyException;
import com.example.devineecho.model.Item;
import com.example.devineecho.model.Player;
import com.example.devineecho.service.ItemService;
import com.example.devineecho.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    private final PlayerService playerService;

    @Autowired
    public ItemController(ItemService itemService, PlayerService playerService) {
        this.itemService = itemService;
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<Item>> getItems() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseItem(@RequestParam Long itemId, @RequestParam String currencyType) {
        String username = getAuthenticatedUsername();
        return playerService.findByUsername(username)
                .map(player -> {
                    try {
                        itemService.purchaseItem(player, itemId, currencyType.toUpperCase());
                        Player updatedPlayer = playerService.savePlayer(player);
                        return ResponseEntity.ok(updatedPlayer);
                    } catch (InsufficientCurrencyException e) {
                        return ResponseEntity.badRequest().body(
                                Map.of(
                                        "message", e.getMessage(),
                                        "missingCurrency", e.getCurrencyType(),
                                        "missingAmount", e.getMissingAmount()
                                )
                        );
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/owned")
    public ResponseEntity<List<Item>> getOwnedItems() {
        String username = getAuthenticatedUsername();
        return playerService.findByUsername(username)
                .map(player -> ResponseEntity.ok(player.getOwnedItems()))
                .orElse(ResponseEntity.notFound().build());
    }

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
