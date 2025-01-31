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
    private int gold = 0;
    private int diamond = 0;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Item> inventory = new ArrayList<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> equippedSkills = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> purchasedSkills = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    private Item equippedCharacterSkin;

    @OneToOne(cascade = CascadeType.ALL)
    private Item equippedSkillSkin;

    @OneToOne(cascade = CascadeType.ALL)
    private Item equippedEnemySkin;

    public Player(String username, String phoneNumber) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public void encodeAndSetPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.password = passwordEncoder.encode(rawPassword);
    }

    public void initializeSecurityAnswer(String securityAnswer) {
        if (securityAnswer == null || securityAnswer.isEmpty()) {
            throw new IllegalArgumentException("Security answer cannot be null or empty");
        }
        this.securityAnswer = securityAnswer;
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
        this.health = health;
    }

    public void updateGold(int gold) {
        this.gold = gold;
    }

    public void updateDiamond(int diamond) {
        this.diamond = diamond;
    }

    public void equipDefaultSkills(List<Skill> defaultSkills) {
        this.equippedSkills.clear();
        this.equippedSkills.addAll(defaultSkills);
    }

    public void addItemToInventory(Item item) {
        this.inventory.add(item);
    }

    public void addSkill(Skill skill) {
        this.purchasedSkills.add(skill);
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
