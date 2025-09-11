package com.example.Log_Anomoly_Detectiong_Spring.repository;

import com.example.Log_Anomoly_Detectiong_Spring.entity.Anomaly;
import com.example.Log_Anomoly_Detectiong_Spring.entity.LogEntry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogEntry,Long> {
    Page<LogEntry> findAllByServiceNameIgnoreCase(String serviceName, Pageable pageable);
}
