package com.example.devineecho.controller;

import com.example.devineecho.dto.PlayerSignupRequest;
import com.example.devineecho.model.Player;
import com.example.devineecho.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        Player player = new Player(request.getUsername(), request.getPhoneNumber());
        authenticationService.signup(player, request.getPassword(), request.getSecurityAnswer());
        return ResponseEntity.ok("유저등록 완료");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Player player) {
        return authenticationService.login(player);
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<Map<String, String>> kakaoLogin(@RequestParam("code") String code) {
        return authenticationService.handleKakaoCallback(code);
    }

}
