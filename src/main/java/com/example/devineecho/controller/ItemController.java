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
import java.util.Optional;

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
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/owned")
    public ResponseEntity<List<Item>> getOwnedItems() {
        String username = getAuthenticatedUsername();
        return playerService.findByUsername(username)
                .map(player -> ResponseEntity.ok(player.getInventory()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/equipment")
    public ResponseEntity<List<Item>> getEquipmentItems() {
        String username = getAuthenticatedUsername();
        return playerService.findByUsername(username)
                .map(player -> ResponseEntity.ok(itemService.getPlayerItemsByType(player, Item.ItemType.EQUIPMENT)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/skins")
    public ResponseEntity<List<Item>> getSkinItems() {
        String username = getAuthenticatedUsername();
        return playerService.findByUsername(username)
                .map(player -> ResponseEntity.ok(itemService.getPlayerItemsByType(player, Item.ItemType.SKIN)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/equip-equipment")
    public ResponseEntity<Player> equipEquipment(@RequestParam Long itemId) {
        String username = getAuthenticatedUsername();
        return playerService.findByUsername(username)
                .map(player -> {
                    Optional<Item> itemOpt = itemService.getItemById2(itemId);
                    if (itemOpt.isPresent()) {
                        player.updatePlayerCharacterSkin(itemOpt.get());
                        Player updatedPlayer = playerService.saveAndMergePlayer(player);
                        return ResponseEntity.ok(updatedPlayer);
                    }
                    return ResponseEntity.notFound().<Player>build();
                }).orElseGet(() -> ResponseEntity.notFound().<Player>build());
    }

    @PostMapping("/equip-skin")
    public ResponseEntity<Player> equipSkin(@RequestParam Long itemId) {
        String username = getAuthenticatedUsername();
        return playerService.findByUsername(username)
                .map(player -> {
                    Optional<Item> itemOpt = itemService.getItemById2(itemId);
                    if (itemOpt.isPresent()) {
                        player.updatePlayerSkillSkin(itemOpt.get());
                        Player updatedPlayer = playerService.saveAndMergePlayer(player);
                        return ResponseEntity.ok(updatedPlayer);
                    }
                    return ResponseEntity.notFound().<Player>build();
                }).orElseGet(() -> ResponseEntity.notFound().<Player>build());
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buyItem(@RequestParam Long itemId, @RequestParam String currencyType) {
        String username = getAuthenticatedUsername();
        return playerService.findByUsername(username)
                .map(player -> {
                    try {
                        itemService.purchaseItem(player, itemId, currencyType);
                        return ResponseEntity.ok(playerService.saveAndMergePlayer(player));
                    } catch (InsufficientCurrencyException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                    }
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
