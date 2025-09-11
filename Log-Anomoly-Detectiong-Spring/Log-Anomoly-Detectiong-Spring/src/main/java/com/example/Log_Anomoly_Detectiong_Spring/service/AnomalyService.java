package com.example.Log_Anomoly_Detectiong_Spring.service;

import com.example.Log_Anomoly_Detectiong_Spring.entity.Anomaly;
import com.example.Log_Anomoly_Detectiong_Spring.entity.LogEntry;
import com.example.Log_Anomoly_Detectiong_Spring.repository.AnomalyRepository;
import com.example.Log_Anomoly_Detectiong_Spring.repository.LogRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AnomalyService {
    private final AnomalyRepository anomalyRepository;
    private final LogRepository logRepository;
    private final KafkaProducerService kafkaProducerService;

    public AnomalyService(AnomalyRepository anomalyRepository,
                          LogRepository logRepository,
                          KafkaProducerService kafkaProducerService) {
        this.anomalyRepository = anomalyRepository;
        this.logRepository = logRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Transactional
    public Anomaly markAnomalous(Long logId, Double score, String reason) {
        LogEntry logEntry = logRepository.findById(logId).orElseThrow();
        logEntry.setAnomalous(true);
        logEntry.setAnomalyScore(score);
        logEntry.setAnomalyReason(reason);
        logRepository.save(logEntry);

        Anomaly anomaly = anomalyRepository.save(new Anomaly(logId, score, reason));

        String message = String.format(" Anomaly detected -> LogId=%d | Score=%.4f | Reason=%s",
                logId, score != null ? score : 0.0, reason);
        kafkaProducerService.sendMessage(message);

        return anomaly;
    }
}
