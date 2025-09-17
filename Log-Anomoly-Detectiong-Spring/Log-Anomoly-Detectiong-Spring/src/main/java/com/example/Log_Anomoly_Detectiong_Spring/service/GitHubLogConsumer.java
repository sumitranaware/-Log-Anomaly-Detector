//package com.example.Log_Anomoly_Detectiong_Spring.service;
//
//import com.example.Log_Anomoly_Detectiong_Spring.entity.LogEntry;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@Service
//public class GitHubLogConsumer {
//
//    private final ObjectMapper mapper = new ObjectMapper();
//    private final AnomalyDetectionClient anomalyDetectionClient;
//    private final LogService logService;
//
//    public GitHubLogConsumer(AnomalyDetectionClient anomalyDetectionClient, LogService logService) {
//        this.anomalyDetectionClient = anomalyDetectionClient;
//
//        this.logService = logService;
//    }
//
//    @KafkaListener(topics = "github-events", groupId = "github-log-group")
//    public void consume(String message) {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            LogEntry logEntry = mapper.readValue(message, LogEntry.class);
//            logService.processIncomingLog(logEntry);
//        } catch (Exception e) {
//            System.err.println("Failed to parse Kafka message: " + e.getMessage());
//        }
//    }
//
//}
