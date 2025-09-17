package com.example.Log_Anomoly_Detectiong_Spring.entity;

import com.example.Log_Anomoly_Detectiong_Spring.utility.FlexibleTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String serviceName;

    @Column(name = "log_level")
    @JsonAlias("level")
    private String logLevel;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String message;

    @JsonDeserialize(using = FlexibleTimeDeserializer.class)
    private Instant timestamp;

    private Boolean anomalous = false;
    private Double anomalyScore;
    private String anomalyReason;

    public LogEntry() {}

    public LogEntry(String serviceName, String logLevel, String message, Instant timestamp) {
        this.serviceName = serviceName;
        this.logLevel = logLevel;
        this.message = message;
        this.timestamp = timestamp;
    }
}
