package com.example.devineecho.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Player implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private int level;
    private int experience;
    private int currentStage;
    private int health;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<Skill> skills;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public void encodeAndSetPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.password = passwordEncoder.encode(rawPassword);
    }

    public void resetPlayerData() {
        this.level = 1;
        this.experience = 0;
        this.currentStage = 1;
        if (this.skills != null) {
            this.skills.clear();
        }
    }

    public void updateStageProgress(int level, int additionalExperience, int newStage) {
        if (level <= 0 || additionalExperience < 0 || newStage <= 0) {
            throw new IllegalArgumentException("Invalid stage progress data");
        }
        this.level = level;
        this.experience += additionalExperience;
        this.currentStage = newStage;
    }
}
