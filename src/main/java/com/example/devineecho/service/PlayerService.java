package com.example.devineecho.service;

import com.example.devineecho.model.Player;
import com.example.devineecho.model.Skill;
import com.example.devineecho.dto.StageCompleteRequest;
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
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, SkillRepository skillRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.skillRepository = skillRepository;
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
        Player player = findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        player.resetPlayerData();
        return savePlayer(player);
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

    public void completeStageWithSkills(String username, StageCompleteRequest request) {
        Player player = findByUsername(username)
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

        savePlayer(player);
    }
}
