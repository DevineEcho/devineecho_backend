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
    private List<Skill> skills = new ArrayList<>();



    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> purchasedSkills = new ArrayList<>();


    @OneToOne(cascade = CascadeType.ALL)
    private Item equippedCharacterSkin; // 캐릭터 스킨

    @OneToOne(cascade = CascadeType.ALL)
    private Item equippedSkillSkin; // 스킬 스킨

    @OneToOne(cascade = CascadeType.ALL)
    private Item equippedEnemySkin; // 적 스킨

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

    public void addGold(int amount) {
        this.gold += amount;
    }

    public void subtractGold(int amount) {
        if (this.gold < amount) {
            throw new IllegalArgumentException("Not enough gold");
        }
        this.gold -= amount;
    }

    public void addDiamond(int amount) {
        this.diamond += amount;
    }

    public void subtractDiamond(int amount) {
        if (this.diamond < amount) {
            throw new IllegalArgumentException("Not enough diamonds");
        }
        this.diamond -= amount;
    }


    public void addItemToInventory(Item item) {
        this.inventory.add(item);
    }

    public void addSkill(Skill skill) {
        this.purchasedSkills.add(skill);
    }



    public void resetPlayerData() {
        this.level = 1;
        this.experience = 0;
        this.currentStage = 1;
        this.health = 100;
        this.gold = 0;
        this.diamond = 0;
        this.inventory.clear();
        this.purchasedSkills.clear();
        if (this.skills != null) {
            this.skills.clear();
        }
    }

    public void equipCharacterSkin(Item item) {
        if (!inventory.contains(item)) {
            throw new IllegalArgumentException("Item is not in the player's inventory.");
        }
        this.equippedCharacterSkin = item;
    }

    public void equipSkillSkin(Item item) {
        if (!inventory.contains(item)) {
            throw new IllegalArgumentException("Item is not in the player's inventory.");
        }
        this.equippedSkillSkin = item;
    }

    public void equipEnemySkin(Item item) {
        if (!inventory.contains(item)) {
            throw new IllegalArgumentException("Item is not in the player's inventory.");
        }
        this.equippedEnemySkin = item;
    }

    public void saveSkins(Item characterSkin, Item skillSkin, Item enemySkin) {
        equipCharacterSkin(characterSkin);
        equipSkillSkin(skillSkin);
        equipEnemySkin(enemySkin);
    }

    public List<Item> getOwnedItems() {
        return this.inventory;
    }




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



    public void updateStageProgress(int level, int additionalExperience, int newStage) {
        if (level <= 0 || additionalExperience < 0 || newStage <= 0) {
            throw new IllegalArgumentException("Invalid stage progress data");
        }
        this.level = level;
        this.experience += additionalExperience;
        this.currentStage = newStage;
    }
}
