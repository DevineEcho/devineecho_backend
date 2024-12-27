package com.example.devineecho.controller;

import com.example.devineecho.dto.PlayerSignupRequest;
import com.example.devineecho.model.Player;
import com.example.devineecho.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody PlayerSignupRequest request) {
        // Player 객체 생성 및 필드 설정
        Player player = new Player(request.getUsername(), request.getPhoneNumber());
        authenticationService.signup(player, request.getPassword(), request.getSecurityAnswer());
        return ResponseEntity.ok("User registered successfully!");
    }



    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Player player) {
        return authenticationService.login(player);
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<String> kakaoCallback(@RequestParam String code) {
        return authenticationService.handleKakaoCallback(code);
    }
}
