package com.example.devineecho.controller;

import com.example.devineecho.config.JwtUtil;
import com.example.devineecho.model.Player;
import com.example.devineecho.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final PlayerService playerService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationController(PlayerService playerService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.playerService = playerService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Player player) {
        playerService.signup(player);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Player player) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(player.getUsername(), player.getPassword()));
            String token = jwtUtil.generateToken(player.getUsername());
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}