package com.example.devineecho.model;

import lombok.Data;

@Data
public class StageCompleteRequest {
    private Long playerId;
    private int health;
    private int exp;
    private int stage;
}