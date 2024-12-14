package com.example.devineecho.service;

import com.example.devineecho.model.Player;
import com.example.devineecho.model.StageCompleteRequest;
import com.example.devineecho.repository.PlayerRepository;
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
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
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

    public void signup(Player player) {
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        playerRepository.save(player);
    }

    public Player resetPlayerData(String username) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        player.setLevel(1);
        player.setExperience(0);
        player.setCurrentStage(1);
        player.getSkills().clear();

        return playerRepository.save(player);
    }

    public Optional<Player> loadPlayerData(String username) {
        return playerRepository.findByUsername(username);
    }

    public Optional<Player> findByUsername(String username) {
        return playerRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return playerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public void completeStage(StageCompleteRequest request) {
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        player.setExperience(player.getExperience() + request.getExp());
        player.setCurrentStage(request.getStage());
        playerRepository.save(player);
    }
}
