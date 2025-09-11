package com.example.Log_Anomoly_Detectiong_Spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();

    }
    @Bean
    public ObjectMapper objectMapper(){
            ObjectMapper mapper=new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
        return mapper ;
    }

    @Bean
    public WebClient mlWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8000")
                .build();
    }
}
