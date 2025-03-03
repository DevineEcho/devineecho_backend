package com.example.devineecho.service;

import com.example.devineecho.config.JwtUtil;
import com.example.devineecho.model.Player;
import com.example.devineecho.model.Skill;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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
        
        Player savedPlayer = playerService.savePlayer(player);
        
        List<Skill> defaultSkills = skillService.getDefaultSkills(savedPlayer);
        List<Skill> equippedSkills = new ArrayList<>();

        for (Skill skill : defaultSkills) {
            savedPlayer.getPurchasedSkills().add(skill);
            if (equippedSkills.size() < 3) {
                equippedSkills.add(skill);
            }
        }

        savedPlayer.updateEquippedSkills(equippedSkills);
        playerService.savePlayer(savedPlayer);
    }

    public ResponseEntity<String> login(Player player) {
        Player storedPlayer = (Player) playerService.loadUserByUsername(player.getUsername());
        if (storedPlayer == null) {
            throw new BadCredentialsException("유저 확인 불가");
        }

        if (!passwordEncoder.matches(player.getPassword(), storedPlayer.getPassword())) {
            throw new BadCredentialsException("유효하지 않은 권한");
        }

        String jwt = jwtUtil.generateToken(storedPlayer.getUsername());
        return ResponseEntity.ok(jwt);
    }


    public ResponseEntity<Map<String, String>> handleKakaoCallback(@RequestParam("code") String code) {
        try {
            String responseBody = fetchKakaoToken(code);
            String accessToken = extractAccessToken(responseBody);
            String userInfo = fetchKakaoUserInfo(accessToken);

            String kakaoId = extractUserInfo(userInfo).get("kakaoId");
            Optional<Player> existingPlayer = playerService.findByKakaoId(kakaoId);

            Player player = existingPlayer.orElseGet(() -> registerNewKakaoUser(kakaoId, generateRandomPhoneNumber()));

            String token = jwtUtil.generateToken(player.getUsername());

            Map<String, String> responseBodyMap = new HashMap<>();
            responseBodyMap.put("token", token);
            responseBodyMap.put("username", player.getUsername());
            responseBodyMap.put("phoneNumber", player.getPhoneNumber());
            return ResponseEntity.ok(responseBodyMap);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }







    /**
     * 🔹 카카오 Access Token 요청
     */
    private String fetchKakaoToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "405b1c838633f69a637a66b528da28a4"); // ✅ 여기에 실제 카카오 앱 REST API 키 사용
        params.add("redirect_uri", "http://localhost:3000/login/kakao");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token", request, String.class
        );

        System.out.println("🔍 카카오 토큰 응답: " + response.getBody()); // ✅ 디버깅 출력 추가

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("카카오 토큰 요청 실패: " + response.getBody());
        }

        return response.getBody();
    }




    /**
     * 🔹 카카오 사용자 정보 요청
     */
    private String fetchKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me", HttpMethod.GET, request, String.class
        );

        System.out.println("🔍 카카오 사용자 정보 응답: " + response.getBody());

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("카카오 사용자 정보 요청 실패: " + response.getBody());
        }

        return response.getBody();
    }



    /**
     * 🔹 Access Token 추출
     */
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
            String kakaoId = jsonNode.get("id").asText();
            String phoneNumber = jsonNode.path("kakao_account").path("phone_number").asText();

            // 📌 랜덤 전화번호 생성 함수 호출
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                phoneNumber = generateRandomPhoneNumber();
            }

            Map<String, String> userInfoMap = new HashMap<>();
            userInfoMap.put("kakaoId", kakaoId);
            userInfoMap.put("phoneNumber", phoneNumber);
            return userInfoMap;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse user info from response", e);
        }
    }

    // ✅ 랜덤 전화번호 생성 함수 (010-XXXX-XXXX 형식)
    private String generateRandomPhoneNumber() {
        Random random = new Random();
        int num1 = random.nextInt(9000) + 1000; // 1000 ~ 9999
        int num2 = random.nextInt(9000) + 1000; // 1000 ~ 9999
        return "010-" + num1 + "-" + num2;
    }

    /**
     * 🔹 신규 카카오 회원 자동 등록
     */
    private Player registerNewKakaoUser(String kakaoId, String phoneNumber) {
        System.out.println("🚀 새로운 카카오 유저 생성 중...");

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            phoneNumber = generateRandomPhoneNumber();
        }

        String baseUsername = "Kakao_" + kakaoId.substring(0, 6);
        String uniqueUsername = baseUsername;
        int counter = 1;

        while (playerService.findByUsername(uniqueUsername).isPresent()) {
            uniqueUsername = "Kakao_" + kakaoId.substring(0, 4) + "_" + counter;
            counter++;
            if (counter > 1000) {
                throw new RuntimeException("Too many duplicate usernames.");
            }
        }

        String randomPassword = UUID.randomUUID().toString().substring(0, 12);
        String encodedPassword = passwordEncoder.encode(randomPassword);

        // ✅ 기본 securityAnswer 설정
        String defaultSecurityAnswer = "카카오 로그인";

        Player newPlayer = new Player(uniqueUsername, phoneNumber);
        newPlayer.updateKakaoId(kakaoId);
        newPlayer.updatePassword(randomPassword, passwordEncoder);
        newPlayer.initializeSecurityAnswer(defaultSecurityAnswer); // ✅ 기본값 추가

        Player savedPlayer = playerService.savePlayer(newPlayer);
        System.out.println("✅ 신규 카카오 유저 등록 완료: " + savedPlayer.getUsername());

        return savedPlayer;
    }





}
