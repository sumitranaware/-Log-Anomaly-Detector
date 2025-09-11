package com.example.Log_Anomoly_Detectiong_Spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;

@Data
public class AnomalyDto {
    @JsonProperty("is_anomaly")
    private boolean isAnomaly;

    private Double score;

    @JsonProperty("raw_score")
    private Double rawScore;

    private String serviceName;
    private String level;
    private String message;
    private String timestamp;

    private Map<String, Object> features;
}
