package com.example.Log_Anomoly_Detectiong_Spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class LogAnomolyDetectiongSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogAnomolyDetectiongSpringApplication.class, args);
		System.out.println("Application started...");

	}

}
