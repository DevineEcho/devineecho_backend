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
        // Step 1: 요청을 위한 RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();

        // Step 2: HTTP Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // Step 3: HTTP Body 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "405b1c838633f69a637a66b528da28a4"); // 카카오 REST API 키
        params.add("redirect_uri", "https://localhost:8090/api/auth/kakao/callback"); // 설정한 Redirect URI
        params.add("code", code);

        // Step 4: HttpEntity로 Header와 Body 합치기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        // Step 5: 카카오에 POST 요청 보내기
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token",
                kakaoTokenRequest,
                String.class
        );

        // Step 6: 응답 확인 (카카오에서 받은 Access Token 반환)
        String responseBody = response.getBody();
        if (responseBody == null) {
            return ResponseEntity.status(500).body("Failed to retrieve access token from Kakao.");
        }

        // Step 7: 사용자 정보를 가져오기 위해 Access Token 사용
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.add("Authorization", "Bearer " + extractAccessToken(responseBody));
        HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<String> userInfoResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                userInfoRequest,
                String.class
        );

        // Step 8: 사용자 정보 확인 및 처리
        String userInfo = userInfoResponse.getBody();
        if (userInfo == null) {
            return ResponseEntity.status(500).body("Failed to retrieve user info from Kakao.");
        }

        // 여기에서 사용자 정보(userInfo)를 바탕으로 회원가입 또는 로그인 처리
        String username = extractUsernameFromUserInfo(userInfo);
        Player player = playerService.findByUsername(username)
                .orElseGet(() -> registerNewKakaoUser(username));

        // JWT 토큰 발급 후 반환
        String token = jwtUtil.generateToken(player.getUsername());
        return ResponseEntity.ok(token);
    }

    private String extractAccessToken(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("access_token").asText(); // JSON의 "access_token" 값을 추출
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse access token from response", e);
        }
    }

    private String extractUsernameFromUserInfo(String userInfo) {
        try {
            JsonNode jsonNode = objectMapper.readTree(userInfo);
            return jsonNode.get("id").asText(); // JSON의 "id" 값을 추출 (카카오 고유 ID)
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse user info from response", e);
        }
    }

    private Player registerNewKakaoUser(String username) {
        Player player = new Player();
        player.setUsername(username);
        player.setPassword(""); // 카카오 사용자의 경우 비밀번호는 의미 없음
        playerService.savePlayer(player);
        return player;
    }
}
