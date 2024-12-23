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

    private int level = 1;
    private int experience = 0;
    private int currentStage = 1;
    private int health = 100;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> skills;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // 현재 권한 정보는 비어 있음
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 설정
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부 설정
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 인증 정보 만료 여부 설정
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부 설정
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
        this.health = 100;
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


    public void updateHealth(int newHealth) {
        if (newHealth < 0) {
            throw new IllegalArgumentException("Health cannot be negative");
        }
        this.health = newHealth;
    }


    public void updateSkills(List<Skill> newSkills) {
        if (this.skills != null) {
            this.skills.clear();
        }
        this.skills = newSkills;
    }
}
