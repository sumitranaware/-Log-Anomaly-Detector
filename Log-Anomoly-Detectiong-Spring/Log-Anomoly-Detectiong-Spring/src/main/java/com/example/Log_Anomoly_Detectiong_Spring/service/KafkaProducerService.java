package com.example.Log_Anomoly_Detectiong_Spring.service;

import com.example.Log_Anomoly_Detectiong_Spring.entity.LogEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, LogEntry> logKafkaTemplate;
    private final KafkaTemplate<String, String> stringKafkaTemplate;
    private final String logsTopic;
    private final String anomaliesTopic;

    public KafkaProducerService(KafkaTemplate<String, LogEntry> logKafkaTemplate,
                                KafkaTemplate<String, String> stringKafkaTemplate,
                                @Value("${app.kafka.logs-topic:logs-topic}") String logsTopic,
                                @Value("${app.kafka.anomalies-topic:anomalies}") String anomaliesTopic) {
        this.logKafkaTemplate = logKafkaTemplate;
        this.stringKafkaTemplate = stringKafkaTemplate;
        this.logsTopic = logsTopic;
        this.anomaliesTopic = anomaliesTopic;
    }

    public void publish(LogEntry log) {
        logKafkaTemplate.send(logsTopic, log.getServiceName(), log);
    }

    public void sendMessage(String message) {
        stringKafkaTemplate.send(anomaliesTopic, message);
    }
}
