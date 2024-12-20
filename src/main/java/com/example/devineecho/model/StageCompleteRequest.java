package com.example.devineecho.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StageCompleteRequest {
    private Long playerId;
    private int level;
    private int exp;
    private int stage;
}
