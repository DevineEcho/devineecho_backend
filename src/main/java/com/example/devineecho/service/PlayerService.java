package com.example.devineecho.service;

import com.example.devineecho.model.Player;
import com.example.devineecho.model.Skill;
import com.example.devineecho.repository.PlayerRepository;
import com.example.devineecho.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService implements UserDetailsService {

    private final PlayerRepository playerRepository;
    private final SkillRepository skillRepository;
    private final SkillService skillService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, SkillRepository skillRepository, SkillService skillService, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.skillRepository = skillRepository;
        this.skillService = skillService;
        this.passwordEncoder = passwordEncoder;
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

    public Player savePlayer(Player player) {
        return playerRepository.save(player);
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

        List<Skill> defaultSkills = new ArrayList<>();
        defaultSkills.add(skillService.getSkillByName("HolyCircle"));
        defaultSkills.add(skillService.getSkillByName("SaintAura"));
        defaultSkills.add(skillService.getSkillByName("GodsHammer"));

        player.getEquippedSkills().clear();
        player.getEquippedSkills().addAll(defaultSkills);

        return playerRepository.save(player);
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
