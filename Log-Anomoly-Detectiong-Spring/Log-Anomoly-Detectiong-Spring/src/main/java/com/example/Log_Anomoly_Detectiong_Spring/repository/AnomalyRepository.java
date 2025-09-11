package com.example.Log_Anomoly_Detectiong_Spring.repository;

import com.example.Log_Anomoly_Detectiong_Spring.entity.Anomaly;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnomalyRepository extends JpaRepository<Anomaly,Long> {

}
