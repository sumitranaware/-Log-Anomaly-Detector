package com.example.Log_Anomoly_Detectiong_Spring.controller;

import com.example.Log_Anomoly_Detectiong_Spring.dto.AnomalyDetectionResponse;
import com.example.Log_Anomoly_Detectiong_Spring.dto.LogRequest;
import com.example.Log_Anomoly_Detectiong_Spring.entity.Anomaly;
import com.example.Log_Anomoly_Detectiong_Spring.entity.LogEntry;
import com.example.Log_Anomoly_Detectiong_Spring.repository.AnomalyRepository;
import com.example.Log_Anomoly_Detectiong_Spring.repository.LogRepository;
import com.example.Log_Anomoly_Detectiong_Spring.service.AnomalyService;
import com.example.Log_Anomoly_Detectiong_Spring.service.LogService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api")
public class LogController {
    private final LogService logService;
    private final AnomalyService anomalyService;
    private final AnomalyRepository anomalyRepository;
    private final LogRepository logRepository;

    public LogController(LogService logService, AnomalyService anomalyService, AnomalyRepository anomalyRepository, LogRepository logRepository) {
        this.logService = logService;
        this.anomalyService = anomalyService;
        this.anomalyRepository = anomalyRepository;
        this.logRepository = logRepository;
    }

    @PostMapping("/logs")
    public List<LogEntry> ingest(@Valid @RequestBody List<LogRequest> request){
        return request.stream()
                .map(logService::saveAndPublish)
                .toList();
    }

    @GetMapping("/logs")
    public Page<LogEntry> list(
            @RequestParam(required = false) String serviceName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return logService.list(serviceName, PageRequest.of(page, size));
    }

    @PostMapping("/anomalies/mark/{logId}")
    public Anomaly mark(@PathVariable Long logId,
                        @RequestParam(required = false) Double score,
                        @RequestParam(required = false, defaultValue = "Anomaly detected") String reason) {
        return anomalyService.markAnomalous(logId, score, reason);
    }

    @GetMapping("/anomalies")
    public Page<Anomaly> anomalies(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size){
        return anomalyRepository.findAll(PageRequest.of(page,size));
    }

    @GetMapping("/health")
    public String health(){
        return "ok";
    }

//    @GetMapping("/logs/anomalies/live")
//    public Page<LogEntry> getLiveAnomalies(Pageable pageable) {
//        return logRepository.findAllByAnomalousTrue(pageable);
//    }

}
