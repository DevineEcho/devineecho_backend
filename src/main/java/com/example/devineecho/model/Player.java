package com.example.devineecho.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
        return List.of(); // 권한이 없으면 빈 리스트 반환
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 (true로 설정)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부 (true로 설정)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 여부 (true로 설정)
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부 (true로 설정)
    }
}
