package com.example.devineecho.service;

import com.example.devineecho.model.Player;
import com.example.devineecho.model.Skill;
import com.example.devineecho.model.StageCompleteRequest;
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
        player.encodeAndSetPassword(player.getPassword(), passwordEncoder);
        playerRepository.save(player);
    }

    public Player resetPlayerData(String username) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        player.resetPlayerData();
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

    /**
     * 스테이지 완료 시 플레이어와 적 스킬 정보를 저장
     */
    public void completeStageWithSkills(String username, StageCompleteRequest request) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 플레이어의 진행 상태 업데이트
        player.updateStageProgress(request.getLevel(), request.getExp(), request.getStage());

        // **플레이어 스킬 저장 (기존 스킬 제거 및 새로 저장)**
        List<Skill> existingPlayerSkills = skillRepository.findBySkillTypeAndPlayer(Skill.SkillType.PLAYER, player);
        skillRepository.deleteAll(existingPlayerSkills);

        List<Skill> newPlayerSkills = request.getPlayerSkills().stream() // 플레이어 스킬 리스트를 가져옴
                .map(skill -> Skill.builder()
                        .name(skill.getName())
                        .level(skill.getLevel())
                        .skillType(Skill.SkillType.PLAYER)
                        .player(player)
                        .build())
                .toList();
        skillRepository.saveAll(newPlayerSkills);

        // **적 스킬 저장 (기존 적 스킬 제거 및 새로 저장)**
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

        // 플레이어 정보 저장
        playerRepository.save(player);
    }



    /**
     * 플레이어의 스킬 업데이트
     */
    public Player updatePlayerSkills(String username, List<Skill> newSkills) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 기존 스킬 삭제
        skillRepository.deleteAll(player.getSkills());

        // 새 스킬 추가
        newSkills.forEach(skill -> {
            skill.setPlayer(player);
            skill.setSkillType(Skill.SkillType.PLAYER);
        });
        skillRepository.saveAll(newSkills);

        player.getSkills().clear();
        player.getSkills().addAll(newSkills);

        return playerRepository.save(player);
    }
}
