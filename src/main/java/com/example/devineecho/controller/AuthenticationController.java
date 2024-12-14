package com.example.devineecho.controller;

import com.example.devineecho.config.JwtUtil;
import com.example.devineecho.model.Player;
import com.example.devineecho.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final PlayerService playerService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @GetMapping("/kakao/callback")
    public ResponseEntity<String> kakaoCallback(@RequestParam String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "405b1c838633f69a637a66b528da28a4"); // 카카오 REST API 키
        params.add("redirect_uri", "https://localhost:8090/api/auth/kakao/callback"); // 설정한 Redirect URI
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token",
                kakaoTokenRequest,
                String.class
        );

        String responseBody = response.getBody();
        if (responseBody == null) {
            return ResponseEntity.status(500).body("Failed to retrieve access token from Kakao.");
        }


        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.add("Authorization", "Bearer " + extractAccessToken(responseBody));
        HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<String> userInfoResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                userInfoRequest,
                String.class
        );


        String userInfo = userInfoResponse.getBody();
        if (userInfo == null) {
            return ResponseEntity.status(500).body("Failed to retrieve user info from Kakao.");
        }


        String username = extractUsernameFromUserInfo(userInfo);
        Player player = playerService.findByUsername(username)
                .orElseGet(() -> registerNewKakaoUser(username));

        String token = jwtUtil.generateToken(player.getUsername());
        return ResponseEntity.ok(token);
    }

    private String extractAccessToken(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse access token from response", e);
        }
    }

    private String extractUsernameFromUserInfo(String userInfo) {
        try {
            JsonNode jsonNode = objectMapper.readTree(userInfo);
            return jsonNode.get("id").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse user info from response", e);
        }
    }

    private Player registerNewKakaoUser(String username) {
        Player player = new Player();
        player.setUsername(username);
        player.setPassword("");
        playerService.savePlayer(player);
        return player;
    }
}
