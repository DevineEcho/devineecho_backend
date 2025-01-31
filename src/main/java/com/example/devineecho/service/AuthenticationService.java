package com.example.devineecho.service;

import com.example.devineecho.config.JwtUtil;
import com.example.devineecho.model.Player;
import com.example.devineecho.model.Skill;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthenticationService {

    private final PlayerService playerService;
    private final JwtUtil jwtUtil;
    private final SkillService skillService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(PlayerService playerService, SkillService skillService, JwtUtil jwtUtil,
                                  PasswordEncoder passwordEncoder) {
        this.playerService = playerService;
        this.skillService = skillService;
        this.jwtUtil = jwtUtil;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.passwordEncoder = passwordEncoder;
    }


    public void signup(Player player, String rawPassword, String securityAnswer) {
        player.encodeAndSetPassword(rawPassword, passwordEncoder);
        player.initializeSecurityAnswer(securityAnswer);


        List<Skill> defaultSkills = new ArrayList<>();
        defaultSkills.add(skillService.getSkillByName("HolyCircle"));
        defaultSkills.add(skillService.getSkillByName("SaintAura"));
        defaultSkills.add(skillService.getSkillByName("GodsHammer"));

        player.equipDefaultSkills(defaultSkills);

        playerService.savePlayer(player);
    }


    public ResponseEntity<String> login(Player player) {
        Player storedPlayer = (Player) playerService.loadUserByUsername(player.getUsername());
        if (storedPlayer == null) {
            throw new BadCredentialsException("User not found");
        }

        if (!passwordEncoder.matches(player.getPassword(), storedPlayer.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String jwt = jwtUtil.generateToken(storedPlayer.getUsername());
        return ResponseEntity.ok(jwt);
    }

    public ResponseEntity<String> handleKakaoCallback(String code) {
        String responseBody = fetchKakaoToken(code);
        String accessToken = extractAccessToken(responseBody);

        String userInfo = fetchKakaoUserInfo(accessToken);
        Map<String, String> userInfoMap = extractUserInfo(userInfo);

        String username = userInfoMap.get("username");
        String phoneNumber = userInfoMap.get("phoneNumber");

        Player player = playerService.findByUsername(username)
                .orElseGet(() -> registerNewKakaoUser(username, phoneNumber));

        String token = jwtUtil.generateToken(player.getUsername());
        return ResponseEntity.ok(token);
    }

    private String fetchKakaoToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "405b1c838633f69a637a66b528da28a4");
        params.add("redirect_uri", "https://localhost:8090/api/auth/kakao/callback");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token", request, String.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("Failed to retrieve access token from Kakao.");
        }
        return response.getBody();
    }

    private String fetchKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me", HttpMethod.GET, request, String.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("Failed to retrieve user info from Kakao.");
        }
        return response.getBody();
    }

    private String extractAccessToken(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse access token from response", e);
        }
    }

    private Map<String, String> extractUserInfo(String userInfo) {
        try {
            JsonNode jsonNode = objectMapper.readTree(userInfo);
            String username = jsonNode.get("id").asText();
            String phoneNumber = jsonNode.path("kakao_account").path("phone_number").asText();
            Map<String, String> userInfoMap = new HashMap<>();
            userInfoMap.put("username", username);
            userInfoMap.put("phoneNumber", phoneNumber);
            return userInfoMap;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse user info from response", e);
        }
    }

    private Player registerNewKakaoUser(String username, String phoneNumber) {
        Player player = new Player(username, phoneNumber);
        return playerService.savePlayer(player);
    }
}
