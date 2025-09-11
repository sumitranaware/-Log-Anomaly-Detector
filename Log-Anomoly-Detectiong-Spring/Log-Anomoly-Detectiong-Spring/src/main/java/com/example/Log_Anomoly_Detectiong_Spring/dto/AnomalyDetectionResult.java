package com.example.Log_Anomoly_Detectiong_Spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnomalyDetectionResult {
    private boolean anomalous;
    private double score;
    private String reason;
}
