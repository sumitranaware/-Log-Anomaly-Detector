package com.example.Log_Anomoly_Detectiong_Spring.service;

import com.example.Log_Anomoly_Detectiong_Spring.dto.AnomalyDetectionResponse;
import com.example.Log_Anomoly_Detectiong_Spring.dto.AnomalyDetectionResult;
import com.example.Log_Anomoly_Detectiong_Spring.dto.LogRequest;
import com.example.Log_Anomoly_Detectiong_Spring.entity.LogEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class AnomalyDetectionClient {
    private final RestTemplate restTemplate;
    private static final String ML_SERVICE_URL = "http://localhost:8000/detect";

    public AnomalyDetectionClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AnomalyDetectionResult detectAnomaly(LogEntry logEntry) {
        try {

            LogRequest dto = new LogRequest();
            dto.setServiceName(logEntry.getServiceName());
            dto.setLogLevel(logEntry.getLogLevel());
            dto.setMessage(logEntry.getMessage());
            dto.setTimestamp(logEntry.getTimestamp());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");

            List<LogRequest> entries = Collections.singletonList(dto);
            HttpEntity<List<LogRequest>> request = new HttpEntity<>(entries, headers);


            ResponseEntity<AnomalyDetectionResponse[]> response = restTemplate.exchange(
                    ML_SERVICE_URL,
                    HttpMethod.POST,
                    request,
                    AnomalyDetectionResponse[].class
            );

            if (response.getBody() != null && response.getBody().length > 0) {
                AnomalyDetectionResponse body = response.getBody()[0];
                return new AnomalyDetectionResult(
                        body.isAnomaly(),
                        body.getScore(),
                        "Detected by ML service"
                );
            }

            return new AnomalyDetectionResult(false, 0.0, "No response from ML service");

        } catch (Exception e) {
            log.error("Error calling ML Service", e);
            return new AnomalyDetectionResult(false, 0.0, "ML service error: " + e.getMessage());
        }
    }
}
