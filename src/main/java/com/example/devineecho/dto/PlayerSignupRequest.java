package com.example.devineecho.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerSignupRequest {
    private String username;
    private String phoneNumber;
    private String password;
    private String securityAnswer;
}
