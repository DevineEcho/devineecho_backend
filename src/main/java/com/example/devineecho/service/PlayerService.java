package com.example.devineecho.service;

import com.example.devineecho.dto.StageCompleteRequest;
import com.example.devineecho.model.Player;
import com.example.devineecho.model.Skill;
import com.example.devineecho.repository.PlayerRepository;
import com.example.devineecho.repository.SkillRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService implements UserDetailsService {

    private final PlayerRepository playerRepository;
    private final SkillRepository skillRepository;

    private final EntityManager entityManager;
    private final SkillService skillService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, SkillRepository skillRepository, SkillService skillService, PasswordEncoder passwordEncoder, EntityManager entityManager) {
        this.playerRepository = playerRepository;
        this.skillRepository = skillRepository;
        this.skillService = skillService;
        this.passwordEncoder = passwordEncoder;
        this.entityManager = entityManager;
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Optional<Player> findByUsername(String username) {
        return playerRepository.findByUsername(username);
    }

    public Player getPlayerById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Player not found with ID: " + id));
    }

    public Optional<Player> findByKakaoId(String kakaoId) {
        return playerRepository.findByKakaoId(kakaoId);
    }

    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }


    public Player saveAndMergePlayer(Player player) {
        Player mergedPlayer = entityManager.merge(player);
        playerRepository.save(mergedPlayer);
        return mergedPlayer;
    }


    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }

    public Player resetPlayerData(String username) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        player.updateLevel(1);
        player.updateExperience(0);
        player.updateCurrentStage(1);
        player.updateHealth(100);
        player.updateGold(0);
        player.updateDiamond(0);
        player.getInventory().clear();
        player.getPurchasedSkills().clear();

        List<Skill> equippedSkillsBeforeReset = new ArrayList<>(player.getEquippedSkills());

        player.getPurchasedSkills().clear();
        player.getEquippedSkills().clear();

        Skill holyCircle = skillRepository.findFirstByName("HolyCircle")
                .orElseThrow(() -> new RuntimeException("HolyCircle not found"));

        if (!equippedSkillsBeforeReset.contains(holyCircle)) {
            equippedSkillsBeforeReset.add(holyCircle);
        }

        for (Skill skill : equippedSkillsBeforeReset) {
            if (!player.getPurchasedSkills().contains(skill)) {
                player.getPurchasedSkills().add(skill);
            }
            if (!player.getEquippedSkills().contains(skill)) {
                player.getEquippedSkills().add(skill);
            }
        }

        return playerRepository.save(player);
    }


    @Transactional
    public void updateStageData(Player player, StageCompleteRequest request) {
        player.updateCurrentStage(request.getStage());
        player.updateLevel(request.getLevel());
        player.updateExperience(request.getExp());
        player.updateHealth(request.getHealth());


        List<Skill> newSkills = request.getPlayerSkills();
        newSkills.forEach(skill -> skill.setPlayer(player));
        player.updateEquippedSkills(newSkills);

        List<Skill> newEnemySkills = request.getEnemySkills();
        newEnemySkills.forEach(skill -> skill.setPlayer(player));
        player.updateEnemySkills(newEnemySkills);

        playerRepository.save(player);
    }


    public Player loadPlayerData(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }


}
