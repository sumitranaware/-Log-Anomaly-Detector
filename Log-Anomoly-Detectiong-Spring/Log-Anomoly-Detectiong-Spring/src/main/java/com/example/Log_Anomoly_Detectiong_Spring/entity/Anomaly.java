package com.example.Log_Anomoly_Detectiong_Spring.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "anomaly_features")
public class Anomaly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isAnomaly;

    private Double score;
    private Double threshold;
    private String logId;
    private String timestamp;
    private String level;
    private String message;
    private String anomalyReason;

    @JdbcTypeCode(SqlTypes.JSON)   
    private Map<String, Object> features;

    public Anomaly(Long logId, Double score, String reason) {
    }
}
