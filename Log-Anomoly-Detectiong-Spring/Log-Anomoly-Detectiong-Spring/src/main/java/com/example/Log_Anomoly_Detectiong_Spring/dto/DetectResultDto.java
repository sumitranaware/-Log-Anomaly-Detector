package com.example.Log_Anomoly_Detectiong_Spring.dto;

import lombok.Data;

import java.util.List;

@Data
public class DetectResultDto {
    private int count;
    private List<AnomalyDto> anomalies;
}
