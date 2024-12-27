package com.example.devineecho.service;

import com.example.devineecho.model.Item;
import com.example.devineecho.model.Player;
import com.example.devineecho.model.Skill;
import com.example.devineecho.model.StageCompleteRequest;
import com.example.devineecho.repository.ItemRepository;
import com.example.devineecho.repository.PlayerRepository;
import com.example.devineecho.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService implements UserDetailsService {

    private final PlayerRepository playerRepository;
    private final SkillRepository skillRepository;
    private final ItemRepository itemRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public PlayerService(PlayerRepository playerRepository, SkillRepository skillRepository, PasswordEncoder passwordEncoder, ItemRepository itemRepository) {
        this.playerRepository = playerRepository;
        this.skillRepository = skillRepository;
        this.passwordEncoder = passwordEncoder;
        this.ItemRepository = itemRepository;
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }

    public Player resetPlayerData(String username) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        player.resetPlayerData();
        return playerRepository.save(player);
    }

    public Player loadPlayerData(String username) {
        return playerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }


    public Optional<Player> findByUsername(String username) {
        return playerRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return playerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public void completeStageWithSkills(String username, StageCompleteRequest request) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        player.updateStageProgress(request.getLevel(), request.getExp(), request.getStage());

        List<Skill> existingPlayerSkills = skillRepository.findBySkillTypeAndPlayer(Skill.SkillType.PLAYER, player);
        skillRepository.deleteAll(existingPlayerSkills);

        List<Skill> newPlayerSkills = request.getPlayerSkills().stream()
                .map(skill -> Skill.builder()
                        .name(skill.getName())
                        .level(skill.getLevel())
                        .skillType(Skill.SkillType.PLAYER)
                        .player(player)
                        .build())
                .toList();
        skillRepository.saveAll(newPlayerSkills);

        List<Skill> existingEnemySkills = skillRepository.findBySkillTypeAndPlayer(Skill.SkillType.ENEMY, player);
        skillRepository.deleteAll(existingEnemySkills);

        List<Skill> newEnemySkills = request.getEnemySkills().stream()
                .map(skill -> Skill.builder()
                        .name(skill.getName())
                        .level(skill.getLevel())
                        .skillType(Skill.SkillType.ENEMY)
                        .player(player)
                        .build())
                .toList();
        skillRepository.saveAll(newEnemySkills);


        playerRepository.save(player);
    }



    public Player updatePlayerSkills(String username, List<Skill> newSkills) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));


        skillRepository.deleteAll(player.getSkills());


        newSkills.forEach(skill -> {
            skill.setPlayer(player);
            skill.setSkillType(Skill.SkillType.PLAYER);
        });
        skillRepository.saveAll(newSkills);

        player.getSkills().clear();
        player.getSkills().addAll(newSkills);

        return playerRepository.save(player);
    }

    public void purchaseSkin(String username, Long itemId) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found."));

        if (!item.getItemType().equals(Item.ItemType.SKIN)) {
            throw new IllegalArgumentException("Item is not a skin.");
        }

        // Check if the player can afford the item
        if (player.getGold() >= item.getRequiredGold() || player.getDiamond() >= item.getRequiredDiamond()) {
            if (item.getRequiredGold() > 0 && player.getGold() >= item.getRequiredGold()) {
                player.subtractGold(item.getRequiredGold());
            } else if (item.getRequiredDiamond() > 0 && player.getDiamond() >= item.getRequiredDiamond()) {
                player.subtractDiamond(item.getRequiredDiamond());
            } else {
                throw new IllegalArgumentException("Insufficient funds.");
            }
        }

        player.addItemToInventory(item);
        playerRepository.save(player);
    }


    public void equipSkin(String username, Long itemId) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found."));

        if (!item.getItemType().equals(Item.ItemType.SKIN)) {
            throw new IllegalArgumentException("Item is not a skin.");
        }

        switch (item.getSkinType()) {
            case "CHARACTER":
                player.setEquippedCharacterSkin(item);
                break;
            case "SKILL":
                player.setEquippedSkillSkin(item);
                break;
            case "ENEMY":
                player.setEquippedEnemySkin(item);
                break;
            default:
                throw new IllegalArgumentException("Unknown skin type.");
        }

        playerRepository.save(player);
    }


    public void saveSkins(String username, Player updatedPlayer) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        player.setEquippedCharacterSkin(updatedPlayer.getEquippedCharacterSkin());
        player.setEquippedSkillSkin(updatedPlayer.getEquippedSkillSkin());
        player.setEquippedEnemySkin(updatedPlayer.getEquippedEnemySkin());

        playerRepository.save(player);
    }


}
