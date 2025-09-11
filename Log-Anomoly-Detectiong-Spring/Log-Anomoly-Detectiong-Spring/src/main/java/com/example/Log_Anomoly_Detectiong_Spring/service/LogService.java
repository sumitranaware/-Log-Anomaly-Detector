package com.example.Log_Anomoly_Detectiong_Spring.service;


import com.example.Log_Anomoly_Detectiong_Spring.dto.AnomalyDto;
import com.example.Log_Anomoly_Detectiong_Spring.dto.DetectResultDto;
import com.example.Log_Anomoly_Detectiong_Spring.dto.LogRequest;
import com.example.Log_Anomoly_Detectiong_Spring.entity.LogEntry;
import com.example.Log_Anomoly_Detectiong_Spring.repository.LogRepository;
import com.example.Log_Anomoly_Detectiong_Spring.utility.LogFeatureExtractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class LogService {
    private final LogRepository repository;
    private final KafkaProducerService producerService;
    private final WebClient webClient;

    public LogService(LogRepository repository,
                      KafkaProducerService producerService,
                      WebClient.Builder builder) {
        this.repository = repository;
        this.producerService = producerService;
        this.webClient = builder.baseUrl("http://localhost:8000").build();
    }

    public LogEntry saveAndPublish(LogRequest req) {
        Instant eventTime = (req.getTimestamp() == null)
                ? Instant.now()
                : req.getTimestamp();

        LogEntry entry = new LogEntry(
                req.getServiceName(),
                req.getLogLevel(),
                req.getMessage(),
                eventTime
        );

        Map<String, Object> features = LogFeatureExtractor.extractFeatures(req);

        AnomalyDto anomalyRequest = new AnomalyDto();
        anomalyRequest.setServiceName(req.getServiceName());
        anomalyRequest.setLevel(req.getLogLevel());
        anomalyRequest.setMessage(req.getMessage());
        anomalyRequest.setTimestamp(eventTime.toString());
        anomalyRequest.setFeatures(features);

        try {
            DetectResultDto detectResult = webClient.post()
                    .uri("/detect")
                    .bodyValue(List.of(anomalyRequest))
                    .retrieve()
                    .bodyToMono(DetectResultDto.class)
                    .block();

            if (detectResult != null && !detectResult.getAnomalies().isEmpty()) {
                AnomalyDto anomaly = detectResult.getAnomalies().get(0);

                entry.setAnomalous(anomaly.isAnomaly());
                entry.setAnomalyScore(anomaly.getScore());
                entry.setAnomalyReason(
                        anomaly.isAnomaly() ? "IsolationForest anomaly detected" : null
                );
            }
        } catch (Exception e) {
            entry.setAnomalous(false);
            entry.setAnomalyReason("Detection service unavailable: " + e.getMessage());
        }

        LogEntry saved = repository.save(entry);
        producerService.publish(saved);
        return saved;
    }
    public Page<LogEntry> list(String serviceName, Pageable pageable) {
        if (serviceName == null || serviceName.isBlank()) {
            return repository.findAll(pageable);
        }
        return repository.findAllByServiceNameIgnoreCase(serviceName, pageable);
    }
}
