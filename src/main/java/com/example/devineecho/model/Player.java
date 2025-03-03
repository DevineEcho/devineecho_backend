package com.example.devineecho.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Player implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String kakaoId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String securityAnswer;

    private int level = 1;
    private int experience = 0;
    private int currentStage = 1;
    private int health = 100;
    private int maxHealth = 100;
    private int gold = 0;
    private int diamond = 0;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Item> inventory = new ArrayList<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Skill> equippedSkills = new ArrayList<>();


    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Skill> purchasedSkills = new ArrayList<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Skill> enemySkills = new ArrayList<>();


    @OneToOne(cascade = CascadeType.ALL)
    private Item equippedCharacterSkin;

    @OneToOne(cascade = CascadeType.ALL)
    private Item equippedSkillSkin;

    @OneToOne(cascade = CascadeType.ALL)
    private Item equippedEnemySkin;

    public Player(String username, String phoneNumber) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("유저명이 존재해야합니다");
        }
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("전화번호가 존재해야합니다");
        }
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public void encodeAndSetPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("비밀번호가 존재해야합니다");
        }
        this.password = passwordEncoder.encode(rawPassword);
    }

    public void initializeSecurityAnswer(String securityAnswer) {
        if (securityAnswer == null || securityAnswer.isEmpty()) {
            throw new IllegalArgumentException("비밀번호찾기 답변이 필요합니다");
        }
        this.securityAnswer = securityAnswer;
    }

    public void updatePassword(String rawPassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(rawPassword);
    }


    public void updateKakaoId(String kakaoId) {
        this.kakaoId = kakaoId;
    }
    public void updateLevel(int level) {
        this.level = level;
    }

    public void updateExperience(int experience) {
        this.experience = experience;
    }

    public void updateCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }



    public void updateHealth(int health) {
        this.health = Math.max(0, Math.min(health, this.maxHealth));
    }

    public void updateGold(int gold) {
        this.gold = gold;
    }

    public void updateDiamond(int diamond) {
        this.diamond = diamond;
    }


    public void addItemToInventory(Item item) {
        this.inventory.add(item);
    }

    public void updatePurchasedSkills(List<Skill> skills) {
        this.purchasedSkills.clear();
        this.purchasedSkills.addAll(skills);
    }

    public void updateEquippedSkills(List<Skill> skills) {
        this.equippedSkills.clear();
        this.equippedSkills.addAll(skills);
    }

    public void updateEnemySkills(List<Skill> newEnemySkills) {
        this.enemySkills.clear();
        this.enemySkills.addAll(newEnemySkills);
    }

    public void updatePlayerCharacterSkin(Item newSkin) {
        if (this.equippedCharacterSkin != null) {
            this.equippedCharacterSkin.assignToPlayer(null);
        }
        this.equippedCharacterSkin = newSkin;
        newSkin.assignToPlayer(this);
    }

    public void updatePlayerSkillSkin(Item newSkin) {
        if (this.equippedSkillSkin != null) {
            this.equippedSkillSkin.assignToPlayer(null);
        }
        this.equippedSkillSkin = newSkin;
        newSkin.assignToPlayer(this);
    }

    public void updateEnemySkin(Item newSkin) {
        if (this.equippedEnemySkin != null) {
            this.equippedEnemySkin.assignToPlayer(null);
        }
        this.equippedEnemySkin = newSkin;
        newSkin.assignToPlayer(this);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
