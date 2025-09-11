package com.example.Log_Anomoly_Detectiong_Spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class AnomalyDetectionResponse {

    @JsonProperty("is_anomaly")
    private boolean anomaly;

    private double score;
    private String level;
    private String message;
    private String timestamp;

    private Map<String, Object> features; // keep flexible
}
