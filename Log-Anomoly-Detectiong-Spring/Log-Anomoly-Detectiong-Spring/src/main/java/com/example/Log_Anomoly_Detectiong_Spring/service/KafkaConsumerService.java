package com.example.Log_Anomoly_Detectiong_Spring.service;

import com.example.Log_Anomoly_Detectiong_Spring.dto.AnomalyDetectionResult;
import com.example.Log_Anomoly_Detectiong_Spring.entity.LogEntry;
import com.example.Log_Anomoly_Detectiong_Spring.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final MLClient anomalyDetectionClient;
    private final LogRepository logRepository;
    private final AnomalyService anomalyService;

    @KafkaListener(topics = "${app.kafka.logs-topic:logs-topic}", groupId = "log-anomaly-group")
    public void consume(String message) {
        log.info("Received Kafka message: {}", message);

        try {
            String[] parts = message.split("\\|", 3);
            String serviceName = parts.length >= 1 ? parts[0] : "unknown";
            String logLevel = parts.length >= 2 ? parts[1] : "INFO";
            String logMessage = parts.length == 3 ? parts[2] : message;

            LogEntry entry = new LogEntry(serviceName, logLevel, logMessage, Instant.now());


            LogEntry saved = logRepository.save(entry);

            boolean anomaly = false;
            double score = 0.0;
            try {
                AnomalyDetectionResult result = anomalyDetectionClient.detectAnomaly(saved);
                anomaly = result.isAnomalous();
                score = result.getScore();
            } catch (Exception ex) {
                log.warn("Anomaly detection failed for log: {}", ex.getMessage());
            }

            if (anomaly) {
                anomalyService.markAnomalous(saved.getId(), score, "Auto-detected anomaly from ML service");
            } else {
                saved.setAnomalous(false);
                saved.setAnomalyScore(score);
                logRepository.save(saved);
            }

            log.info("Saved log entry [{}] with anomaly={}", saved.getId(), anomaly);

        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", message, e);
        }
    }
}
