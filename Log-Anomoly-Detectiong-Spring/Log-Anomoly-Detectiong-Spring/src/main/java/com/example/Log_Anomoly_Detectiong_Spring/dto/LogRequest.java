package com.example.Log_Anomoly_Detectiong_Spring.dto;

import com.example.Log_Anomoly_Detectiong_Spring.utility.FlexibleTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogRequest {
    @NotBlank
    private String serviceName;

    @JsonProperty("level")
    private String logLevel;

    @NotBlank
    private String message;

    @JsonDeserialize(using = FlexibleTimeDeserializer.class)
    private Instant timestamp;
}
