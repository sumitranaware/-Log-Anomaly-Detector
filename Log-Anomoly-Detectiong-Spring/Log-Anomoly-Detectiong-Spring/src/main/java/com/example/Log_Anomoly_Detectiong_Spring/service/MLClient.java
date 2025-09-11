package com.example.Log_Anomoly_Detectiong_Spring.service;

import com.example.Log_Anomoly_Detectiong_Spring.dto.AnomalyDto;
import com.example.Log_Anomoly_Detectiong_Spring.dto.DetectResultDto;
import com.example.Log_Anomoly_Detectiong_Spring.dto.LogRequest;
import com.example.Log_Anomoly_Detectiong_Spring.dto.AnomalyDetectionResult;
import com.example.Log_Anomoly_Detectiong_Spring.entity.LogEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class MLClient {

    private final WebClient webClient;
    private final String detectEndpoint;

    public MLClient(WebClient mlWebClient, @Value("${ml.service.url:http://localhost:8000/detect}") String mlUrl) {
        this.webClient = mlWebClient.mutate().baseUrl(mlUrl.replace("/detect","")).build();
        this.detectEndpoint = "/detect";
    }

    public AnomalyDetectionResult detectAnomaly(LogEntry logEntry) {
        try {
            LogRequest dto = new LogRequest();
            dto.setServiceName(logEntry.getServiceName());
            dto.setLogLevel(logEntry.getLogLevel());
            dto.setMessage(logEntry.getMessage());
            dto.setTimestamp(logEntry.getTimestamp());

            List<LogRequest> body = Collections.singletonList(dto);

            Mono<DetectResultDto> respMono = webClient.post()
                    .uri(detectEndpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(DetectResultDto.class);

            DetectResultDto result = respMono.block(); // blocking call - acceptable in standard MVC app

            if (result != null && result.getAnomalies() != null && !result.getAnomalies().isEmpty()) {
                AnomalyDto a = result.getAnomalies().get(0);
                double score = a.getScore() != null ? a.getScore() : (a.getRawScore() != null ? a.getRawScore() : 0.0);
                return new AnomalyDetectionResult(a.isAnomaly(), score, "Detected by ML service");
            }
            return new AnomalyDetectionResult(false, 0.0, "No anomalies returned");
        } catch (Exception e) {
            log.error("Error calling ML Service", e);
            return new AnomalyDetectionResult(false, 0.0, "ML service error: " + e.getMessage());
        }
    }
}
