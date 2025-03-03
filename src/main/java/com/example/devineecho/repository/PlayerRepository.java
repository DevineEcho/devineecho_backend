package com.example.devineecho.repository;

import com.example.devineecho.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByUsername(String name);

    Optional<Player> findByKakaoId(String kakaoId);
}
